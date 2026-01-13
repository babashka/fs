(ns babashka.fs-cwd-test
  "Tests that are run from a scratch current working directory.
   Invoked via bb cwd-test."
  (:require
   [babashka.fs :as fs]
   [babashka.fs-test-util :as util]
   [babashka.test-report]
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [matcher-combinators.test]))

(use-fixtures :each
  (fn [f]
    (util/clean-cwd)
    (fs/create-dirs "da1/da2/da3/da4")
    (spit "f1.ext" "f1.ext")
    (spit "da1/da2/da3/da4/f2.ext" "f2.ext")
    (f)))

(defn- os []
  (let [os-name (str/lower-case (System/getProperty "os.name"))]
    (condp re-find os-name
      #"win" :win
      #"mac" :mac
      #"(nix|nux|aix)" :unix
      #"sunos" :solaris
      :unknown)))

(defn- path->str
  "Converts x to string unless x is nil"
  [x]
  (when-not (nil? x)
    (if (fs/windows?)
      ;; don't use fs/unixify as it also always converts to absolute path
      (cond->> (str/escape (str x) {\\ "/"})
        (fs/absolute? x) (str "/"))
      (str x))))

(defn- fsnapshot []
  (->> (#'fs/path-seq ".")
       (map (fn [p]
              {:path (fs/unixify (path->str p))
               :content (when (fs/regular-file? p)
                          (slurp (fs/file p)))
               :attr (dissoc (fs/read-attributes p "*") :lastAccessTime :fileKey)}))
       (sort-by :path)
       (into [])))

(defn jdk-major []
  (let [version (-> (System/getProperty "java.version")
                    (str/split #"\."))]
    (if (= "1" (first version))
      (Long/valueOf (second version))
      (Long/valueOf (first version)))))

;;
;; es- empty-string tests
;; Not all of these require an isolated scratch cwd, but they are grouped here for convenience
;; 
(deftest es-absolute?-test
  (is (= false (fs/absolute? ""))))

(deftest es-absolutize-test
  (is (= (path->str (System/getProperty "user.dir")) (path->str (fs/absolutize "")))))

(deftest es-canonicalize-test
  ;; There is windows bug in jdk24: https://bugs.openjdk.org/browse/JDK-8355342
  ;; that not only canonicalizes, but converts mapped drives to network drives.
  ;; This is slated to be fixed in jdk26, at the time of this writing, I don't see
  ;; backports planned.
  ;; This test will fail if running from a mapped drive on windows.
  (if (and (fs/windows?) (>= (jdk-major) 24) (str/starts-with? (path->str (fs/canonicalize "")) "//"))
    (throw (ex-info "due to bug JDK-8355342 in jdk24, please run this test on windows from an unmapped drive" {}))
    (is (= (path->str (System/getProperty "user.dir")) (path->str (fs/canonicalize ""))))))

(deftest es-components-test
  (is (= [""] (mapv path->str (fs/components "")))))

(deftest es-copy-test
  ;; returns the target 
  ;; as per javadoc: ...if the source and target are the same file... completes without copying the file
  (is (= "" (path->str (fs/copy "" ""))))
  ;; as per javadoc: ...if the source and target are the same file... completes without copying the file
  (is (= "f1.ext" (path->str (fs/copy "f1.ext" ""))))
  (is (thrown? java.nio.file.FileAlreadyExistsException (fs/copy "" "f1.ext")))
  (let [res (fs/copy "" "f1.ext" {:replace-existing true})]
    (is (= "f1.ext" (path->str res)))
    (is (= true (fs/directory? res)))))

(deftest es-copy-tree-test
  ;; returns the starting file
  ;; effectively copying self to self through tree so no-op
  (let [before (fsnapshot)]
    (is (= (fs/absolutize "") (fs/copy-tree "" "")))
    (is (match? before (fsnapshot)))))

(deftest es-dest-copy-tree-test
  ;; returns the starting file
  (let [res (fs/copy-tree "da1" "")]
    (is (= (fs/absolutize "da1") res))
    (is (= true (fs/exists? "da2/da3/da4/f2.ext")))))

(deftest es-src-copy-tree-test
  ;; returns the starting file
  ;; f1.ext is not a directory
  (is (thrown? java.lang.IllegalArgumentException (fs/copy-tree "" "f1.ext")))
  ;; no worky - can't copy parent to child, results in da1/da1/da1... file name too long:
  ;; (fs/copy-tree "" "da1") 
  )

(deftest es-create-dir-test
  (is (thrown? java.nio.file.FileAlreadyExistsException (fs/create-dir ""))))

(deftest es-create-dirs-test
  ;; dir already exists, no-op
  (is (= "" (path->str (fs/create-dirs "")))))

(deftest es-create-file-test
  ;; NOTE:
  ;; - JDK25 linux throws:  java.nio.file.FileAlreadyExistsException
  ;; - prior to that: java.lang.ArrayIndexOutOfBoundsException
  (is (thrown? Exception (fs/create-file ""))))

(when-not (fs/windows?)
  (deftest es-create-link-test
    (is (thrown? java.nio.file.FileSystemException (fs/create-link "" "")))
    ;; javadoc implies link is for files only (not directories):
    (is (thrown? java.nio.file.FileSystemException (fs/create-link "link" "")))
    ;; for comparison:
    (is (thrown? java.nio.file.FileSystemException (fs/create-link "link" ".")))))

(when-not (fs/windows?)
  (deftest es-create-sym-link-test
    ;; on macOS throws java.nio.file.FileAlreadyExistsException
    ;; on linux throws java.nio.file.NoSuchFileException
    (is (thrown? java.nio.file.FileSystemException (fs/create-sym-link "" "")))
    (when (= :linux (os))
      ;; linux bug? inconsistent: if "" is cwd, should be equivalent to (fs/create-sym-link "symlink1" ".") but throws:
      (is (thrown? java.nio.file.NoSuchFileException (fs/create-sym-link "symlink1" ""))))
    (when (= :mac (os))
      (fs/create-sym-link "symlink1" "")
      ;; link is created
      (is (fs/sym-link? "symlink1"))
      ;; but does not map to cwd 
      (is (not (fs/exists? "symlink1/f1.ext")))
      (is (not (fs/exists? "symlink1/da1/da2/da3/da4/f2.ext"))))
    ;; for comparison:
    (fs/create-sym-link "symlink2" ".")
    (is (fs/exists? "symlink2/f1.ext"))
    (is (fs/exists? "symlink2/da1/da2/da3/da4/f2.ext"))))

(deftest es-create-temp-dir-test
  (let [temp-dir (fs/create-temp-dir {:dir "" :prefix ""})]
    (is (re-matches #".+" (path->str temp-dir)))
    (is (= true (fs/exists? (fs/file-name temp-dir))))
    (is (= true (fs/directory? temp-dir)))))

(deftest es-create-temp-file-test
  (let [temp-file (fs/create-temp-file {:dir "" :prefix ""})]
    (is (re-matches #".+" (path->str temp-file)))
    (is (= true (fs/exists? (fs/file-name temp-file))))
    (is (= true (fs/regular-file? temp-file)))))

(deftest es-creation-time-test
  (let [dir-creation-time (fs/creation-time ".")]
    (is (= dir-creation-time (fs/creation-time "")))))

(deftest es-delete-test
  (let [before (fsnapshot)]
    ;; can't delete non-empty dir
    (is (thrown? java.nio.file.FileSystemException (fs/delete "")))
    (is (match? before (fsnapshot)))))

(deftest es-delete-if-exists-test
  (let [before (fsnapshot)]
    ;; can't delete non-empty dir
    (is (thrown? java.nio.file.FileSystemException (fs/delete-if-exists "")))
    (is (match? before (fsnapshot)))))

(deftest es-delete-on-exit-test
  ;; tested elsewhere, here we just check that it does not throw
  ;; NOTE: this does not seem to actually delete on exit, perhaps because the dir is in use?
  (fs/delete-on-exit ""))

(deftest es-delete-tree-test
  ;; although this throws, assumedly on attempting to delete cwd, it first deletes all files and subdirs
  (is (thrown? java.nio.file.FileSystemException (fs/delete-tree "")))
  (is (= true (fs/exists? "")))
  (is (= false (fs/exists? "f1.ext")))
  (is (= false (fs/exists? "da1/da2/da3/da4/f2.ext"))))

(deftest es-directory?-test
  (is (= true (fs/directory? ""))))

(deftest es-ends-with?-test
  (is (= true (fs/ends-with? "" ""))))

(deftest es-executable?-test
  ;; dir has +x so returns true
  (is (= true (fs/executable? ""))))

(deftest es-exists?-test
  (is (= true (fs/exists? ""))))

(deftest es-expand-home-test
  (is (= "" (path->str (fs/expand-home "")))))

(deftest es-extension-test
  (is (nil? (fs/extension ""))))

(deftest es-file-test
  (is (= "" (path->str (fs/file "")))))

(deftest es-file-name-test
  (is (= "" (fs/file-name ""))))

(deftest es-get-attribute-test
  (is (= true (fs/get-attribute "" "basic:isDirectory"))))

(deftest es-glob-test
  (is (= ["da1/da2/da3/da4/f2.ext"] (mapv path->str (fs/glob "" "**/f2.ext")))))

(deftest es-gunzip-test
  (let [last-modified (fs/last-modified-time "f1.ext")
        content (slurp "f1.ext")
        gzip (fs/gzip "f1.ext")]
    (is (thrown? java.nio.file.FileAlreadyExistsException (fs/gunzip gzip "")))
    (Thread/sleep 50)
    (fs/gunzip gzip "" {:replace-existing true})
    (is (not= last-modified (fs/last-modified-time "f1.ext")))
    (is (= content (slurp "f1.ext")))))

(deftest es-gzip-test
  ;; gzip expects a file, not a dir
  (is (thrown? java.io.FileNotFoundException (fs/gzip ""))))

(deftest es-hidden-test
  (if (and (not (fs/windows?)) (< (jdk-major) 17))
    (is (thrown? java.lang.ArrayIndexOutOfBoundsException (fs/hidden? "")))
    (is (= false (fs/hidden? "")))))

(deftest es-last-modified-time-test
  (let [dir-last-modified-time (fs/last-modified-time ".")]
    (is (= dir-last-modified-time (fs/last-modified-time "")))))

(deftest es-list-dir-test
  (is (= ["da1" "f1.ext"] (sort (mapv path->str (fs/list-dir ""))))))

(deftest es-list-dirs-test
  (is (= ["da1" "f1.ext"] (sort (mapv path->str (fs/list-dirs [""] "*"))))))

(deftest es-match-test
  (is (= ["da1/da2/da3/da4/f2.ext" "f1.ext"] (sort (mapv path->str (fs/match "" "regex:.*\\.ext" {:recursive true}))))))

(deftest es-modified-since
  (let [later (java.time.Instant/parse "2025-11-09T10:15:30.00Z")
        earlier (.minusSeconds later 1)]
    (fs/set-last-modified-time "" earlier)
    (fs/set-last-modified-time "f1.ext" later)
    (fs/set-last-modified-time "da1/da2/da3/da4/f2.ext" later)
    (is (= [] (fs/modified-since "" "")))
    (is (= [] (fs/modified-since "" "da1/da2/da3/da4/f2.ext")))
    (is (= [] (fs/modified-since "" "da1")))
    (is (= [] (fs/modified-since "da1/da2/da3/da4/f2.ext" "")))

    (fs/set-last-modified-time "" later)
    (fs/set-last-modified-time "f1.ext" later)
    (fs/set-last-modified-time "da1/da2/da3/da4/f2.ext" earlier)
    (is (= ["f1.ext"] (mapv path->str (fs/modified-since "da1/da2/da3/da4/f2.ext" ""))))))

(deftest es-move-test
  ;; returns target
  ;; as per javadoc should be no-op
  (let [before (fsnapshot)]
    (is (= "" (path->str (fs/move "" ""))))
    (is (match? before (fsnapshot)))))

(deftest es-src-move-test
  (let [before (fsnapshot)
        target (fs/create-temp-dir)]
    ;; Device or resource busy
    (is (thrown? java.nio.file.FileSystemException (fs/move "" (fs/file target "new-thing"))))
    (is (match? before (fsnapshot)))))

(deftest es-dest-move-test
  (is (= "da3" (path->str (fs/move "da1/da2/da3" ""))))
  (is (= true (fs/exists? "da3/da4/f2.ext")))
  (is (= false (fs/exists? "da1/da2/da3"))))

(deftest es-normalize-test
  (is (= "" (path->str (fs/normalize "")))))

(deftest es-owner-test
  (is (= (fs/owner "") (fs/owner "f1.ext"))))

(deftest es-parent-test
  ;; reminder: parent in path, not parent in filesystem
  (is (nil? (fs/parent ""))))

(deftest es-path-test
  (is (= "" (path->str (fs/path "")))))

(deftest es-read-all-bytes-test
  (is (thrown? java.io.IOException (fs/read-all-bytes ""))))

(deftest es-read-all-lines-test
  (is (thrown? java.io.IOException (fs/read-all-lines ""))))

(deftest es-read-attributes-test
  (is (= {:isDirectory true} (fs/read-attributes "" "basic:isDirectory"))))

(deftest es-read-attributes*-test
  (is (= {"isDirectory" true} (fs/read-attributes* "" "basic:isDirectory"))))

(when-not (fs/windows?)
  (deftest es-read-link-test
    (is (thrown? java.nio.file.NotLinkException (fs/read-link "")))))

(deftest es-readable?-test
  (is (= true (fs/readable? ""))))

(deftest es-real-path-test
  (is (= (path->str (System/getProperty "user.dir")) (path->str (fs/real-path "")))))

(deftest es-regular-file?-test
  (is (= false (fs/regular-file? ""))))

(deftest es-relative?-test
  (is (= true (fs/relative? ""))))

(deftest es-relativize-test
  (is (= "" (path->str (fs/relativize "" "")))))

(deftest es-same-file?-test
  (is (= true (fs/same-file? "" "")))
  (is (= true (fs/same-file? "." ""))))

(deftest es-set-attribute-test
  (let [new-time (fs/instant->file-time (java.time.Instant/parse "2025-11-10T01:02:03.00Z"))]
    (fs/set-attribute "" "basic:lastModifiedTime" new-time)
    (is (= new-time (fs/get-attribute "" "basic:lastModifiedTime")))))

(deftest es-set-creation-time-test
  ;; getting creation time on linux supported only since jdk22 and backported to jdk17 and jdk21
  ;; prior to that it returned last modified time
  ;; https://bugs.openjdk.org/browse/JDK-8316304
  ;; 
  ;; setting creation time on linux/macos not supported at this time
  ;; https://bugs.openjdk.org/browse/JDK-8151430
  (let [old-create-time (fs/creation-time "")
        new-create-time (fs/instant->file-time (java.time.Instant/parse "2025-11-10T01:02:01.00Z"))
        new-modify-time (fs/instant->file-time (java.time.Instant/parse "2025-11-10T01:02:10.00Z"))]
    (fs/set-creation-time "" new-create-time) ;; this is a no-op on linux
    (fs/set-last-modified-time "" new-modify-time)
    (cond
      ;; quite a storied history here
      ;; sometimes the correct creation time is returned
      (or (= :win (os))
          (and (= :mac (os)) (> (jdk-major) 17)))
      (is (= new-create-time (fs/creation-time "")) "returns correct new creation time")
      ;; other times the modified time is returned in place of creation time
      (or (= :mac os)
          (and (= :unix (os)) (< (jdk-major) 17)))
      (is (= new-modify-time (fs/creation-time "")) "returns new modified time")
      ;; other times old creation time is returned 
      :else
      (is (= old-create-time (fs/creation-time "")) "returns original creation time"))))

(deftest es-set-last-modified-time-test
  (let [old-time (fs/last-modified-time "")
        new-time (fs/instant->file-time (java.time.Instant/parse "2025-11-10T01:02:03.00Z"))]
    (fs/set-last-modified-time "" new-time)
    (is (not= old-time (fs/last-modified-time "")))
    (is (= new-time (fs/last-modified-time "")))))

(deftest es-set-posix-file-permission-test
  (when (not (fs/windows?))
    (let [old (fs/posix-file-permissions "")
          new "rwxrwxrwx"]
      (fs/set-posix-file-permissions "" new)
      (is (not= old (fs/posix->str (fs/posix-file-permissions ""))))
      (is (= new (fs/posix->str (fs/posix-file-permissions "")))))))

(deftest es-size-test
  ;; non-obvious, but size works on directories, per javadocs:
  ;; The size of files that are not regular files is implementation specific and therefore unspecified.
  (is (= (fs/size ".") (fs/size ""))))

(deftest es-split-ext-test
  (is (= ["" nil] (fs/split-ext ""))))

(deftest es-split-paths-test
  (is (= [""] (mapv path->str (fs/split-paths "")))))

(deftest es-starts-with?-test
  (is (= true (fs/starts-with? "" ""))))

(deftest es-strip-ext-test
  (is (= "" (fs/strip-ext ""))))

(deftest es-unixify-test
  (is (= "" (fs/unixify ""))))

(deftest es-zip-unzip-test
  (let [before (fsnapshot)
        ;; zip to temp-dir instead of cwd, so we don't zip our zip
        dest-zip (fs/file (fs/temp-dir) "foo.zip")]
    (fs/zip dest-zip "" {:root ""})
    (is (thrown? java.nio.file.FileAlreadyExistsException (fs/unzip dest-zip "")))
    (fs/unzip dest-zip "" {:replace-existing true})
    (is (match? (mapv #(update % :attr dissoc :creationTime :lastModifiedTime :lastAccessTime)
                      before)
                (mapv #(update % :attr dissoc :creationTime :lastModifiedTime :lastAccessTime)
                      (fsnapshot))))))

(deftest es-update-file-test
  ;; makes sense, "" is the cwd, not a regular file
  (is (thrown? java.io.FileNotFoundException (fs/update-file "" str "updated"))))

(deftest es-walk-file-tree-test
  (let [files (atom [])]
    (fs/walk-file-tree "" {:visit-file (fn [f _attrs]
                                         (swap! files conj f)
                                         :continue)})
    (is (= ["da1/da2/da3/da4/f2.ext" "f1.ext"] (sort (mapv path->str @files))))))

(deftest es-which-test
  (is (nil? (fs/which ""))))

(deftest es-which-all-test
  (is (= [] (fs/which-all ""))))

(deftest es-write-all-test
  (is (true? (fs/writable? ""))))

(deftest es-write-bytes-test
  (is (thrown? java.nio.file.FileSystemException (fs/write-bytes "" (.getBytes (String. "foo"))))))

(deftest es-write-lines-test
  (is (thrown? java.nio.file.FileSystemException (fs/write-lines "" ["foo"]))))

(deftest zip-should-not-zip-self-test
  (fs/create-dirs "foo/bar/baz")
  (spit "foo/bar/baz/somefile.txt" "bippity boo")
  (fs/zip "foo/zippy.zip" "foo")
  (fs/unzip "foo/zippy.zip" "zip-out")
  (is (match?
        ["foo/bar"
         "foo/bar/baz"
         "foo/bar/baz/somefile.txt"
         "foo/zippy.zip"]
        (->> (fs/glob "foo" "**") (mapv fs/unixify) sort))
      "sources and created zip file present")
  (is (not (fs/exists? "zip-out/foo/zippy.zip"))
      "zip file was not zipped")
  (is (match? ["zip-out/foo"
               "zip-out/foo/bar"
               "zip-out/foo/bar/baz"
               "zip-out/foo/bar/baz/somefile.txt"]
              (->> (fs/glob "zip-out" "**") (mapv fs/unixify) sort))
      "all files except zip file zipped"))

(deftest copy-tree-fails-on-parent-to-child-test
  (fs/create-dirs "foo/bar/baz")
  (spit "foo/bar/baz/somefile.txt" "bippity boo")
  (let [before (fsnapshot)]
    (is (= (fs/absolutize "foo") (fs/copy-tree "foo" "foo"))
        "copy to self is allowed and a no-op")
    (is (thrown-with-msg? Exception #"Cannot copy src directory: foo, under itself to dest: foo/new-dir"
                          (fs/copy-tree "foo" "foo/new-dir"))
        "copy to new dir under self throws")
    (is (thrown-with-msg? Exception #"Cannot copy src directory: foo, under itself to dest: foo/bar"
                          (fs/copy-tree "foo" "foo/bar"))
        "copy to existing dir under self throws")
    (is (= before (fsnapshot))
        "files/dirs are unchanged")
    (fs/copy-tree "foo" "foobar")
    (is (fs/exists? "foobar/bar/baz/somefile.txt"))))

(deftest copy-tree-ok-on-child-to-existing-parent-test
  (fs/create-dirs "foo/bar/baz")
  (spit "foo/bar/baz/somefile.txt" "bippity boo")
  (is (= (fs/absolutize "foo/bar") (fs/copy-tree "foo/bar" "foo"))
      "copy to dir above self to existing dir is fine")
  (is (match? ["foo/bar"
               "foo/bar/baz"
               "foo/bar/baz/somefile.txt"
               ;; our copied dir
               "foo/baz"
               "foo/baz/somefile.txt"]
              (->> (fs/glob "foo" "**") (mapv fs/unixify) sort))
      "child copied to parent"))

(deftest copy-tree-ok-on-child-to-new-parent-test
  (fs/create-dirs "foo/bar/baz")
  (spit "foo/bar/baz/somefile.txt" "bippity boo")
  (is (= (fs/absolutize "foo/bar/baz") (fs/copy-tree "foo/bar/baz" "foo/new-dir"))
      "copy to dir above self to new dir is fine")
  (is (match? ["foo/bar"
               "foo/bar/baz"
               "foo/bar/baz/somefile.txt"
               ;; our copied dir
               "foo/new-dir"
               "foo/new-dir/somefile.txt"]
              (->> (fs/glob "foo" "**") (mapv fs/unixify) sort))
      "child copied to parent"))

;; sl- symbolic link tests

(when-not (fs/windows?)
  ;;
  ;; create-dirs
  ;;
  (deftest sl-create-dirs-test
    (fs/create-dirs "dir1/dir2/dir3")
    (fs/create-sym-link "link-dir1" "dir1")
    (fs/create-sym-link "dir1/link-dir2" "dir2")
    (spit "dir1/file1.txt" "file1")
    (spit "dir1/dir2/file2.txt" "file2")
    (fs/create-sym-link "dir1/link-file1.txt" "file1.txt")
    (fs/create-sym-link "dir1/dir2/link-file2.txt" "file2.txt")
    (let [before (fsnapshot)]
      ;; no-ops, dirs exist
      (doseq [p ["link-dir1"
                 "dir1/link-dir2"
                 "link-dir1/link-dir2"
                 "link-dir1/dir2"]]
        (is (= (fs/path p) (fs/create-dirs p))
            (format "creating existing path %s does not throw" p)))

      ;; failures
      (doseq [p ["link-dir1/file1.txt"
                 "link-dir1/link-dir2/file2.txt"
                 "link-dir1/link-file1.txt"
                 "link-dir1/link-dir2/link-file2.txt"]]
        (is (thrown? java.nio.file.FileAlreadyExistsException (fs/create-dirs p))
            (format "create over existinf file %s throws" p)))

      (is (match? before (fsnapshot))
          "no changes expected for no-ops and throws"))

    ;; creates dirs with symlinks in parent path
    (doseq [[create-path                 expected-new-path]
            [["link-dir1/new1"           "dir1/new1"]
             ["dir1/link-dir2/new2"      "dir1/dir2/new2"]
             ["link-dir1/link-dir2/new3" "dir1/dir2/new3"]
             ["link-dir1/dir2/new4"      "dir1/dir2/new4"]]]
      (is (= (fs/path create-path) (fs/create-dirs create-path))
          "creates new dir when parent path has sym-links to dirs")
      (is (= true (fs/exists? expected-new-path))
          (format "new %s item exists" expected-new-path))
      (is (= true (fs/directory? expected-new-path {:nofollow-links true}))
          (format "new %s is directory" expected-new-path))))

  ;;
  ;; delete-tree
  ;; 
  (deftest sl-delete-tree-good-sym-link-root-test
    (fs/create-dirs "foo/bar/baz")
    (fs/create-sym-link "good-link" "foo")
    (fs/delete-tree "good-link")
    (is (= false (fs/exists? "good-link" {:nofollow-links true}))
        "link was deleted")
    (is (= true (fs/exists? "foo/bar/baz"))
        "link target was not deleted"))

  (deftest sl-delete-tree-bad-sym-link-root-test
    (fs/create-sym-link "bad-link" "bad-target")
    (fs/delete-tree "bad-link")
    (is (= false (fs/exists? "bad-link" {:nofollow-links true}))))

  ;;
  ;; copy-tree
  ;;
  (deftest sl-copy-tree-nofollow-src-link-throws-test
    (fs/create-dirs "src-dir/bar/baz")
    (spit "src-dir/bar/baz/somefile.txt" "bippity boo")
    (fs/create-sym-link "link-src-dir" "src-dir")

    (is (thrown-with-msg? IllegalArgumentException #"Not a directory: link-src-dir"
                          (fs/copy-tree "link-src-dir" "dest-dir" {:nofollow-links true}))))

  (deftest sl-copy-tree-nofollow-dest-link-throws-test
    (fs/create-dirs "src-dir/bar/baz")
    (spit "src-dir/bar/baz/somefile.txt" "bippity boo")
    (fs/create-sym-link "link-dest-dir" "dest-dir")
    (is (thrown-with-msg? IllegalArgumentException #"Not a directory: link-dest-dir"
                          (fs/copy-tree "src-dir" "link-dest-dir" {:nofollow-links true}))))

  (deftest sl-copy-tree-follow-src-dest-links-test
    (fs/create-dirs "src-dir/src-bar/src-baz")
    (fs/create-dirs "dest-dir/dest-bar/dest-baz")
    (spit "src-dir/src-bar/src-baz/src-file.txt" "src-file")
    (spit "dest-dir/dest-bar/dest-baz/dest-file.txt" "dest-file")
    (fs/create-sym-link "link-src-dir" "src-dir")
    (fs/create-sym-link "link-dest-dir" "dest-dir")

    (is (= (fs/real-path "link-src-dir") (fs/copy-tree "link-src-dir" "link-dest-dir")))
    (is (fs/exists? "dest-dir/dest-bar/dest-baz/dest-file.txt")
        "existing dest-file.txt still exists")
    (is (fs/exists? "dest-dir/src-bar/src-baz/src-file.txt")
        "src-file.txt was copied to existing dest-dir"))

  (deftest sl-copy-tree-follow-src-link-new-dest-test
    (fs/create-dirs "src-dir/bar/baz")
    (spit "src-dir/bar/baz/somefile.txt" "bippity boo")
    (fs/create-sym-link "link-src-dir" "src-dir")

    (is (= (fs/real-path "link-src-dir") (fs/copy-tree "link-src-dir" "new-dest-dir")))
    (is (fs/exists? "new-dest-dir/bar/baz/somefile.txt")
        "src-file.txt was copied to new dest dir"))

  ;;
  ;; move
  ;; 
  (deftest sl-move-bad-link-to-bad-link-test
    (fs/create-sym-link "bad-link1" "bad-target1")
    (fs/create-sym-link "bad-link2" "bad-target2")

    (fs/move "bad-link1" "bad-link2" {:replace-existing true})

    (is (= false (fs/exists? "bad-link1" {:nofollow-links true})))
    (is (= (fs/path "bad-target1") (fs/read-link "bad-link2"))))

  (deftest sl-move-good-link-to-good-link-test
    (fs/create-dir "dir1")
    (fs/create-dir "dir2")
    (fs/create-sym-link "good-link1" "dir1")
    (fs/create-sym-link "good-link2" "dir2")

    (fs/move "good-link1" "good-link2" {:replace-existing true})

    (is (= false (fs/exists? "good-link1" {:nofollow-links true})))
    (is (= true (fs/directory? "good-link2"))) ;; via link follow
    (is (= (fs/path "dir1") (fs/read-link "good-link2"))))

  (deftest sl-move-good-link-to-good-link-no-replace-test
    (fs/create-dir "dir1")
    (fs/create-dir "dir2")
    (fs/create-sym-link "good-link1" "dir1")
    (fs/create-sym-link "good-link2" "dir2")

    (fs/move "good-link1" "good-link2" {:replace-existing true})

    (is (= false (fs/exists? "good-link1" {:nofollow-links true})))
    (is (= true (fs/directory? "good-link2"))) ;; via link follow
    (is (= (fs/path "dir1") (fs/read-link "good-link2"))))

  (deftest sl-move-good-link-under-dir-test
    (fs/create-dir "dir1")
    (fs/create-dir "dir2")
    (fs/create-sym-link "good-link1" "dir1")

    (fs/move "good-link1" "dir2")

    (is (= false (fs/exists? "good-link1" {:nofollow-links true})))
    (is (= true (fs/exists? (fs/path "dir2" "good-link1") {:nofollow-links true})))
    ;; moved relative link is now broken
    (is (= false (fs/exists? (fs/path "dir2" "good-link1")))) ;; via follow
    (is (= (fs/path "dir1") (fs/read-link (fs/path "dir2" "good-link1")))))

  (deftest sl-move-file-to-to-good-link-test
    (spit "file1.txt" "foo")
    (fs/create-dir "dir1")
    (fs/create-sym-link "good-link1" "dir1")

    (fs/move "file1.txt" "good-link1" {:replace-existing true})
    (is (= false (fs/exists? "file1.txt" {:nofollow-links true})))
    (is (= true (fs/exists? "good-link1" {:nofollow-links true})))
    (is (= false (fs/sym-link? "good-link1"))))

  (deftest sl-move-good-link-to-file-test
    (spit "file1.txt" "foo")
    (fs/create-dir "dir1")
    (fs/create-sym-link "good-link1" "dir1")

    (fs/move "good-link1" "file1.txt" {:replace-existing true})
    (is (= false (fs/exists? "good-link1" {:nofollow-links true})))
    (is (= true (fs/exists? "file1.txt" {:nofollow-links true})))
    (is (= true (fs/sym-link? "file1.txt"))))

  (deftest sl-rename-good-link-test
    (fs/create-dir "dir1")
    (fs/create-sym-link "good-link1" "dir1")

    (fs/move "good-link1" "good-link2")
    (is (= false (fs/exists? "good-link1" {:nofollow-links true})))
    (is (= true (fs/exists? "good-link2" {:nofollow-links true})))
    (is (= true (fs/sym-link? "good-link2")))
    (is (= (fs/path "dir1") (fs/read-link "good-link2"))))

  (deftest sl-move-link-without-replace-test
    (fs/create-dir "dir1")
    (fs/create-dir "dir2")
    (fs/create-sym-link "good-link1" "dir1")
    (fs/create-sym-link "good-link2" "dir2")

    (is (thrown-with-msg? java.nio.file.FileAlreadyExistsException #"good-link2" (fs/move "good-link1" "good-link2")))))

;; misc -- will merge into fs-test as part of https://github.com/babashka/fs/issues/158

(deftest gzip-arg-types-test
  (spit "foo.txt" "foo")
  (doseq [arg-type [:str :file :path]]
    (testing (str "args type: " (name arg-type))
      (let [arg-fn (arg-type {:str identity :file fs/file :path fs/path})]
        (is (= (str (fs/path "./foo.txt.gz"))
               (fs/gzip (arg-fn "foo.txt"))))
        (is (= (str (fs/path "out-dir" "foo.txt.gz"))
               (fs/gzip (arg-fn "foo.txt") {:dir (arg-fn "out-dir")})))
        (is (= (str (fs/path "out-dir" "bar.txt.gz"))
               (fs/gzip (arg-fn "foo.txt") {:dir (arg-fn "out-dir") :out-file "bar.txt.gz"})))))))
