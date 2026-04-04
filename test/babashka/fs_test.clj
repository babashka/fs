(ns babashka.fs-test
  (:require
   [babashka.fs :as fs]
   [babashka.fs-test-util :as util]
   [babashka.process :as process]
   [babashka.test-report]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [matcher-combinators.test])
  (:import
   [java.io FileNotFoundException]
   [java.nio.file Files FileAlreadyExistsException FileSystemException LinkOption]
   [java.nio.file.attribute FileTime]))

(set! *warn-on-reflection* true)

(use-fixtures :each
  (fn [f]
    (util/clean-cwd)
    (f)))

(def windows? (-> (System/getProperty "os.name")
                  (str/lower-case)
                  (str/starts-with? "windows")))

(def cant-set-last-modified-time-on-sym-link?
  "Some os jdk combos have bugs in that they do not allow setting last modfieid time on a symbolic link"
  (or (and (= :unix (util/os)) (= 11 (util/jdk-major)))
      (and (= :mac (util/os)) (#{11 21} (util/jdk-major)))))

(defn- files [& files]
  (doseq [f files]
    (if (str/ends-with? f "/")
      (fs/create-dirs f)
      (let [d (fs/parent f)]
        (when d (fs/create-dirs d))
        (spit f f)))))

(defn- normalized
  "Return sorted file `entries` as strings with dirs annotated with distinguishing trailing /.
  For example this [file1.txt dir1 dir1/dir2 dir1/dir2/dir3 dir1/dir2/dir3/file2.txt dir4]
  Becomes [file1.txt dir1/ dir1/dir2/ dir1/dir2/dir3/ dir1/dir2/dir3/file2.txt dir4/]

  Optionally specify:
  - `:collapse true` also collapses dirs like so: [dir1/dir2/dir3/file2.txt dir4/ file1.txt]
  - `:relativize <some-dir>` to `fs/relativize` files to `<some-dir>`
  - `:normalize true` to `fs/normalize` files."
  ([entries]
   (normalized entries {}))
  ([entries {:keys [collapse relativize normalize]}]
   (let [pre (cond->> entries
               relativize (map #(fs/relativize (fs/absolutize relativize) (fs/absolutize %)))
               normalize (map fs/normalize))
         sorted (->> pre
                     (map fs/unixify)
                     (map #(if (fs/directory? %)
                                  (str % "/")
                                  %))
                     sort)]
     (if-not collapse
       sorted
       (->> sorted
            reverse
            (reduce (fn [acc n]
                      (if (and (seq acc) (fs/starts-with? (last acc) n))
                        acc
                        (conj acc n)))
                    [])
            sort)))))

(defn- list-tree
  "Return [[normalized]] collapsed file entries under `root-dir`"
  [root-dir]
  (-> (fs/glob root-dir "**" {:hidden true})
      (normalized {:collapse true :normalize true})))

(defn- create-zip-file
  "Create zip `filename` with `zip-entries` preserving order of `zip-entries`."
  [^String filename zip-entries]
  (with-open [zos (java.util.zip.ZipOutputStream. (java.io.FileOutputStream. filename))]
    (doseq [[^String name ^String content] zip-entries]
      (.putNextEntry zos (java.util.zip.ZipEntry. name))
      (when content
        (let [bytes (.getBytes content)]
          (.write zos bytes 0 (count bytes))))
      (.closeEntry zos))))

(defn- file-time [date-string]
  (java.nio.file.attribute.FileTime/from
   (java.time.Instant/parse date-string)))

(defn- file-time-recently
  []
  (FileTime/from (.minusMillis (java.time.Instant/now) 100)))

;;
;; Tests are organized alphabetically by bb fs API fn name.
;; (with some minor exceptions where it makes sense, ex.: *posix* fns are all grouped together)
;;

;;
;; absolute?
;;
(deftest absolute?-empty-string-test
  (is (= false (fs/absolute? ""))))

;;
;; absolutize
;;
(deftest absolutize-empty-string-test
  (is (= (util/path->str (System/getProperty "user.dir")) (util/path->str (fs/absolutize "")))))

;;
;; canonicalize
;;
(deftest canonicalize-sym-link-test
  (files "file")
  (fs/create-sym-link "link" "file")
  (if (and (= :win (util/os)) (< (util/jdk-major) 24))
    ;; https://bugs.openjdk.org/browse/JDK-8003887: windows does not follow sym-links prior to JDK 24
    ;; test errant behaviour to learn if JDK team ever backports fix to earlier versions
    (is (= (fs/path (fs/cwd) "link")
           (fs/canonicalize "link/foo/..")
           (fs/canonicalize "link/foo/.." {:nofollow-links false}))
        "following link does not work due to JDK bug")
    (is (= (fs/path (fs/cwd) "file")
           (fs/canonicalize "link/foo/..")
           (fs/canonicalize "link/foo/.." {:nofollow-links false}))
        "following link"))
  (is (= (fs/path (fs/cwd) "link") (fs/canonicalize "link/foo/.." {:nofollow-links true}))
      "not following link"))

(deftest canonicalize-empty-string-test
  ;; There is windows bug in jdk24: https://bugs.openjdk.org/browse/JDK-8355342
  ;; that not only canonicalizes, but converts mapped drives to network drives.
  ;; This is slated to be fixed in jdk26, at the time of this writing, I don't see
  ;; backports planned.
  ;; This test will fail if running from a mapped drive on windows.
  (if (and (fs/windows?) (>= (util/jdk-major) 24) (str/starts-with? (util/path->str (fs/canonicalize "")) "//"))
    (throw (ex-info "due to bug JDK-8355342 in jdk24, please run this test on windows from an unmapped drive" {}))
    (is (= (util/path->str (System/getProperty "user.dir")) (util/path->str (fs/canonicalize ""))))))

;;
;; components
;;
(deftest components-test
  (is (match? ["foo" "bar" "baz" "bop.txt"]
              (map str (fs/components "foo/bar/baz/bop.txt")))))

(deftest components-empty-string-test
  (is (= [""] (mapv util/path->str (fs/components "")))))

;;
;; copy
;; 
(deftest copy-to-file-test
  (files "file" "dest-dir/")
  (fs/copy "file" "dest-dir/file")
  (is (match? ["dest-dir/file"
               "file"]
              (list-tree "."))))

(deftest copy-into-dir-test
  (files "file" "dest-dir/")
  (fs/copy "file" "dest-dir")
  (is (match? ["dest-dir/file"
               "file"]
              (list-tree "."))))

(deftest copy-input-stream-test
  (files "file" "dest-dir/")
  (with-open [is (io/input-stream (fs/file "file"))]
    (fs/copy is "dest-dir/file"))
  (is (match? ["dest-dir/file"
               "file"]
              (list-tree "."))))

(deftest copy-empty-string-test
  (files "f1.ext")
  ;; returns the target 
  ;; as per javadoc: ...if the source and target are the same file... completes without copying the file
  (is (= "" (util/path->str (fs/copy "" ""))))
  (is (= "f1.ext" (util/path->str (fs/copy "f1.ext" ""))))
  (is (thrown? java.nio.file.FileAlreadyExistsException (fs/copy "" "f1.ext")))
  (let [res (fs/copy "" "f1.ext" {:replace-existing true})]
    (is (= "f1.ext" (util/path->str res)))
    (is (= true (fs/directory? res)))))

;;
;; copy-tree
;;
(deftest copy-tree-test
  (files "src-dir/.foo"
         "src-dir/a/a.txt"
         "src-dir/a/b/b.txt"
         "src-dir/a/b/c"
         "src-dir/foo.txt")
  (fs/copy-tree "src-dir" "dest-dir")
  (is (match? ["dest-dir/.foo"
               "dest-dir/a/a.txt"
               "dest-dir/a/b/b.txt"
               "dest-dir/a/b/c"
               "dest-dir/foo.txt"]
              (list-tree "dest-dir"))))

(deftest copy-tree-from-file-throws-test
  (files "src-dir/dir/file.txt" "dest-dir/")
  (let [before (util/fsnapshot)]
    ;; cf. python3 -c 'import shutil; shutil.copytree("foo/bar1", "foo2")'
    (is (thrown-with-msg? IllegalArgumentException #"Not a directory"
                          (fs/copy-tree "src-dir/dir/file.txt" "dest-dir")))
    (is (match? before (util/fsnapshot)))))

(deftest copy-tree-to-file-throws-test
  (files "src-dir/dir/file.txt" "dest-dir/file.txt")
  (let [before (util/fsnapshot)]
    (is (thrown-with-msg? IllegalArgumentException #"Not a directory"
                          (fs/copy-tree "src-dir/dir" "dest-dir/file.txt")))
    (is (match? before (util/fsnapshot)))))

(deftest copy-tree-creates-missing-dest-dirs-test
  (files "src-dir/foo/file.txt"
         "dest-dir/")
  ;; https://github.com/babashka/fs/issues/42
  ;; foo2 doesn't exist
  (fs/copy-tree "src-dir/foo" "dest-dir/foo2/foo")
  (is (match? ["dest-dir/foo2/foo/file.txt"
               "src-dir/foo/file.txt"]
              (list-tree "."))))

(deftest copy-tree-nested-ro-dir-test
  (files "src-dir/foo/bar/")
  ;; https://github.com/babashka/fs/issues/122
  (.setReadOnly (fs/file "src-dir" "foo"))
  (fs/copy-tree  "src-dir" "dest-dir")
  (is (match? ["dest-dir/foo/bar/"
               "src-dir/foo/bar/"]
              (list-tree ".")))
  (when (not windows?)
    ;; you can always write to directories on Windows, even if they are read-only
    ;; https://answers.microsoft.com/en-us/windows/forum/all/all-folders-are-now-read-only-windows-10/0ca1880f-e997-46af-bd85-042a53fc078e
    (is (not (fs/writable? "dest-dir/foo")))))

(deftest copy-tree-fails-on-parent-to-child-test
  (files "foo/bar/baz/somefile.txt")
  (let [before (util/fsnapshot)]
    (is (= (fs/absolutize "foo") (fs/copy-tree "foo" "foo"))
        "copy to self is allowed and a no-op")
    (is (thrown-with-msg? Exception #"Cannot copy src directory: foo, under itself to dest: foo/new-dir"
                          (fs/copy-tree "foo" "foo/new-dir"))
        "copy to new dir under self throws")
    (is (thrown-with-msg? Exception #"Cannot copy src directory: foo, under itself to dest: foo/bar"
                          (fs/copy-tree "foo" "foo/bar"))
        "copy to existing dir under self throws")
    (is (= before (util/fsnapshot))
        "files/dirs are unchanged")
    (fs/copy-tree "foo" "foobar")
    (is (fs/exists? "foobar/bar/baz/somefile.txt"))))

(deftest copy-tree-ok-on-child-to-existing-parent-test
  (files "foo/bar/baz/somefile.txt")
  (spit "foo/bar/baz/somefile.txt" "bippity boo")
  (is (= (fs/absolutize "foo/bar") (fs/copy-tree "foo/bar" "foo"))
      "copy to dir above self to existing dir is fine")
  (is (match? ["foo/bar/baz/somefile.txt"
               ;; our copied dir
               "foo/baz/somefile.txt"]
              (list-tree "foo"))
      "child copied to parent"))

(deftest copy-tree-ok-on-child-to-new-parent-test
  (files "foo/bar/baz/somefile.txt")
  (is (= (fs/absolutize "foo/bar/baz") (fs/copy-tree "foo/bar/baz" "foo/new-dir"))
      "copy to dir above self to new dir is fine")
  (is (match? ["foo/bar/baz/somefile.txt"
               ;; our copied dir
               "foo/new-dir/somefile.txt"]
              (list-tree "foo"))
      "child copied to parent"))

(deftest copy-tree-empty-string-test
  ;; returns the starting file
  ;; effectively copying self to self through tree so no-op
  (let [before (util/fsnapshot)]
    (is (= (fs/absolutize "") (fs/copy-tree "" "")))
    (is (match? before (util/fsnapshot)))))

(deftest copy-tree-dest-empty-string-test
  (files "da1/da2/da3/da4/f2.txt")
  ;; returns the starting file
  (let [res (fs/copy-tree "da1" "")]
    (is (= (fs/absolutize "da1") res))
    (is (match? ["da1/da2/da3/da4/f2.txt"
                 "da2/da3/da4/f2.txt"]
                (list-tree ".")))))

(deftest copy-tree-src-empty-string-test
  (files "f1.ext")
  (files "da1/da2/da3/f2.txt")
  (is (thrown-with-msg? java.lang.IllegalArgumentException #"Not a directory.*" (fs/copy-tree "" "f1.ext")))
  (is (thrown-with-msg? Exception #"Cannot copy.*under itself" (fs/copy-tree "" "da1"))))

(deftest copy-tree-nofollow-src-link-throws-sym-link-test
  (files "src-dir/bar/baz/somefile.txt")
  (fs/create-sym-link "link-src-dir" "src-dir")
  (is (thrown-with-msg? IllegalArgumentException #"Not a directory: link-src-dir"
                        (fs/copy-tree "link-src-dir" "dest-dir" {:nofollow-links true}))))

(deftest copy-tree-nofollow-dest-link-throws-sym-link-test
  (files "src-dir/bar/baz/somefile.txt")
  (fs/create-sym-link "link-dest-dir" "dest-dir")
  (is (thrown-with-msg? IllegalArgumentException #"Not a directory: link-dest-dir"
                        (fs/copy-tree "src-dir" "link-dest-dir" {:nofollow-links true}))))

(deftest copy-tree-follow-src-dest-links-sym-link-test
  (files "src-dir/src-bar/src-baz/src-file.txt"
         "dest-dir/dest-bar/dest-baz/dest-file.txt")
  (fs/create-sym-link "link-src-dir" "src-dir")
  (fs/create-sym-link "link-dest-dir" "dest-dir")
  (is (= (fs/real-path "link-src-dir") (fs/copy-tree "link-src-dir" "link-dest-dir")))
  (is (match? ["dest-dir/dest-bar/dest-baz/dest-file.txt"
               "dest-dir/src-bar/src-baz/src-file.txt"
               "link-dest-dir/"
               "link-src-dir/"
               "src-dir/src-bar/src-baz/src-file.txt"]
              (list-tree "."))))

(deftest copy-tree-follow-src-link-new-dest-sym-link-test
  (files "src-dir/bar/baz/somefile.txt")
  (fs/create-sym-link "link-src-dir" "src-dir")
  (is (= (fs/real-path "link-src-dir") (fs/copy-tree "link-src-dir" "new-dest-dir")))
  (is (match? ["link-src-dir/"
               "new-dest-dir/bar/baz/somefile.txt"
               "src-dir/bar/baz/somefile.txt"]
              (list-tree "."))))

;;
;; create-dir
;;
(deftest create-dir-test
  (is (fs/create-dir "foo"))
  (is (match? ["foo/"] (normalized
                         (fs/glob "." "**"))))
  (is (fs/directory? "foo")))

(deftest create-dir-empty-string-test
  (is (thrown? java.nio.file.FileAlreadyExistsException (fs/create-dir ""))))

;;
;; create-dirs
;;
(deftest create-dirs-empty-string-test
  ;; dir already exists, no-op
  (is (= "" (util/path->str (fs/create-dirs "")))))

(deftest create-dirs-sym-link-test
  (files "dir1/dir2/dir3/"
         "dir1/file1.txt"
         "dir1/dir2/file2.txt")
  (fs/create-sym-link "link-dir1" "dir1")
  (fs/create-sym-link "dir1/link-dir2" "dir2")
  (fs/create-sym-link "dir1/link-file1.txt" "file1.txt")
  (fs/create-sym-link "dir1/dir2/link-file2.txt" "file2.txt")
  (let [before (util/fsnapshot)]
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

    (is (match? before (util/fsnapshot))
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
;; create-file
;;
(deftest create-file-empty-string-test
  ;; NOTE:
  ;; - JDK25 linux throws:  java.nio.file.FileAlreadyExistsException
  ;; - prior to that: java.lang.ArrayIndexOutOfBoundsException
  (is (thrown? Exception (fs/create-file ""))))

;;
;; create-link
;;
(deftest create-link-test
  (files "dudette.txt")
  (let [link (fs/create-link "hard-link.txt" "dudette.txt")]
    (is (= "hard-link.txt" (str link)))
    (is (match? ["dudette.txt"
                 "hard-link.txt"]
                (list-tree ".")))
    (when (not windows?)
      ;; an attribute check is not available on Windows
      (is (= 2 (fs/get-attribute link "unix:nlink"))))
    (is (= true (fs/same-file? "dudette.txt" "hard-link.txt")))
    (is (= false (fs/sym-link? "hard-link.txt")))
    (is (= (slurp "hard-link.txt")
           (slurp "dudette.txt")))))

(deftest create-link-empty-string-test
  (is (thrown? java.nio.file.FileSystemException (fs/create-link "" "")))
  ;; javadoc implies link is for files only (not directories):
  (is (thrown? java.nio.file.FileSystemException (fs/create-link "link" "")))
  ;; for comparison:
  (is (thrown? java.nio.file.FileSystemException (fs/create-link "link" "."))))

;;
;; create-sym-link
;;
(deftest create-sym-link-empty-string-test
  (files "da1/da2/da3/da4/f2.txt")
  ;; on macOS throws java.nio.file.FileAlreadyExistsException
  ;; on linux throws java.nio.file.NoSuchFileException
  (is (thrown? java.nio.file.FileSystemException (fs/create-sym-link "" "")))
  ;; a bit of different behaviour depending on OS
  (if (not= :mac (util/os))
    ;; linux/windows bug? inconsistent: if "" is cwd, should be equivalent to (fs/create-sym-link "symlink1" ".") but throws:
    (is (thrown? Exception  (fs/create-sym-link "symlink1" "")))
    (do (fs/create-sym-link "symlink1" "")
        ;; link is created
        (is (= true (fs/sym-link? "symlink1")))
        ;; but does not map to cwd
        (is (match? ["da1/da2/da3/da4/f2.txt"
                     "symlink1"]
                    (list-tree ".")))))
  ;; for comparison with .:
  (util/clean-cwd)
  (files "da1/da2/da3/da4/f2.txt")
  (fs/create-sym-link "symlink2" ".")
  (is (= true (fs/sym-link? "symlink2")))
  (is (match? ["da1/da2/da3/da4/f2.txt"
               "symlink2/"]
              (list-tree "."))) 
  (is (fs/exists? "symlink2/da1/da2/da3/da4/f2.txt")))

;;
;; create-temp-dir
;;
(deftest create-temp-dir-empty-string-test
  (let [temp-dir (fs/create-temp-dir {:dir "" :prefix ""})]
    (is (re-matches #".+" (util/path->str temp-dir)))
    (is (= true (fs/exists? (fs/file-name temp-dir))))
    (is (= true (fs/directory? temp-dir)))))

;;
;; create-temp-file
;;
(deftest create-temp-file-empty-string-test
  (let [temp-file (fs/create-temp-file {:dir "" :prefix ""})]
    (is (re-matches #".+" (util/path->str temp-file)))
    (is (= true (fs/exists? (fs/file-name temp-file))))
    (is (= true (fs/regular-file? temp-file)))))

;;
;; creation-time
;;
(when (or (= :win (util/os))
          (and (= :mac (util/os)) (> (util/jdk-major) 17)))
  ;; we'll only test on envs where creation time fully works, see set-creation-time-test
  (deftest creation-time-sym-link-test
    (files "file")
    (let [ct-file (file-time "2024-01-01T00:00:00.00Z")
          ct-link (file-time "2025-01-01T00:00:00.00Z")
          nofollow-opts (into-array [LinkOption/NOFOLLOW_LINKS])]
      (fs/create-sym-link "link" "file")
      ;; use JVM API to set precondition
      (Files/setAttribute (fs/path "file") "basic:creationTime" ct-file nofollow-opts)
      (Files/setAttribute (fs/path "link") "basic:creationTime" ct-link nofollow-opts)
      (is (= ct-file
             (fs/creation-time "link")
             (fs/creation-time "link" {:nofollow-links false}))
          "following link")
      (is (= ct-link
             (fs/creation-time "link" {:nofollow-links true}))
          "not following link"))))

(deftest creation-time-empty-string-test
  (let [dir-creation-time (fs/creation-time ".")]
    (is (= dir-creation-time (fs/creation-time "")))))

;;
;; delete
;;
(when (not windows?)
  (deftest delete-permissions-unix-ro-file-test
    (files "my-file")
    (fs/set-posix-file-permissions "my-file" "r--r--r--")
    (is (nil? (fs/delete "my-file")))))

(when (not windows?)
  (deftest delete-permissions-unix-file-in-ro-dirs-throws-test
    (files "my-dir/my-file")
    (doseq [permissions ["r--------"
                         "--x------"
                         "r-x------"]]
      (fs/set-posix-file-permissions "my-dir" permissions)
      (is (thrown? java.nio.file.AccessDeniedException (fs/delete "my-dir/my-file"))
          (str "throws when dir has ro permissions: " permissions)))
    ;; and finally let's test if we can delete when permissions allow
    (fs/set-posix-file-permissions "my-dir" "rwx------")
    (is (nil? (fs/delete "my-dir/my-file"))
        "deletes when dir has write permissions")))

(when windows?
  (deftest delete-permissions-windows-idempotently-writable-file-test
    (files "my-file")
    ;; on windows, .setWritable is idempotent
    (.setWritable (fs/file "my-file") true)
    (.setWritable (fs/file "my-file") true)
    (is (nil? (fs/delete "my-file")))))

(when windows?
  (deftest delete-permissions-windows-ro-file-throws-test
    (files "my-file")
    (.setWritable (fs/file "my-file") false)
    (is (thrown? Exception (fs/delete "my-file")))
    (.setWritable (fs/file "my-file") true)
    (is (nil? (fs/delete "my-file")))))

(when windows?
  (deftest delete-permissions-windows-file-in-ro-dir-test
    (files "my-dir/my-file")
    (.setWritable (fs/file "my-dir") false)
    (is (nil? (fs/delete "my-dir/my-file")))))

(deftest delete-empty-string-test
  (files "foo/bar/baz/boop.txt" "bop.txt")
  (let [before (util/fsnapshot)]
    ;; can't delete non-empty dir
    (is (thrown? java.nio.file.FileSystemException (fs/delete "")))
    (is (match? before (util/fsnapshot)))))

;;
;; delete-if-exists
;;
(deftest delete-if-exists-test
  (files "dude")
  (is (= true (fs/delete-if-exists "dude")))
  (is (= false (fs/delete-if-exists "dude"))))

(deftest delete-if-exists-empty-string-test
  (files "foo/bar/baz/boop.txt" "bop.txt")
  (let [before (util/fsnapshot)]
    ;; can't delete non-empty dir
    (is (thrown? java.nio.file.FileSystemException (fs/delete-if-exists "")))
    (is (match? before (util/fsnapshot)))))

;;
;; delete-on-exit
;;
(deftest delete-on-exit-empty-string-test
  ;; tested elsewhere, here we just check that it does not throw
  ;; NOTE: this does not seem to actually delete on exit, perhaps because the dir is in use?
  (is (do (fs/delete-on-exit "")
          true)
      "does not throw"))

;;
;; delete-tree
;;
(deftest delete-tree-test
  (files "foo/bar/baz/file.txt")
  (fs/delete-tree "foo")
  (is (match? [] (fs/glob "." "**"))))

(deftest delete-tree-nested-test
  (files "foo/bar/baz/file.txt")
  (fs/delete-tree "foo/bar/baz")
  (is (match? ["foo/bar/"]
              (list-tree "."))))

(deftest delete-tree-ok-if-dir-missing-test
  (is (do (fs/delete-tree "foo")
          true))
  (is (do (fs/delete-tree "foo/bar/baz")
          true)))

(deftest delete-tree-does-not-follow-symlink-test
  (files "dir1/"
         "dir2/foo")
  (fs/create-sym-link "dir1/link-to-dir2" "../dir2")
  (is (= true (fs/same-file? "dir1/link-to-dir2" "dir2")) "precondition: link")
  (is (match? ["dir1/link-to-dir2/"
               "dir2/foo"]
              (list-tree ".")) "precondition: files")
  (fs/delete-tree "dir1")
  (is (match? ["dir2/foo"]
              (list-tree "."))))

(deftest delete-tree-force-deletes-ro-dirs-and-files-test
  (files "dir1/file1.txt"
         "dir1/subdir/file2.txt")
  (if windows?
    (do
      (.setWritable (fs/file "dir1/file1.txt") false)
      (.setWritable (fs/file "dir1/subdir/file2.txt") false)
      (.setWritable (fs/file "dir1/subdir") false)
      (.setWritable (fs/file "dir1") false))
    (do
      (fs/set-posix-file-permissions "dir1/file1.txt" "r--r--r--")
      (fs/set-posix-file-permissions "dir1/subdir/file2.txt" "r--r--r--")
      (fs/set-posix-file-permissions "dir1/subdir" "r--r--r--")
      (fs/set-posix-file-permissions "dir1" "r--r--r--")))
  (fs/delete-tree "dir1" {:force true})
  (is (match? [] (fs/glob "." "**"))))

(deftest delete-tree-empty-string-test
  (files "f1.ext"
         "da1/da2/da3/da4/f2.ext")
  ;; although this throws, assumedly on attempting to delete cwd, it first deletes all files and subdirs
  (is (thrown? java.nio.file.FileSystemException (fs/delete-tree "")))
  (is (match? [] (list-tree "."))))

(deftest delete-tree-good-sym-link-root-sym-link-test
  (files "foo/bar/baz/")
  (fs/create-sym-link "good-link" "foo")
  (fs/delete-tree "good-link")
  (is (match? ["foo/bar/baz/"]
              (list-tree "."))
      "link was deleted, dir was not"))

(deftest delete-tree-bad-sym-link-root-sym-link-test
  (fs/create-sym-link "bad-link" "bad-target")
  (fs/delete-tree "bad-link")
  (is (match? [] (list-tree "."))
      "bad link was deleted"))

;;
;; directory?
;;
(deftest directory?-test
  (files "dir/file.txt")
  (is (= true (fs/directory? "dir")))
  (is (= false (fs/directory? "dir/file.txt")))
  (is (= false (fs/directory? "idontexist")))
  (is (= false (fs/directory? (fs/path "dir" "idontexist")))))

(deftest directory?-sym-link-test
  (files "dir/")
  (fs/create-sym-link "dir-link" "dir")
  (is (= true
         (fs/directory? "dir-link")
         (fs/directory? "dir-link" {:nofollow-links false}))
      "following links")
  (is (= false
         (fs/directory? "file-link" {:nofollow-links true}))
      "not following links"))

(deftest directory?-empty-string-test
  (is (= true (fs/directory? ""))))

;;
;; ends-with?
;;
(deftest ends-with?-test
  (is (= true (fs/ends-with? "one/two/three" "three")))
  (is (= true (fs/ends-with? "one/two/three" "two/three")))
  (is (= true (fs/ends-with? "one/two/three" "one/two/three")))
  (is (= false (fs/ends-with? "one/two/three" "one/three"))))

(deftest ends-with?-empty-string-test
  (is (= true (fs/ends-with? "" ""))))

;;
;; executable?
;;
(deftest executable?-empty-string-test
  ;; dir has +x so returns true
  (is (= true (fs/executable? ""))))

;;
;; exists?
;;
(deftest invalid-path-test
  (testing "illegal windows path"
    ;; a `:` outside of the drive letter is illegal but should not
    ;; throw.
    (is (false? (fs/exists? "c:/123:456")))))

(deftest exists?-sym-link-test
  (fs/create-sym-link "link" "non-existent-target")
  (is (= false (fs/exists? "link") (fs/exists? {:nofollow-links false}))
      "following link (to non existent target)")
  (is (= true (fs/exists? "link" {:nofollow-links true}))
      "not following link (link exists)"))

(deftest exists?-empty-string-test
  (is (= true (fs/exists? ""))))
 
;;
;; expand-home
;;
(deftest expand-home-test
  ; The following tests assume that fs/home is working correctly
  (testing "for the current user"
    (is (= (fs/home)
           (fs/expand-home (fs/path "~"))
           (fs/expand-home "~")
           (fs/expand-home (str "~" fs/file-separator))))
    (is (= (fs/path (fs/home) "abc" "bb")
           (fs/expand-home (fs/path "~" "abc" "bb"))))
    ; Weird but technically allowed
    (is (= (fs/path (fs/home) "..")
           (fs/expand-home (fs/path "~" ".."))))
    (is (= (fs/path (fs/home) ".")
           (fs/expand-home (fs/path "~" ".")))))
  (testing "for another user"
    (is (= (fs/home "lola")
           (fs/expand-home (fs/path "~lola"))
           (fs/expand-home "~lola")
           (fs/expand-home (str "~lola" fs/file-separator))))
    (is (= (fs/path (fs/home "lola") "has" "a" "file")
           (fs/expand-home (fs/path "~lola" "has" "a" "file"))
           (fs/expand-home (str/join fs/file-separator ["~lola" "has" "a" "file"]))))
    ; Weird but technically allowed
    (is (= (fs/path (fs/home "raymond") "..")
           (fs/expand-home (fs/path "~raymond" ".."))))
    (is (= (fs/path (fs/home "raymond") ".")
           (fs/expand-home (fs/path "~raymond" "."))))
    (is (= (fs/path (fs/home) "file")
           (fs/expand-home "~/file"))))
  (testing "with nothing to expand"
    (doseq [input [["a" "b" "c"]
                   [""]
                   ["."]
                   [".."]]]
      (is (= (apply fs/path input)
             (fs/expand-home (str/join fs/file-separator input))))))
  (testing "with ~ in another place"
    (doseq [input [["a" "b" "~"]
                   ["a" "b" "~c"]
                   ["a" "~" "c"]
                   ["a" "~b" "c"]
                   ["a~" "b" "c"]]]
      (is (= (apply fs/path input)
             (fs/expand-home (str/join fs/file-separator input))))))
  (is (= (fs/path (fs/home) "abc" "~" "def")
         (fs/expand-home (fs/path "~" "abc" "~" "def")))))

(deftest expand-home-empty-string-test
  (is (= "" (util/path->str (fs/expand-home "")))))

;;
;; extension
;;
(deftest extension-test
  (is (= "clj" (fs/extension "file-name.clj")))
  (is (= "template" (fs/extension "file-name.html.template")))
  (is (nil? (fs/extension ".dotfile")))
  (is (nil? (fs/extension "bin/something"))))

(deftest extension-empty-string-test
  (is (nil? (fs/extension ""))))

;;
;; file
;;
(deftest file-test
  (let [f (fs/file "foo" "bar" (fs/path "baz"))]
    (is (instance? java.io.File f))
    (is (= "foo/bar/baz" (fs/unixify f)))))

(deftest file-empty-string-test
  (is (= "" (util/path->str (fs/file "")))))

;;
;; file-name
;;
(deftest file-name-test
  (let [f "some-dir/foo.ext"]
    (is (= "foo.ext" (fs/file-name f)))
    (is (= "foo.ext" (fs/file-name (fs/file f))))
    (is (= "foo.ext" (fs/file-name (fs/path f))))))

(deftest file-name-empty-string-test
  (is (= "" (fs/file-name ""))))

;;
;; file-time->instant (and instant->file-time)
;;
(deftest file-time-test
  (let [lmt (fs/get-attribute "." "basic:lastModifiedTime")]
    (is (instance? java.time.Instant (fs/file-time->instant lmt)))
    (is (= lmt (fs/instant->file-time (fs/file-time->instant lmt))))))

;;
;; get-attribute
;;
(deftest get-attribute-test
  (files "file")
  (let [lmt (java.nio.file.attribute.FileTime/from
             (java.time.Instant/parse "2026-02-11T23:24:25.26Z"))]
    (fs/set-last-modified-time "file" lmt)
    (is (= lmt (fs/get-attribute "file" "basic:lastModifiedTime")))
    (is (= lmt (fs/last-modified-time "file")))))

(deftest get-attribute-sym-link-test
  (files "file")
  (let [lmt-file (file-time "2024-01-01T00:00:00.00Z")
        lmt-link (file-time "2025-01-01T00:00:00.00Z")
        nofollow-opts (into-array [LinkOption/NOFOLLOW_LINKS])]
    (fs/create-sym-link "link" "file")
    ;; use JVM API to set precondition (when we can)
    (Files/setAttribute (fs/path "file") "basic:lastModifiedTime" lmt-file nofollow-opts)
    (if cant-set-last-modified-time-on-sym-link?
      (process/shell "touch -h -d" (str lmt-link) "link")
      (Files/setAttribute (fs/path "link") "basic:lastModifiedTime" lmt-link nofollow-opts))
    (is (= lmt-file
           (fs/get-attribute "link" "basic:lastModifiedTime")
           (fs/get-attribute "link" "basic:lastModifiedTime" {:nofollow-links false})))
    (is (= lmt-link
           (fs/get-attribute "link" "basic:lastModifiedTime" {:nofollow-links true})))))

(deftest get-attribute-empty-string-test
  (is (= true (fs/get-attribute "" "basic:isDirectory"))))

;;
;; glob
;;
(deftest glob-test
  (files "README.md" "project.clj" ".gitignore"
         "dira1/foo.txt"
         "dira1/dirb1/README.md"
         "dira1/dirb1/source.clj"
         "dira1/dirb1/dirc1/"
         "dira2/dirb1/test.cljc")
  (testing "glob single"
    (let [readme-match (fs/glob "." "README.md")]
      (is (match? ["README.md"] (map str readme-match)))
      (is (every? #(instance? java.nio.file.Path %) readme-match))))
  (testing "glob ** multiple with same filename auto-recursive"
    (let [readme-match (fs/glob "." "**README.md")]
      (is (match? ["README.md"
                   "dira1/dirb1/README.md"]
                  (normalized readme-match)))
      (is (every? #(instance? java.nio.file.Path %) readme-match))))
  (testing "glob ** but disable recursion"
    (let [readme-match (fs/glob "." "**README.md" {:recursive false})]
      (is (match? ["README.md"] (normalized readme-match)))
      (is (every? #(instance? java.nio.file.Path %) readme-match))))
  (testing "glob recursive by extension"
    (is (match? ["dira1/dirb1/source.clj"
                 "dira2/dirb1/test.cljc"
                 "project.clj"]
                (normalized
                  (fs/glob "." "**.{clj,cljc}")))))
  (testing "glob also matches directories and doesn't return the specified root directory"
    (is (match? ["dira1/dirb1/README.md"
                 "dira1/dirb1/dirc1/"
                 "dira1/dirb1/source.clj"]
                (normalized
                  (fs/glob "dira1/dirb1" "**"))))
    (is (match? ["dira1/dirb1/"
                 "dira1/dirb1/README.md"
                 "dira1/dirb1/dirc1/"
                 "dira1/dirb1/source.clj"]
                (normalized
                  (fs/glob "dira1" "dirb1**")))))
  (testing "symlink as root path"
    (let [sym-link (fs/create-sym-link "sym-link" "dira1")]
      (is (match? [] (fs/glob sym-link "*")))
      (is (match? ["sym-link/foo.txt"]
                  (normalized
                   (fs/glob sym-link "*" {:follow-links true}))))
      (is (match? ["dira1/foo.txt"]
                  (normalized
                   (fs/glob (fs/read-link sym-link) "*"))))))
  (when-not windows?
    (testing "hidden files"
      (testing "are not matched by default"
        (is (match? [] (normalized
                         (fs/glob "." "*git*")))))
      (testing "matched when :hidden option specified"
        (is (match? [".gitignore"]
                    (normalized
                      (fs/glob "." "*git*" {:hidden true})))))
      (testing "automatically matched when pattern starts with a dot"
        (is (match? [".gitignore"]
                    (normalized
                      (fs/glob "." ".gitig*"))))))))

(deftest glob-unicode-test
  (let [test-files [{:name "dir/📷 photography.md"        :has-variant-selector false}
                    {:name "dir/🗞️ article.md"            :has-variant-selector true}
                    {:name "dir/🗣️ talk.md"               :has-variant-selector true}
                    {:name "dir/🤔 interesting things.md" :has-variant-selector false}]]
    ;; sanity test our data
    (doseq [{:keys [name has-variant-selector]} test-files]
      (is (= has-variant-selector (str/includes? name "\uFE0F"))
          name))
    (apply files (map :name test-files))
    (if (and (= :mac (util/os)) (< (util/jdk-major) 26))
      ;; On macOS with JDK < 26 a bug exhibits where filenames with unicode with
      ;; variant selectors do not match.
      ;; See https://bugs.openjdk.org/browse/JDK-8354490 and https://github.com/babashka/fs/issues/141
      ;; We explicitly test for this bug learn if the fix is ever backported by JDK team
      (is (match? (keep #(when-not (:has-variant-selector %) (:name %)) test-files)
                  (normalized (fs/glob "dir" "*.md")))
          "JDK bug means we do not match filenames that have Unicode char with variation selector")
      (is (match? (map :name test-files)
                  (normalized (fs/glob "dir" "*.md")))
          "all files are returned"))))

(deftest glob-with-specific-depth-test
  (files "foo/bar/baz/dude.txt")
  (is (match? ["foo/bar/baz/dude.txt"]
              (normalized
               (if windows?
                 (fs/glob "." "foo\\\\bar\\\\baz\\\\*")
                 (fs/glob "." "foo/bar/baz/*"))))))

(deftest glob-windows-friendly-test
  (files "foo/bar/baz/dude.clj"
         "foo/bar/baz/dude2.clj")
  (is (match? ["foo/bar/baz/dude.clj"
               "foo/bar/baz/dude2.clj"]
              (normalized
                (fs/glob "." "foo/bar/baz/*.clj")))))

(deftest glob-returns-directories-test
  (files "foo/")
  (is (match? ["foo/"] (normalized
                         (fs/glob "." "*" {:max-depth 1})))))

(deftest glob-empty-string-test
  (files "da1/da2/da3/da4/f2.ext" "f1.ext")
  (is (= ["da1/da2/da3/da4/f2.ext"] (mapv util/path->str (fs/glob "" "**/f2.ext")))))

;;
;; gunzip (and gzip)
;;
(deftest gzip-unzip-default-output-dir-test
  (doseq [source-dir [""
                      "."
                      "out"
                      "a/b/c"
                      (fs/absolutize "out")
                      (fs/absolutize "a/b/c")]]
    (util/clean-cwd)
    (testing (str "with default dir " (pr-str source-dir))
      (let [input-file (str (fs/path source-dir "README.md"))
            input-content "original\ncontent\nhere"
            expected-gz-file (str input-file ".gz")]
        (files input-file)
        (spit input-file input-content)
        (is (= expected-gz-file (fs/gzip input-file))
            "gzip returns created gz in same dir as input file")
        (is (match? (normalized
                      [input-file
                       expected-gz-file]
                      {:relativize "."})
                    (list-tree "."))
            "both input file and output file exist")
        (spit input-file "some\nnew\ncontent\n")
        (is (thrown? java.nio.file.FileAlreadyExistsException
                     (fs/gunzip expected-gz-file))
            "throws on attempted overwrite")
        ;; NOTE: we must specify the `dest` when specifying options, specify `nil` for default
        (fs/gunzip expected-gz-file nil {:replace-existing true})
        (is (match? (normalized
                      [input-file
                       expected-gz-file]
                      {:relativize "."})
                    (list-tree "."))
            "both input file and output file exist after output file overwrite")
        (is (= input-content (slurp input-file))
            "gunzipped content matches gzipped content")))))

(deftest gzip-unzip-specified-output-dir-test
  (doseq [source-dir ["." "out" "foo/bar/baz" (fs/absolutize "out") (fs/absolutize "foo/bar/baz")]
          out-dir ["." "out" "foo/bar/baz" (fs/absolutize "out") (fs/absolutize "foo/bar/baz")]]
    (util/clean-cwd)
    (testing (str "with source dir " (pr-str source-dir) " and output dir " (pr-str out-dir))
      (let [input-file (str (fs/normalize (fs/path source-dir "README.md")))
            input-content "original\ncontent\nhere"
            expected-ungz-file (str (fs/normalize (fs/path out-dir "README.md")))
            expected-gz-file (str (fs/path out-dir "README.md.gz"))]
        (files input-file)
        (spit input-file input-content)
        (is (= expected-gz-file (fs/gzip input-file {:dir out-dir}))
            "gzip returns created gz file in specified out dir")
        (is (match? (normalized
                     [input-file
                      expected-gz-file]
                     {:relativize "."})
                    (list-tree "."))
            "both input file and output file exist")
        (if-not (= (fs/absolutize expected-ungz-file) (fs/absolutize input-file))
          (do
            (is (do (fs/gunzip expected-gz-file out-dir)
                    true)
                "does not throw")
            (is (match? (normalized
                         [input-file
                          expected-gz-file
                          expected-ungz-file]
                         {:relativize "."})
                        (list-tree "."))
                "both input file and output files exist"))
          (do
            (is (thrown? java.nio.file.FileAlreadyExistsException
                         (fs/gunzip expected-gz-file out-dir))
                "throws on attempted overwrite")
            (spit expected-ungz-file "some\nnew\ncontent\n")
            (is (do (fs/gunzip expected-gz-file out-dir {:replace-existing true})
                    true)
                "does not throw on overwrite")
            (is (match? (normalized
                         [expected-gz-file
                          expected-ungz-file]
                         {:relativize "."})
                        (list-tree "."))
                "both input file and output file exist")))
        (is (= (slurp expected-ungz-file) (slurp input-file))
            "gunzipped content matches gzipped content")))))

(deftest gzip-out-file-test
  (doseq [[expected-gz source-file opts] [["boop"             "foo.txt"    {:out-file "boop"}]
                                          ["foo.txt.gz"       "foo.txt"    {:out-file "foo.txt.gz"}]
                                          ["a/b/c/foo.gz"     "a/b/c/foo"  {:out-file "foo.gz"}]
                                          ["d/e/f/foo.gz"     "a/b/c/foo"  {:out-file "foo.gz" :dir "d/e/f"}]
                                          ["a/b/c/y/z/foo.gz" "a/b/c/foo"  {:out-file "y/z/foo.gz"}]
                                          ["out/y/z/foo.gz"   "a/b/c/foo"  {:out-file "y/z/foo.gz" :dir "out"}]]]
    (testing (str "source-file: " source-file " opts: " opts)
      (util/clean-cwd)
      (files source-file)
      (spit source-file "orig content")
      (is (= expected-gz (fs/unixify (fs/gzip source-file opts))))
      (fs/gunzip expected-gz "verify")
      (is (= "orig content" (slurp (fs/file "verify" (-> expected-gz fs/file-name fs/strip-ext))))
          "ungzipped matches original"))))

(deftest gzip-arg-types-test
  (files "foo.txt")
  (doseq [arg-type [:str :file :path]]
    (testing (str "args type: " (name arg-type))
      (let [arg-fn (arg-type {:str identity :file fs/file :path fs/path})]
        (is (= "foo.txt.gz"
               (fs/gzip (arg-fn "foo.txt"))))
        (is (= (str (fs/path "out-dir" "foo.txt.gz"))
               (fs/gzip (arg-fn "foo.txt") {:dir (arg-fn "out-dir")})))
        (is (= (str (fs/path "out-dir" "bar.txt.gz"))
               (fs/gzip (arg-fn "foo.txt") {:dir (arg-fn "out-dir") :out-file "bar.txt.gz"})))))))

(deftest gunzip-empty-string-test
  (files "f1.ext")
  (let [last-modified (fs/last-modified-time "f1.ext")
        content (slurp "f1.ext")
        gzip (fs/gzip "f1.ext")]
    (is (thrown? java.nio.file.FileAlreadyExistsException (fs/gunzip gzip "")))
    (Thread/sleep 50)
    (fs/gunzip gzip "" {:replace-existing true})
    (is (not= last-modified (fs/last-modified-time "f1.ext")))
    (is (= content (slurp "f1.ext")))))

(deftest gzip-empty-string-test
  ;; gzip expects a file, not a dir
  (is (thrown? java.io.FileNotFoundException (fs/gzip ""))))

;;
;; hidden?
;;
(deftest hidden-empty-string-test
  (if (and (not (fs/windows?)) (< (util/jdk-major) 17))
    (is (thrown? java.lang.ArrayIndexOutOfBoundsException (fs/hidden? "")))
    (is (= false (fs/hidden? "")))))

;;
;; home 
;;
(deftest home-test
  (let [user-home (fs/path (System/getProperty "user.home"))
        user-dir (fs/parent user-home)]
    (testing "without arguments"
      (is (= user-home
             (fs/home))))
    (testing "with a username"
      (is (= (fs/path user-dir "this-is-me")
             (fs/home "this-is-me"))))
    (testing "without username"
      (is (= user-home
             (fs/home "")
             (fs/home nil))))))

;;
;; last-modified-time
;;
(deftest last-modified-time-sym-link-test
  (files "file")
  (let [lmt-file (file-time "2024-01-01T00:00:00.00Z")
        lmt-link (file-time "2025-01-01T00:00:00.00Z")
        nofollow-opts (into-array [LinkOption/NOFOLLOW_LINKS])]
    (fs/create-sym-link "link" "file")
    ;; use JVM API to set precondition (when we can)
    (Files/setAttribute (fs/path "file") "basic:lastModifiedTime" lmt-file nofollow-opts)
    (if cant-set-last-modified-time-on-sym-link?
      (process/shell "touch -h -d" (str lmt-link) "link")
      (Files/setAttribute (fs/path "link") "basic:lastModifiedTime" lmt-link nofollow-opts))
    (is (= lmt-file
           (fs/last-modified-time "link")
           (fs/last-modified-time "link" {:nofollow-links false})))
    (is (= lmt-link
           (fs/last-modified-time "link" {:nofollow-links true})))))

(deftest last-modified-time-empty-string-test
  (let [dir-last-modified-time (fs/last-modified-time ".")]
    (is (= dir-last-modified-time (fs/last-modified-time "")))))

;;
;; list-dir 
;;
(deftest list-dir-test
  (files "dir1/"
         "dir2/foo.txt"
         "file.txt"
         "source1.clj"
         "source2.clj")
  (is (match? ["./dir1/"
               "./dir2/"
               "./file.txt"
               "./source1.clj"
               "./source2.clj"]
              (normalized (fs/list-dir "."))))
  (is (match? ["./dir1/"
               "./dir2/"]
              (normalized (fs/list-dir "." (fn accept [x] (fs/directory? x))))))
  (is (match? [] (fs/list-dir "." (fn accept [_] false))))
  (is (match? ["./source1.clj"
               "./source2.clj"]
              (normalized (fs/list-dir "." "*.clj")))))

(deftest list-dir-empty-string-test
  (files "da1/da2/da3/da4/f2.ext" "f1.ext")
  (is (= ["da1/" "f1.ext"] (normalized (fs/list-dir "")))))

;;
;; list-dirs
;;
(deftest list-dirs-and-which-test
  (let [java-executable (if windows?
                          "java.exe"
                          "java")
        java (first (filter fs/executable?
                            (fs/list-dirs
                             (filter fs/exists?
                                     (fs/exec-paths))
                             java-executable)))]
    (is java)
    ;; on Windows we can find the executable on the path without the .exe extension
    (is (= java (fs/which "java")))
    (is (contains? (set (fs/which-all "java")) java))
    (fs/create-dirs "on-path/path-subdir")
    (doseq [f ["foo.foo" "foo.foo.bat" "foo.foo.cmd" "foo.cmd.bat" "foo.foo.ps1" "bar.bar"]]
      (spit (fs/file "on-path" f) "echo hello"))
    (when (not windows?)
      (fs/set-posix-file-permissions (fs/file "on-path" "foo.foo") "r-xr-x---"))
    (fs/copy-tree "on-path" "off-path")
    (if windows?
      (is (= (fs/path "on-path/foo.foo.bat") (fs/which "foo.foo")))
      (is (= (fs/path "on-path/foo.foo") (fs/which "foo.foo"))))
    (when windows?
      (testing "on windows, can find executable when including extension"
        (let [expected (fs/path "on-path/foo.foo.bat")]
          (is (= expected (fs/which "foo.foo") (fs/which "foo.foo.bat"))))))
    (when windows?
      (testing "on windows, can find foo.cmd.bat"
        (let [expected (fs/path "on-path/foo.cmd.bat")]
          (is (= expected (fs/which "foo.cmd") (fs/which "foo.cmd.bat"))))))
    (when windows?
      (testing "on windows, can override win extension search"
        (let [expected (fs/path "on-path/foo.foo.ps1")]
          (is (= expected (fs/which "foo.foo" {:win-exts ["ps1"]}))))))
    (testing "custom path"
      (is (= [] (fs/which-all "foo.foo" {:paths ["./idontexist"]})))
      (is (nil? (fs/which "foo.foo" {:paths ["./idontexist"]})))
      (if windows?
        (testing "windows"
          (is (= [(fs/path "./on-path/foo.foo.bat") (fs/path "./on-path/foo.foo.cmd")]
                 (fs/which-all "foo.foo" {:paths ["./on-path"]})))
          (is (= [(fs/path "./off-path/foo.foo.bat") (fs/path "./off-path/foo.foo.cmd")]
                 (fs/which-all "foo.foo" {:paths ["./off-path"]})))
          (is (= [(fs/path "./off-path/foo.foo.bat") (fs/path "./off-path/foo.foo.cmd")
                  (fs/path "./on-path/foo.foo.bat") (fs/path "./on-path/foo.foo.cmd")]
                 (fs/which-all "foo.foo" {:paths ["./off-path" "./on-path"]})))
          (is (= (fs/path "./off-path/foo.foo.bat")
                 (fs/which "foo.foo" {:paths ["./off-path" "./on-path"]}))))
        (testing "macos/linux"
          (is (= [(fs/path "./on-path/foo.foo")]
                 (fs/which-all "foo.foo" {:paths ["./on-path"]})))
          (is (= [(fs/path "./off-path/foo.foo")]
                 (fs/which-all "foo.foo" {:paths ["./off-path"]})))
          (is (= [(fs/path "./off-path/foo.foo") (fs/path "./on-path/foo.foo")]
                 (fs/which-all "foo.foo" {:paths ["./off-path" "./on-path"]})))
          (is (= (fs/path "./off-path/foo.foo")
                 (fs/which "foo.foo" {:paths ["./off-path" "./on-path"]}))))))
    (testing "'which' shouldn't find directories"
      (is (nil? (fs/which "path-subdir"))))
    (testing "'which' shouldn't find non executables"
      (is (nil? (fs/which "bar.bar"))))
    (testing "given a relative path, 'which' shouldn't search path entries"
      (is (nil? (fs/which "./foo.foo"))))
    (testing "relative path should resolve regardless of search path entries"
      (is (true? (fs/exists? "./off-path/bar.bar")))
      (is (nil? (fs/which "./off-path/bar.bar")) "non-executable return s nil")
      (is (nil? (fs/which "./relatively/missing")))
      (if windows?
        (testing "windows"
          (is (= (fs/path "./on-path/foo.foo.bat") (fs/which "./on-path/foo.foo")))
          (is (= (fs/path "./off-path/foo.foo.bat") (fs/which "./off-path/foo.foo"))))
        (testing "macos/linux"
          (is (= (fs/path "./off-path/foo.foo") (fs/which "./off-path/foo.foo")))
          (is (= (fs/path "./on-path/foo.foo") (fs/which "./on-path/foo.foo"))))))
    (testing "absolute path should resolve regardless of search path entries"
      (is (true? (fs/exists? (fs/absolutize "./off-path/bar.bar"))))
      (is (nil? (fs/which (fs/absolutize "./off-path/bar.bar"))) "non-executable returns nil")
      (is (nil? (fs/which "/absolutely/missing")))
      (if windows?
        (testing "windows"
          (is (= (fs/absolutize "./on-path/foo.foo.bat") (fs/which (fs/absolutize "./on-path/foo.foo"))))
          (is (= (fs/absolutize "./off-path/foo.foo.bat") (fs/which (fs/absolutize "./off-path/foo.foo")))))
        (testing "macos/linux"
          (let [on-path (fs/absolutize "./on-path/foo.foo")]
            (is (= on-path (fs/which on-path))))
          (let [off-path (fs/absolutize "./off-path/foo.foo")]
            (is (= off-path (fs/which off-path)))))))
    (->> ["on-path" "off-path"]
         (run! fs/delete-tree))))

(deftest list-dirs-empty-string-test
  (files "da1/da2/da3/da4/f2.ext" "f1.ext")
  (is (= ["da1/" "f1.ext"] (normalized (fs/list-dirs [""] "*")))))

;;
;; match
;;
(deftest match-test
  (files "README.md" "project.clj"
         "dira1/foo.txt"
         "dira1/dirb1/README.md"
         "dira1/dirb1/source.clj"
         "dira1/dirb1/dirc1/"
         "dira2/dirb1/test.cljc")
  (testing "match single"
    (let [readme-match (fs/match "." "regex:.*README.md")]
      (is (match? ["README.md"] (map str readme-match)))
      (is (every? #(instance? java.nio.file.Path %) readme-match))))
  (testing "match multiple with same filename recursive"
    (let [readme-match (fs/match "." "regex:.*README.md" {:recursive true})]
      (is (match? ["README.md"
                   "dira1/dirb1/README.md"]
                  (normalized readme-match)))
      (is (every? #(instance? java.nio.file.Path %) readme-match))))
  (testing "match multiple recursive by extension"
    (is (match? ["dira1/dirb1/source.clj"
                 "dira2/dirb1/test.cljc"
                 "project.clj"]
                (normalized
                  (fs/match "." "regex:.*\\.cljc?" {:recursive true})))))
  (testing "match also matches directories and doesn't return the root directory"
    (is (match? ["dira1/dirb1/README.md"
                 "dira1/dirb1/dirc1/"
                 "dira1/dirb1/source.clj"]
                (normalized
                  (fs/match "dira1/dirb1" "regex:.*" {:recursive true}))))
    (is (match? ["dira1/dirb1/"
                 "dira1/dirb1/README.md"
                 "dira1/dirb1/dirc1/"
                 "dira1/dirb1/source.clj"]
                (normalized
                 (fs/match "dira1" "regex:dirb1.*" {:recursive true})))))
  (testing "symlink as root path"
    (let [sym-link (fs/create-sym-link "sym-link" "dira1")
          target (fs/read-link sym-link)]
      (is (= (str target) "dira1"))
      (is (match? [] (fs/match sym-link "regex:.*")))
      (is (match? ["sym-link/foo.txt"]
                  (normalized (fs/match sym-link "regex:.*" {:follow-links true}))))
      (is (match? ["dira1/foo.txt"]
                  (normalized (fs/match (fs/read-link sym-link) "regex:.*")))))))

(deftest match-at-specific-depth-test
  (files "foo/bar/baz/dude.txt")
  (is (match? ["foo/bar/baz/dude.txt"]
              (normalized
               (if windows?
                 (fs/match "." "regex:foo\\\\bar\\\\baz\\\\.*" {:recursive true})
                 (fs/match "." "regex:foo/bar/baz/.*" {:recursive true}))))))

(when-not windows?
  (deftest match-on-root-with-special-chars-test
    (files "some-dir/foo*{[]}/test.txt")
    (is (match? ["some-dir/foo*{[]}/test.txt"]
                (normalized (fs/match "some-dir/foo*{[]}" "glob:*.txt"))))
    (is (match? ["some-dir/foo*{[]}/test.txt"]
                (normalized (fs/match "some-dir/foo*{[]}" "regex:.*\\.txt"))))))

(deftest match-empty-string-test
  (files "da1/da2/da3/da4/f2.ext" "f1.ext")
  (is (= ["da1/da2/da3/da4/f2.ext" "f1.ext"] (normalized (fs/match "" "regex:.*\\.ext" {:recursive true})))))

;;
;; modified-since
;;
(deftest modified-since-with-sleep-test
  (files "dir1/anchor")
  (Thread/sleep 50)
  (files "dir2/f1"
         "dir2/f2")
  (is (match? ["dir2/f1"]
              (normalized (fs/modified-since "dir1/anchor" "dir2/f1"))))
  (is (match? ["dir2/f1"
               "dir2/f2"]
              (normalized (fs/modified-since "dir1/anchor" "dir2"))))
  (is (match? ["dir2/f1"
               "dir2/f2"]
              (normalized (fs/modified-since "dir1" "dir2"))))
  (fs/set-last-modified-time "dir1/anchor" (fs/last-modified-time "dir2/f1"))
  (is (match? [] (fs/modified-since "dir1/anchor" "dir2/f1"))))

(deftest modified-since-no-sleep-test
  (files "dir1/anchor"
         "dir2/f1"
         "dir2/f2")
  (let [now (java.time.Instant/now)
        _ (fs/set-last-modified-time "dir1/anchor" now)
        later (.plusNanos (java.time.Instant/now) 10000)
        _ (fs/set-last-modified-time "dir2/f1" later)
        _ (fs/set-last-modified-time "dir2/f2" later)]
    (is (match? ["dir2/f1"]
                (normalized (fs/modified-since "dir1/anchor" "dir2/f1"))))
    (is (match? ["dir2/f1"
                 "dir2/f2"]
                (normalized (fs/modified-since "dir1/anchor" "dir2"))))
    (is (match? ["dir2/f1"
                 "dir2/f2"]
                (normalized (fs/modified-since "dir1" "dir2"))))
    (fs/set-last-modified-time "dir1/anchor" (fs/last-modified-time "dir2/f1"))
    (is (match? [] (fs/modified-since "dir1/anchor" "dir2/f1")))))

(deftest modified-empty-string-since
  (files "da1/da2/da3/da4/f2.ext" "f1.ext")
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
    (is (= ["f1.ext"] (normalized (fs/modified-since "da1/da2/da3/da4/f2.ext" ""))))))

;;
;; move
;;
(deftest move-to-file-test
  (files "src-dir/foo.txt"
         "dest-dir/")
  (let [foo-content (str/trim (slurp "src-dir/foo.txt"))]
    (fs/move "src-dir/foo.txt" "dest-dir/foo.txt")
    (is (match? ["dest-dir/foo.txt"
                 "src-dir/"]
                (list-tree ".")))
    (is (= foo-content (str/trim (slurp "dest-dir/foo.txt"))))))

(deftest move-to-dir-test
  (files "src-dir/foo.txt"
         "dest-dir/")
  (let [foo-content (str/trim (slurp "src-dir/foo.txt"))]
    (fs/move "src-dir/foo.txt" "dest-dir")
    (is (match? ["dest-dir/foo.txt"
                 "src-dir/"]
                (list-tree ".")))
    (is (= foo-content (str/trim (slurp "dest-dir/foo.txt"))))))

(deftest move-empty-string-test
  (files "da1/da2/da3/da4/f2.ext" "f1.ext")
  ;; returns target
  ;; as per javadoc should be no-op
  (let [before (util/fsnapshot)]
    (is (= "" (util/path->str (fs/move "" ""))))
    (is (match? before (util/fsnapshot)))))

(deftest move-src-empty-string-test
  (files "da1/da2/da3/da4/f2.ext" "f1.ext")
  (let [before (util/fsnapshot)
        target (fs/create-temp-dir)]
    ;; Device or resource busy
    (is (thrown? java.nio.file.FileSystemException (fs/move "" (fs/file target "new-thing"))))
    (is (match? before (util/fsnapshot)))))

(deftest move-dest-empty-string-test
  (files "da1/da2/da3/da4/f2.ext" "f1.ext")
  (is (= "da3" (util/path->str (fs/move "da1/da2/da3" ""))))
  (is (match? ["da1/da2/"
               "da3/da4/f2.ext"
               "f1.ext"]
              (list-tree "."))))

(deftest move-bad-link-to-bad-link-sym-link-test
  (fs/create-sym-link "bad-link1" "bad-target1")
  (fs/create-sym-link "bad-link2" "bad-target2")
  (fs/move "bad-link1" "bad-link2" {:replace-existing true})
  (is (match? ["bad-link2"] (list-tree ".")))
  (is (= (fs/path "bad-target1") (fs/read-link "bad-link2"))))

(deftest move-good-link-to-good-link-sym-link-test
  (files "dir1/" "dir2/")
  (fs/create-sym-link "good-link1" "dir1")
  (fs/create-sym-link "good-link2" "dir2")
  (fs/move "good-link1" "good-link2" {:replace-existing true})
  (is (match? ["dir1/"
               "dir2/"
               "good-link2/"]
              (list-tree ".")))
  (is (= (fs/path "dir1") (fs/read-link "good-link2"))))

(deftest move-good-link-to-good-link-no-replace-sym-link-test
  (files "dir1/" "dir2/")
  (fs/create-sym-link "good-link1" "dir1")
  (fs/create-sym-link "good-link2" "dir2")
  (is (thrown? FileAlreadyExistsException
               (fs/move "good-link1" "good-link2" {:replace-existing false}))))

(deftest move-good-link-under-dir-sym-link-test
  (files "dir1/" "dir2/")
  (fs/create-sym-link "good-link1" "dir1")
  (fs/move "good-link1" "dir2")
  (is (match? ["dir1/"
               "dir2/good-link1"] ;; notice link is not a dir anymore, it is broken
              (list-tree ".")))
  (is (= (fs/path "dir1") (fs/read-link (fs/path "dir2" "good-link1")))))

(deftest move-file-to-to-good-link-sym-link-test
  (files "file1.txt" "dir1/")
  (fs/create-sym-link "good-link1" "dir1")
  (fs/move "file1.txt" "good-link1" {:replace-existing true})
  (is (match? ["dir1/"
               "good-link1"]
              (list-tree ".")))
  (is (= false (fs/sym-link? "good-link1"))))

(deftest move-good-link-to-file-sym-link-test
  (files "file1.txt" "dir1/")
  (fs/create-sym-link "good-link1" "dir1")
  (fs/move "good-link1" "file1.txt" {:replace-existing true})
  (is (match? ["dir1/"
               "file1.txt/"]
              (list-tree ".")))
  (is (= true (fs/sym-link? "file1.txt"))))

(deftest rename-good-link-sym-link-test
  (files "dir1/")
  (fs/create-sym-link "good-link1" "dir1")
  (fs/move "good-link1" "good-link2")
  (is (match? ["dir1/"
               "good-link2/"]
              (list-tree ".")))
  (is (= true (fs/sym-link? "good-link2")))
  (is (= (fs/path "dir1") (fs/read-link "good-link2"))))

(deftest move-link-without-replace-sym-link-test
  (files "dir1/" "dir2/")
  (fs/create-sym-link "good-link1" "dir1")
  (fs/create-sym-link "good-link2" "dir2")

  (is (thrown-with-msg? FileAlreadyExistsException #"good-link2"
                        (fs/move "good-link1" "good-link2"))))

;;
;; normalize
;;
(deftest normalize-test
  (is (= "foo/bar/baz" (fs/unixify (fs/normalize "foo/bar/baz"))))
  (is (= "foo/bar/baz" (fs/unixify (fs/normalize "./foo/./bing/./boop/.././../bar/./baz/.")))))

(deftest normalize-empty-string-test
  (is (= "" (util/path->str (fs/normalize "")))))

;;
;; owner
;;
(deftest file-owner-test
  (files "dir/file")
  (is (= (str (fs/owner "dir")) (str (fs/owner "dir/file")))))

(deftest file-owner-sym-link-test
  ;; This test assumes that the owner of "/" will be different than the owner of a link created in the cwd
  (files "file")
  (fs/create-sym-link "my-link" "/")
  (is (not= (fs/owner "file") (fs/owner "/"))
      "sanity test: owners are different for root dir and file in cwd")
  (is (= (fs/owner "/") (fs/owner "my-link") (fs/owner "my-link" {:nofollow-links false}))
      "following link")
  (is (= (fs/owner "file") (fs/owner "my-link" {:nofollow-links true}))
      "not following link"))

(deftest owner-empty-string-test
  (files "f1.ext")
  (is (= (fs/owner "") (fs/owner "f1.ext"))))

;;
;; parent
;;
(deftest parent-test
  (is (= (fs/path "dir") (fs/parent "dir/foo")))
  (is (= nil (fs/parent "foo"))))

(deftest parent-empty-string-test
  ;; reminder: parent in path, not parent in filesystem
  (is (nil? (fs/parent ""))))

;;
;; path
;;
(deftest path-test
  (let [p (fs/path "foo" "bar" (io/file "baz"))]
    (is (instance? java.nio.file.Path p))
    (is (= "foo/bar/baz" (fs/unixify p)))))

(deftest filesystem-path-resolves-test
  ;; see issue 135
  ;; we open a zip file system on a dummy jar to test
  (create-zip-file "foo.jar" [["bar/"]])
  (let [uri (java.net.URI/create (str "jar:file:" (-> (fs/cwd) fs/path .toUri .getPath) "foo.jar"))]
    (with-open [fs (java.nio.file.FileSystems/newFileSystem uri ^java.util.Map (identity {}))]
      (let [path-in-zip (.getPath ^java.nio.file.FileSystem fs "/bar" (into-array String []))
            zip-path (fs/path path-in-zip "baz.clj")]
        (is zip-path)
        (is (= "/bar/baz.clj" (str zip-path)))))))

(deftest uri->path-test
  (is (instance? java.nio.file.Path
                 (fs/path (.toURI (fs/file "."))))))

(deftest path-empty-string-test
  (is (= "" (util/path->str (fs/path "")))))

;;
;; *posix* 
;;
(when-not windows?
  (deftest posix-test
    (let [requested-permissions "rwxrwxrwx"
          expected-permissions (util/umasked requested-permissions util/umask)]
      (fs/create-file "file1" {:posix-file-permissions requested-permissions})
      (is (= expected-permissions (-> (fs/posix-file-permissions "file1")
                                      fs/posix->str))
          (str "file created with umask " util/umask)))
    (is (= (fs/posix-file-permissions "file1")
           (-> (fs/posix-file-permissions "file1")
               (fs/posix->str)
               (fs/str->posix))))
    (let [requested-permissions "rwx------"
          expected-permissions (util/umasked requested-permissions util/umask)]
      (is (= expected-permissions
             (-> (fs/create-temp-dir {:posix-file-permissions requested-permissions})
                 (fs/posix-file-permissions)
                 (fs/posix->str)))
          (str "temp-dir created with umask: " util/umask)))))

(when-not windows?
  (deftest posix-sym-link-test
    (files "file")
    (fs/create-sym-link "link" "file")
    (let [nofollow-opts (into-array [LinkOption/NOFOLLOW_LINKS])
          orig-link-permissions (fs/posix->str (Files/getPosixFilePermissions (fs/path "link") nofollow-opts))]
      ;; cycle through some variations so we know we'll have at least one that does not match perms at create time
      (doseq [[target  set-permissions]
              [["file" "rw-rw-rw-"]
               ["file" "rwxrwxrwx"]
               ["link" "rw-rw-rw-"]
               ["link" "rwxrwxrwx"]]]
        (testing (str "target: " target ", set-permissions: " set-permissions)
          ;; we can only set posix file permissions on a file, links are always followed on set
          (fs/set-posix-file-permissions target set-permissions)
          (is (= set-permissions
                 (fs/posix->str (fs/posix-file-permissions "link"))
                 (fs/posix->str (fs/posix-file-permissions "link" {:nofollow-links false})))
              "following links")
          (is (= orig-link-permissions
                 (fs/posix->str (fs/posix-file-permissions "link" {:nofollow-links true})))
              "not following links"))))))

(when-not windows?
  (deftest set-posix-test
    (let [requested-permissions "rwxrwxrwx"
          expected-permissions (util/umasked requested-permissions util/umask)]
      ;; a created file is affected by umask
      (fs/create-file "foo" {:posix-file-permissions expected-permissions})
      (is (= expected-permissions (-> (fs/posix-file-permissions "foo")
                                      fs/posix->str))))
    ;; an existing file is not affected by umask
    (doseq [permissions ["rwxrwxrwx" "rwx------"]]
      (fs/set-posix-file-permissions "foo" permissions)
      (is (= permissions (-> (fs/posix-file-permissions "foo")
                             fs/posix->str))
          (str "existing file permissions set to " permissions)))))

(when (not (fs/windows?))
  (deftest set-posix-file-permission-empty-string-test
    (let [old (fs/posix-file-permissions "")
          new "rwxrwxrwx"]
      (fs/set-posix-file-permissions "" new)
      (is (not= old (fs/posix->str (fs/posix-file-permissions ""))))
      (is (= new (fs/posix->str (fs/posix-file-permissions "")))))))

;;
;; read-all-bytes
;;
(deftest read-all-bytes-test
  (spit "README.md" "some\ncontent\nhere")
  (let [bs (fs/read-all-bytes "README.md")]
    (is (bytes? bs))
    (is (= (fs/size "README.md") (count bs)))))

(deftest read-all-bytes-empty-string-test
  (is (thrown? java.io.IOException (fs/read-all-bytes ""))))

;;
;; read-all-lines
;;
(deftest read-all-lines-test
  (spit "README.md" "some\ncontent\nhere")
  (let [ls (with-open [rdr (io/reader (fs/file "README.md"))]
             (doall (line-seq rdr)))]
    (is (= ls (fs/read-all-lines "README.md")))))

(deftest read-all-lines-8859-test
  (spit "iso-8859.txt" "áéíóú\nEspaña" :encoding "ISO-8859-1")
  (is (thrown? java.io.IOException (fs/read-all-lines "iso-8859.txt")))
  (let [ls (fs/read-all-lines "iso-8859.txt" {:charset "iso-8859-1"})]
    (is (= ["áéíóú" "España"] ls))))

(deftest read-all-lines-empty-string-test
  (is (thrown? java.io.IOException (fs/read-all-lines ""))))

;;
;; read-attributes
;;
(deftest read-attributes-sym-link-test
  (files "file")
  (let [lmt-file (file-time "2024-01-01T00:00:00.00Z")
        lmt-link (file-time "2025-01-01T00:00:00.00Z")
        nofollow-opts (into-array [LinkOption/NOFOLLOW_LINKS])]
    (fs/create-sym-link "link" "file")
    ;; use JVM API to set precondition (when we can)
    (Files/setAttribute (fs/path "file") "basic:lastModifiedTime" lmt-file nofollow-opts)
    (if cant-set-last-modified-time-on-sym-link?
      (process/shell "touch -h -d" (str lmt-link) "link")
      (Files/setAttribute (fs/path "link") "basic:lastModifiedTime" lmt-link nofollow-opts))
    (is (= lmt-file
           (:lastModifiedTime (fs/read-attributes "link" "*"))
           (:lastModifiedTime (fs/read-attributes "link" "*" {:nofollow-links false})))
        "following links")
    (is (= lmt-link
           (:lastModifiedTime (fs/read-attributes "link" "*" {:nofollow-links true})))
        "not following links")))

(deftest read-attributes-empty-string-test
  (is (= {:isDirectory true} (fs/read-attributes "" "basic:isDirectory"))))

;;
;; read-attributes*
;;
(deftest read-attributes*-sym-link-test
  (files "file")
  (let [lmt-file (file-time "2024-01-01T00:00:00.00Z")
        lmt-link (file-time "2025-01-01T00:00:00.00Z")
        nofollow-opts (into-array [LinkOption/NOFOLLOW_LINKS])]
    (fs/create-sym-link "link" "file")
    ;; use JVM API to set precondition (when we can)
    (Files/setAttribute (fs/path "file") "basic:lastModifiedTime" lmt-file nofollow-opts)
    (if cant-set-last-modified-time-on-sym-link?
      (process/shell "touch -h -d" (str lmt-link) "link")
      (Files/setAttribute (fs/path "link") "basic:lastModifiedTime" lmt-link nofollow-opts))
    (is (= lmt-file
           (get (fs/read-attributes* "link" "*") "lastModifiedTime")
           (get (fs/read-attributes* "link" "*" {:nofollow-links false}) "lastModifiedTime"))
        "following links")
    (is (= lmt-link
           (get (fs/read-attributes* "link" "*" {:nofollow-links true}) "lastModifiedTime"))
        "not following links")))

(deftest read-attributes*-empty-string-test
  (is (= {"isDirectory" true} (fs/read-attributes* "" "basic:isDirectory"))))

;;
;; read-link
;;
(deftest read-link-empty-string-test
  (is (thrown? java.nio.file.NotLinkException (fs/read-link ""))))

;;
;; readable?
;;
(deftest readable?-test
  (files "dir" "file.txt")
  (is (= true (fs/readable? "dir")))
  (is (= true (fs/readable? "file.txt")))

  (.setReadable (fs/file "dir") false)
  (.setReadable (fs/file "file.txt") false)

  (if windows?
    ;; cannot set a dir to non-readable on Windows
    (is (= true (fs/readable? "dir")))
    (is (= false (fs/readable? "dir"))))

  (if windows?
    ;; cannot set a file to non-readable on Windows
    (is (= true (fs/readable? "file.txt")))
    (is (= false (fs/readable? "file.txt")))))

(deftest readable?-empty-string-test
  (is (= true (fs/readable? ""))))

;;
;; real-path
;;
(deftest real-path-sym-link-test
  (files "dir/file")
  (fs/create-sym-link "my-link" "dir/file")
  (is (= (fs/path (fs/cwd) "dir/file")
         (fs/real-path "my-link")
         (fs/real-path "my-link" {:nofollow-links false})
         (fs/real-path "./dir/../my-link")
         (fs/real-path "./dir/../my-link" {:nofollow-links false}))
      "following links")
  (is (= (fs/path (fs/cwd) "my-link")
         (fs/real-path "./dir/../my-link" {:nofollow-links true})
         (fs/real-path "my-link" {:nofollow-links true}))
      "not following links"))

(deftest real-path-empty-string-test
  (is (= (util/path->str (System/getProperty "user.dir")) (util/path->str (fs/real-path "")))))

;;
;; regular-file?
;;
(deftest regular-file?-test
  (files "dir/file.txt")
  (is (= false (fs/regular-file? "dir")))
  (is (= true (fs/regular-file? "dir/file.txt")))
  (is (= false (fs/regular-file? "idontexist")))
  (is (= false (fs/regular-file? (fs/path "dir" "idontexist")))))

(deftest regular-file?-sym-link-test
  (files "file")
  (fs/create-sym-link "file-link" "file")
  (is (= true
         (fs/regular-file? "file-link")
         (fs/regular-file? "file-link" {:nofollow-links false}))
      "following links (file is a regular file)")
  (is (= false
         (fs/regular-file? "file-link" {:nofollow-links true}))
      "not following links (file-link is not a regular file)"))

(deftest regular-file?-empty-string-test
  (is (= false (fs/regular-file? ""))))

;;
;; relative?
;;
(deftest relative?-empty-string-test
  (is (= true (fs/relative? ""))))

;;
;; relativize
;;
(deftest relativize-empty-string-test
  (is (= "" (util/path->str (fs/relativize "" "")))))

;;
;; root
;;
(deftest root-test
  (doseq [[path                      expected   expected-windows]
          [[""                       nil        nil]
           ["foo"                    nil        nil]
           ["foo/bar"                nil        nil]
           ["/foo/bar"               "/"        "/"]
           ["C:/foo/bar"             nil        "C:/"]
           ["C:foo/bar"              nil        "C:"]
           ["//./PIPE/name/foo/bar"  "/"        "//./PIPE/"]
           ["//server/share/foo/bar" "/"        "//server/share/"]]]
    (if windows?
      (is (= expected-windows (some-> (fs/root path) fs/unixify))
          (str "windows: " path))
      (is (= expected (some-> (fs/root path) str))
          (str "macOS/linux: " path)))))

;;
;; same-file?
;;
(deftest same-file?-test
  (files "file1" "dir1/")
  (fs/copy "file1" "dir1")
  (fs/create-sym-link "link-file" "file1")
  (fs/create-sym-link "link-dir" "dir1")
  (is (= false (fs/same-file? "file1" "dir1/file1"))
      "a copy of a file is not the same file")
  (is (= true (fs/same-file? "file1" "file1"))
      "a file is the same as itself")
  (is (= true (fs/same-file? "link-file" "file1"))
      "a link to a file is the same as its target")
  (is (= true (fs/same-file? "link-dir" "dir1"))
      "a link to a dir is the same as its target")
  (is (= true (fs/same-file? "./dir1/../dir1/./file1" (fs/absolutize "dir1/file1")))
      "a file is the same as itself regardless of path"))

(deftest same-file?-empty-string-test
  (is (= true (fs/same-file? "" "")))
  (is (= true (fs/same-file? "." ""))))

;;
;; set-attribute
;;
(deftest set-attribute-test
  (files "afile")
  (is (= 100 (-> (fs/set-attribute "afile" "basic:lastModifiedTime" (fs/millis->file-time 100))
                 (fs/read-attributes "*") :lastModifiedTime fs/file-time->millis))))

(deftest set-attribute-sym-link-test
  (let [lmt-file (file-time "2021-01-01T00:00:00.00Z")
        lmt-link (file-time "2022-01-01T00:00:00.00Z")
        lmt-new (file-time "2023-01-01T00:00:00.00Z")
        nofollow-opts (into-array [LinkOption/NOFOLLOW_LINKS])]
    (doseq [[opts                       expected-lmt-link  expected-lmt-file  expected-exception]
            [[nil                       lmt-link           lmt-new            nil]
             [{:nofollow-links false}   lmt-link           lmt-new            nil]
             (if cant-set-last-modified-time-on-sym-link?
               [{:nofollow-links true}  lmt-link           lmt-file           FileSystemException]
               [{:nofollow-links true}  lmt-new            lmt-file           nil])]]
      (testing (str "opts: " (pr-str opts))
        (util/clean-cwd)
        (files "file")
        (fs/create-sym-link "link" "file")
        ;; use JVM API to set precondition (when we can)
        (Files/setAttribute (fs/path "file") "basic:lastModifiedTime" lmt-file nofollow-opts)
        (if cant-set-last-modified-time-on-sym-link?
          (process/shell "touch -h -d" (str lmt-link) "link")
          (Files/setAttribute (fs/path "link") "basic:lastModifiedTime" lmt-link nofollow-opts))
        ;; bb fs call (due to jdk bug, is expected to throw on some os/jdk combos)
        (is (match?
             expected-exception
             (try
               (fs/set-attribute "link" "basic:lastModifiedTime" lmt-new opts)
               nil
               (catch Throwable e
                 (class e))))
            "exception")
        ;; use JVM API to test expected result
        (is (= expected-lmt-file (Files/getAttribute (fs/path "file") "basic:lastModifiedTime" nofollow-opts))
            "file")
        (is (= expected-lmt-link (Files/getAttribute (fs/path "link") "basic:lastModifiedTime" nofollow-opts))
            "link")))))

(deftest set-attribute-empty-string-test
  (let [new-time (fs/instant->file-time (java.time.Instant/parse "2025-11-10T01:02:03.00Z"))]
    (fs/set-attribute "" "basic:lastModifiedTime" new-time)
    (is (= new-time (fs/get-attribute "" "basic:lastModifiedTime")))))

;;
;; set-creation-time 
;;
(deftest set-creation-time-test
  (files "dir/")
  (let [modify-time (fs/last-modified-time "dir")
        old-create-time (fs/creation-time "dir")
        os (util/os)
        jdk-major (util/jdk-major)
        new-create-time (fs/millis->file-time 0)]
    (fs/set-creation-time "dir" new-create-time)
    (cond
      ;; quite a storied history here
      ;; sometimes the correct creation time is returned
      (or (= :win os)
          (and (= :mac os) (> jdk-major 17)))
      (is (= new-create-time (fs/creation-time "dir")) "returns correct new creation time")
      ;; other times the modified time is returned in place of creation time
      (and (= :unix os) (< jdk-major 17))
      (is (= modify-time (fs/creation-time "dir")) "returns new modified time")
      ;; other times old creation time is returned 
      :else
      (is (= old-create-time (fs/creation-time "dir")) "returns original creation time"))))

(when (or (= :win (util/os))
          (and (= :mac (util/os)) (> (util/jdk-major) 17)))
  ;; we'll only test on envs where creation time fully works, see set-creation-time-test
  (deftest set-creation-time-sym-link-test
    (let [ct-file (file-time "2021-01-01T00:00:00.00Z")
          ct-link (file-time "2022-01-01T00:00:00.00Z")
          ct-new (file-time "2023-01-01T00:00:00.00Z")
          nofollow-opts (into-array [LinkOption/NOFOLLOW_LINKS])]
      (doseq [[opts                     expected-ct-link expected-ct-file]
              [[nil                     ct-link          ct-new]
               [{:nofollow-links false} ct-link          ct-new]
               [{:nofollow-links true}  ct-new           ct-file]]]
        (testing (str "opts: " (pr-str opts))
          (util/clean-cwd)
          (files "file")
          (fs/create-sym-link "link" "file")
          ;; use JVM API to set precondition
          (Files/setAttribute (fs/path "file") "basic:creationTime" ct-file nofollow-opts)
          (Files/setAttribute (fs/path "link") "basic:creationTime" ct-link nofollow-opts)
          ;; bb fs call
          (fs/set-creation-time "link" ct-new opts)
          ;; use JVM API to test expected result
          (is (= expected-ct-file (Files/getAttribute (fs/path "file") "basic:creationTime" nofollow-opts))
              "file")
          (is (= expected-ct-link (Files/getAttribute (fs/path "link") "basic:creationTime" nofollow-opts))
              "link"))))))

(deftest set-creation-time-empty-string-test
  ;; getting creation time on linux supported only since jdk22 and backported to jdk17 and jdk21
  ;; prior to that it returned last modified time
  ;; https://bugs.openjdk.org/browse/JDK-8316304
  ;; 
  ;; setting creation time on linux/macos not supported at this time
  ;; https://bugs.openjdk.org/browse/JDK-8151430
  (let [old-create-time (fs/creation-time "")
        new-create-time (fs/instant->file-time (java.time.Instant/parse "2025-11-10T01:02:01.00Z"))
        new-modify-time (fs/instant->file-time (java.time.Instant/parse "2025-11-10T01:02:10.00Z"))]
    (fs/set-creation-time "" new-create-time) 
    (fs/set-last-modified-time "" new-modify-time)
    (cond
      ;; quite a storied history here
      ;; sometimes the correct new creation time is returned
      (or (= :win (util/os))
          (and (= :mac (util/os)) (> (util/jdk-major) 17)))
      (is (= new-create-time (fs/creation-time "")) "returns correct new creation time")
      ;; other times the new modified time is returned in place of creation time
      (and (= :unix (util/os)) (< (util/jdk-major) 17))
      (is (= new-modify-time (fs/creation-time "")) "returns new modified time")
      ;; other times old creation time is returned 
      :else
      (is (= old-create-time (fs/creation-time "")) "returns original creation time"))))

;;
;; set-last-modified-time
;;
(deftest set-last-modified-time-test
  (files "dir/")
  (fs/set-last-modified-time "dir" 0)
  (is (= 0 (-> (fs/last-modified-time "dir")
               (fs/file-time->millis)))))

(deftest set-last-modified-time-sym-link-test
  (let [lmt-file (file-time "2021-01-01T00:00:00.00Z")
        lmt-link (file-time "2022-01-01T00:00:00.00Z")
        lmt-new (file-time "2023-01-01T00:00:00.00Z")
        nofollow-opts (into-array [LinkOption/NOFOLLOW_LINKS])]
    (doseq [[opts                       expected-lmt-link  expected-lmt-file  expected-exception]
            [[nil                       lmt-link           lmt-new            nil]
             [{:nofollow-links false}   lmt-link           lmt-new            nil]
             (if cant-set-last-modified-time-on-sym-link?
               [{:nofollow-links true}  lmt-link           lmt-file           FileSystemException]
               [{:nofollow-links true}  lmt-new            lmt-file           nil])]]
      (testing (str "opts: " (pr-str opts))
        (util/clean-cwd)
        (files "file")
        (fs/create-sym-link "link" "file")
        ;; use JVM API to set precondition (when we can)
        (Files/setAttribute (fs/path "file") "basic:lastModifiedTime" lmt-file nofollow-opts)
        (if cant-set-last-modified-time-on-sym-link?
          (process/shell "touch -h -d" (str lmt-link) "link")
          (Files/setAttribute (fs/path "link") "basic:lastModifiedTime" lmt-link nofollow-opts))
        ;; bb fs call (due to jdk bug, is expected to throw on some os/jdk combos)
        (is (match?
             expected-exception
             (try
               (fs/set-last-modified-time "link" lmt-new opts)
               nil
               (catch Throwable e
                 (class e))))
            "exception")
        ;; use JVM API to test expected result
        (is (= expected-lmt-file (Files/getAttribute (fs/path "file") "basic:lastModifiedTime" nofollow-opts))
            "file")
        (is (= expected-lmt-link (Files/getAttribute (fs/path "link") "basic:lastModifiedTime" nofollow-opts))
            "link")))))

(deftest set-last-modified-time-empty-string-test
  (let [old-time (fs/last-modified-time "")
        new-time (fs/instant->file-time (java.time.Instant/parse "2025-11-10T01:02:03.00Z"))]
    (fs/set-last-modified-time "" new-time)
    (is (not= old-time (fs/last-modified-time "")))
    (is (= new-time (fs/last-modified-time "")))))

;;
;; size
;;
(deftest size-test
  (files "dir/")
  (spit "file" "hello")
  (is (= 5 (fs/size "file")))
  (is (not (neg? (fs/size "dir")))
      "size of dirs is unspecified by underlying API"))

(deftest size-empty-string-test
  ;; non-obvious, but size works on directories, per javadocs:
  ;; The size of files that are not regular files is implementation specific and therefore unspecified.
  (is (= (fs/size ".") (fs/size ""))))

;;
;; split-ext
;;
(deftest split-ext-test
  (testing "strings"
    (is (= ["name" "clj"] (fs/split-ext "name.clj")))
    (is (= ["/path/to/file" "ext"] (fs/split-ext "/path/to/file.ext")))
    (is (= ["some/path/hi.tar" "gz"] (fs/split-ext "some/path/hi.tar.gz")))
    (is (= [".dotfile" nil] (fs/split-ext ".dotfile")))
    (is (= ["name" nil] (fs/split-ext "name"))))

  (testing "coerces paths and files"
    (is (= ["name" "clj"] (fs/split-ext (fs/file "name.clj"))))
    (is (= ["name" "clj"] (fs/split-ext (fs/path "name.clj"))))))

(deftest split-ext-empty-string-test
  (is (= ["" nil] (fs/split-ext ""))))

;;
;; split-paths
;;
(deftest split-paths-empty-string-test
  (is (= [""] (mapv util/path->str (fs/split-paths "")))))

;;
;; starts-with?
;;
(deftest starts-with?-empty-string-test
  (is (= true (fs/starts-with? "" ""))))

;;
;; strip-ext
;;
(deftest strip-ext-test
  (is (= "file-name" (fs/strip-ext "file-name.clj")))
  (is (= "file-name.html" (fs/strip-ext "file-name.html.template")))
  (is (= "file-name" (fs/strip-ext "file-name.html.template" {:ext "html.template"})))
  (is (= "file-name.html.template" (fs/strip-ext "file-name.html.template" {:ext "html"})))
  (is (= "/path/to/file-name.html" (fs/strip-ext "/path/to/file-name.html.template")))
  (is (= "path/to/file-name" (fs/strip-ext "path/to/file-name.html.template" {:ext "html.template"})))
  (is (= "/path/to/file-name.html.template" (fs/strip-ext "/path/to/file-name.html.template" {:ext "html"})))
  (is (= ".dotfile" (fs/strip-ext ".dotfile")))
  (is (= ".dotfile" (fs/strip-ext ".dotfile" {:ext "dotfile"})))
  (is (= "bin/something" (fs/strip-ext "bin/something")))
  (is (= "test-resources/dir.dot/no-ext" (fs/strip-ext "test-resources/dir.dot/no-ext"))))

(deftest strip-ext-empty-string-test
  (is (= "" (fs/strip-ext ""))))

;;
;; temp-dir
;;
(deftest temp-dir-test
  (let [tmp-dir-in-temp-dir (fs/create-temp-dir {:path (fs/temp-dir)})]
    (is (fs/starts-with? tmp-dir-in-temp-dir (fs/temp-dir)))))

;;
;; touch
;;
(deftest touch-updates-existing-file-test
  (let [lmt-file (file-time "2024-01-01T00:00:00.00Z")
        lmt-new (file-time "2026-01-01T00:00:00.00Z")
        nofollow-opts (into-array [LinkOption/NOFOLLOW_LINKS])]
    (files "file")
    ;; use JVM API to set precondition 
    (Files/setAttribute (fs/path "file") "basic:lastModifiedTime" lmt-file nofollow-opts)
    (is (= lmt-file (fs/last-modified-time "file"))
        "sanity test: file time before touch")
    (fs/touch "file")
    (let [ft (fs/last-modified-time "file")
          recent-time (file-time-recently)]
      (is (pos? (compare ft recent-time))
          (format "file time %s on/after very recent time %s" ft recent-time)))
    (fs/touch "file" {:time lmt-new})
    (is (= lmt-new (fs/last-modified-time "file"))
        "file time touched (with specified time)")))

(deftest touch-updates-existing-dir-test
  (let [lmt-dir (file-time "2024-01-01T00:00:00.00Z")
        lmt-new (file-time "2026-01-01T00:00:00.00Z") 
        nofollow-opts (into-array [LinkOption/NOFOLLOW_LINKS])]
    (files "dir/")
    ;; use JVM API to set precondition 
    (Files/setAttribute (fs/path "dir") "basic:lastModifiedTime" lmt-dir nofollow-opts)
    (is (= lmt-dir (fs/last-modified-time "dir"))
        "sanity test: dir time before touch")
    (fs/touch "dir")
    (let [dt (fs/last-modified-time "dir")
          recent-time (file-time-recently)]
      (is (pos? (compare dt recent-time))
          (format "dir time %s on/after very recent time %s" dt recent-time)))
    (fs/touch "dir" {:time lmt-new})
    (is (= lmt-new (fs/last-modified-time "dir"))
        "dir time touched (with specified time)")))

(deftest touch-creates-new-file-with-current-time-test
  (fs/touch "file")
  (let [ft (fs/last-modified-time "file")
          recent-time (file-time-recently)]
    (is (pos? (compare ft recent-time))
          (format "file time %s on/after very recent time %s" ft recent-time))))

(deftest touch-creates-new-file-with-specific-time-test
  (let [lmt-new (file-time "2024-01-01T00:00:00.00Z")]
    (fs/touch "file" {:time lmt-new})
    (is (= lmt-new (fs/last-modified-time "file"))
        "file time touched (with specified time)")))

(deftest touch-fails-fast-on-invalid-time-test
  (is (thrown? Exception  (fs/touch "file" {:time "notvalid"})))
  (is (match? [] (list-tree "."))
      "no file created"))

(deftest touch-sym-link-test
  ;; the mechanics of now vs specific time have been tested above, here we focus touching a link
  (let [lmt-file (file-time "2021-01-01T00:00:00.00Z")
        lmt-link (file-time "2022-01-01T00:00:00.00Z")
        lmt-new (file-time "2023-01-01T00:00:00.00Z")
        nofollow-opts (into-array [LinkOption/NOFOLLOW_LINKS])]
    (doseq [[opts                       expected-lmt-link  expected-lmt-file  expected-exception]
            [[nil                       lmt-link           lmt-new            nil]
             [{:nofollow-links false}   lmt-link           lmt-new            nil]
             (if cant-set-last-modified-time-on-sym-link?
               [{:nofollow-links true}  lmt-link           lmt-file           FileSystemException]
               [{:nofollow-links true}  lmt-new            lmt-file           nil])]]
      (testing (str "opts: " (pr-str opts))
        (util/clean-cwd)
        (files "file")
        (fs/create-sym-link "link" "file")
        ;; use JVM API to set precondition (when we can)
        (Files/setAttribute (fs/path "file") "basic:lastModifiedTime" lmt-file nofollow-opts)
        (if cant-set-last-modified-time-on-sym-link?
          (process/shell "touch -h -d" (str lmt-link) "link")
          (Files/setAttribute (fs/path "link") "basic:lastModifiedTime" lmt-link nofollow-opts))
        ;; bb fs call (due to jdk bug, is expected to throw on some os/jdk combos)
        (is (match?
             expected-exception
             (try
               (fs/touch "link" (assoc opts :time lmt-new))
               nil
               (catch Throwable e
                 (class e))))
            "exception")
        ;; use JVM API to test expected result
        (is (= expected-lmt-file (Files/getAttribute (fs/path "file") "basic:lastModifiedTime" nofollow-opts))
            "file")
        (is (= expected-lmt-link (Files/getAttribute (fs/path "link") "basic:lastModifiedTime" nofollow-opts))
            "link")))))

;;
;; unixify
;;
(deftest unixify-test
  (is (= "README.md" (fs/unixify "README.md")))
  (let [file "C:\\Users\\Billy\\proj\\foobar\\README.md"]
    (if windows?
      (is (= "C:/Users/Billy/proj/foobar/README.md" (fs/unixify file)))
      (is (= file (fs/unixify file))))))

(deftest unixify-empty-string-test
  (is (= "" (fs/unixify ""))))

;;
;; unzip (and zip)
;;
(deftest zip-unzip-file-test
  (files "README.md")
  (fs/zip "foo.zip" "README.md")
  (fs/unzip "foo.zip" "out-dir")
  (is (match? ["README.md"
               "foo.zip"
               "out-dir/README.md"]
              (list-tree ".")))
  (is (= (slurp "README.md") (slurp "out-dir/README.md")))
  (is (thrown? FileAlreadyExistsException (fs/unzip "foo.zip" "out-dir")))
  (spit "out-dir/README.md" "content to be replaced")
  (testing "no exception when replacing-existing option specified"
    (is (do (fs/unzip "foo.zip" "out-dir" {:replace-existing true})
            true)))
  (testing (= (slurp "README.md") (slurp "out-dir/README.md"))))

(deftest zip-unzip-zip-file-entry-order-test
  (doseq [[desc zip-entries] [["file before directories"
                               [["foo/bar/baz/boop.txt" "boop content"]
                                ["foo/"]
                                ["foo/bar/"]
                                ["foo/bar/baz/"]]]
                              ["directories before file"
                               [["foo/"]
                                ["foo/bar/"]
                                ["foo/bar/baz/"]
                                ["foo/bar/baz/boop.txt" "boop content"]]]
                              ["directories in odd order before file"
                               [["foo/bar/baz/"]
                                ["foo/bar/"]
                                ["foo/"]
                                ["foo/bar/baz/boop.txt" "boop content"]]]
                              ["no directory entries specified"
                               [["foo/bar/baz/boop.txt" "boop content"]]]]]
    (util/clean-cwd)
    (create-zip-file "foo.zip" zip-entries)
    (fs/unzip "foo.zip" ".")
    (is (match? ["foo.zip"
                 "foo/bar/baz/boop.txt"]
                (list-tree ".")) desc)))

(deftest zip-unzip-dir-test
  (files "src/dira/dirb/dirc/c1.txt"
         "src/dira/a1.txt")
  (fs/zip "foo.zip" "src")
  (fs/unzip "foo.zip" "out-dir")
  (is (match? ["out-dir/src/dira/a1.txt"
               "out-dir/src/dira/dirb/dirc/c1.txt"]
              (list-tree "out-dir"))))

(deftest zip-unzip-dir-and-file-test
  ;; NOTE: currently the API works more like unix zip than tools.build zip:
  ;;  zip out-dir/foo.zip src README.md
  (files "README.md"
         "src/foo/bar/baz.txt")
  (fs/zip "foo.zip" ["src" "README.md"])
  (fs/unzip "foo.zip" "out-dir")
  (is (match? ["out-dir/README.md"
               "out-dir/src/foo/bar/baz.txt"]
              (list-tree "out-dir"))))

(deftest zip-unzip-elide-root-parent-dir-test
  (files "src/foo/bar/baz.txt"
         "src/foo/bar/boop.txt")
  (fs/zip "foo.zip" "src" {:root "src"})
  (fs/unzip "foo.zip" "out-dir")
  (is (match? ["out-dir/foo/bar/baz.txt"
               "out-dir/foo/bar/boop.txt"]
              (list-tree "out-dir"))))

(deftest zip-unzip-extract-fn-name-key-test
  (files "README.md"
         "src/foo/bar/baz.clj"
         "src/foo/bar/boop.cljc"
         "src/foo/bar/bap.cljc/")
  (fs/zip "foo.zip" ["src" "README.md"])
  (fs/unzip "foo.zip" "out-dir" {:extract-fn #(str/ends-with? (:name %) ".clj")})
  ;; only files that have names ending in .cljc should present
  ;; directories are not subject to extract-fn
  (is (match? ["out-dir/src/foo/bar/bap.cljc/"
               "out-dir/src/foo/bar/baz.clj"]
              (list-tree "out-dir"))))

(deftest zip-unzip-extract-fn-entry-key-test
  (files "LICENSE" "README.md")
  (let [readme-time (.toEpochMilli (java.time.Instant/parse "2026-02-25T23:24:25Z"))
        license-time (- readme-time 1000)]
    (fs/set-last-modified-time "README.md" readme-time)
    (fs/set-last-modified-time "LICENSE" license-time)
    (let [zip-entry-times (atom {})]
      (fs/zip "foo.zip" ["LICENSE" "README.md"])
      ;; record zip entry times while extracting to out-dir1
      (fs/unzip "foo.zip" "out-dir1"
                {:extract-fn #(let [time (.getTime ^java.util.zip.ZipEntry (:entry %))]
                                (swap! zip-entry-times assoc (:name %) time)
                                true)})
      (is (match? {"README.md" readme-time
                   "LICENSE" license-time}
                  @zip-entry-times) "zip entry times match source file times")
      (is (match? ["out-dir1/LICENSE"
                   "out-dir1/README.md"]
                  (list-tree "out-dir1")))
      ;; extract files to out-dir2 that have the same time as README.md
      (fs/unzip "foo.zip" "out-dir2"
                {:extract-fn #(= (.getTime ^java.util.zip.ZipEntry (:entry %)) readme-time)})
      (is (match? ["out-dir2/README.md"]
                  (list-tree "out-dir2"))))))

(deftest zip-should-not-zip-self-test
  (files "foo/bar/baz/somefile.txt")
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
  (is (match? ["zip-out/foo/bar/baz/somefile.txt"]
              (list-tree "zip-out"))
      "all files except zip file zipped"))

(deftest zip-unzip-empty-string-test
  (files "da1/da2/da3/da4/f2.ext" "f1.ext")
  (let [before (util/fsnapshot)
        ;; zip to temp-dir instead of cwd, so we don't zip our zip
        dest-zip (fs/file (fs/temp-dir) "foo.zip")]
    (fs/zip dest-zip "" {:root ""})
    (is (thrown? java.nio.file.FileAlreadyExistsException (fs/unzip dest-zip "")))
    (fs/unzip dest-zip "" {:replace-existing true})
    (is (match? (mapv #(update % :attr dissoc :creationTime :lastModifiedTime :lastAccessTime)
                      before)
                (mapv #(update % :attr dissoc :creationTime :lastModifiedTime :lastAccessTime)
                      (util/fsnapshot))))))

;;
;; update-file
;;
(deftest update-file-test
  (testing "Throws if file doesn't exist"
    (is (thrown? FileNotFoundException (= "foooo" (fs/update-file "nope.txt" str "foooo")))))
  (let [file "file1.txt"]
    (spit file "foo")
    (is (= "foobar" (fs/update-file file #(str % "bar"))))
    (is (= "foobar" (slurp file)))
    (is (= "foobarbazbatcat" (fs/update-file file str "baz" "bat" "cat")))
    (is (= "foobarbazbatcat" (slurp file)))
    (let [new-val (fs/update-file file str (rand))]
      (is (= new-val (slurp file)))))
  (let [file "file2.txt"]
    (spit file ", ")
    (is (= "foo, bar, baz" (fs/update-file file str/join ["foo" "bar" "baz"]))))
  (let [file "file3.txt"]
    (spit file "foo")
    (is (= "foobar" (fs/update-file (fs/path file) str "bar")))))

(deftest update-file-empty-string-test
  ;; makes sense, "" is the cwd, not a regular file
  (is (thrown? java.io.FileNotFoundException (fs/update-file "" str "updated"))))

;;
;; walk-file-tree
;;
(deftest walk-test
  (files "f0.ext"
         "da1/f1.ext"
         "da1/da2/f2.ext"
         "da1/da2/da3/f3.ext"
         "da1/da2/da3/da4/")
  (testing "full depth"
    (let [walked-dirs (volatile! [])
          walked-files (volatile! [])]
      (fs/walk-file-tree "." {:post-visit-dir (fn [d _] (vswap! walked-dirs conj d) :continue)
                              :visit-file (fn [f _] (vswap! walked-files conj f) :continue)})
      (is (match? ["./"
                   "./da1/"
                   "./da1/da2/"
                   "./da1/da2/da3/"
                   "./da1/da2/da3/da4/"]
                  (normalized @walked-dirs)))
      (is (match? ["./da1/da2/da3/f3.ext"
                   "./da1/da2/f2.ext"
                   "./da1/f1.ext"
                   "./f0.ext"]
                  (normalized @walked-files)))))
  (testing "max-depth 2"
    (let [walked-dirs (volatile! [])
          walked-files (volatile! [])]
      (fs/walk-file-tree "." {:post-visit-dir (fn [d _] (vswap! walked-dirs conj d) :continue)
                              :visit-file (fn [f _] (vswap! walked-files conj f) :continue)
                              :max-depth 2})
      (is (match? ["./" "./da1/"]
                  (normalized @walked-dirs)))
      (is (match? ["./da1/da2/" ;; notice that non-descended dirs are matched as files
                   "./da1/f1.ext"
                   "./f0.ext"]
                  (normalized @walked-files)))))
  (testing "max-depth 0"
    (let [walked-dirs (volatile! [])
          walked-files (volatile! [])]
      (fs/walk-file-tree "." {:post-visit-dir (fn [d _] (vswap! walked-dirs conj d) :continue)
                              :visit-file (fn [f _] (vswap! walked-files conj f) :continue)
                              :max-depth 0})
      (is (match? [] (normalized @walked-dirs)))
      (is (match? ["./"] (normalized @walked-files)))))
  (is (fs/walk-file-tree "." {:pre-visit-dir (fn [_ _] :terminate)}))
  (is (fs/walk-file-tree "." {:pre-visit-dir (fn [_ _] java.nio.file.FileVisitResult/TERMINATE)}))
  (is (thrown-with-msg?
       Exception #":continue, :skip-subtree, :skip-siblings, :terminate"
       (fs/walk-file-tree "." {:pre-visit-dir (fn [_ _])}))))

(deftest walk-file-tree-empty-string-test
  (files "da1/da2/da3/da4/f2.ext" "f1.ext")
  (let [files (atom [])]
    (fs/walk-file-tree "" {:visit-file (fn [f _attrs]
                                         (swap! files conj f)
                                         :continue)})
    (is (= ["da1/da2/da3/da4/f2.ext" "f1.ext"] (normalized @files)))))

;;
;; which
;;
(deftest which-empty-string-test
  (is (nil? (fs/which ""))))

;;
;; which-all
;; 
(deftest which-all-empty-string-test
  (is (= [] (fs/which-all ""))))

;;
;; with-temp-dir
;;
(deftest with-temp-dir-test
  (let [capture-dir (volatile! nil)]
    (testing "with-temp-dir"
      (fs/with-temp-dir [dir {:prefix "with-temp-dir-test"}]
        (vreset! capture-dir dir)
        (testing "creates a directory with the given options"
          (is (fs/exists? dir))
          (is (str/starts-with? (fs/file-name (str dir)) "with-temp-dir-test")))
        (fs/create-file (fs/path dir "xx"))
        (is (fs/exists? (fs/path dir "xx"))))
      (testing "deletes its directory and contents on exit from the scope"
        (is (not (fs/exists? (fs/path @capture-dir "xx"))))
        (is (not (fs/exists? @capture-dir))))
      (fs/with-temp-dir [dir {:keep true}]
        (vreset! capture-dir dir))
      (testing "does NOT delete directory with :keep true"
        (is (fs/exists? (fs/path @capture-dir)))
        (fs/delete-tree @capture-dir {:force true})))))

(deftest with-temp-dir-read-only-test
  (let [capture-dir (volatile! nil)]
    (fs/with-temp-dir [tmp-dir {:prefix "with-temp-dir-read-only-test"}]
      (vreset! capture-dir tmp-dir)
      (let [dir (fs/path tmp-dir "my-dir")
            file (fs/path tmp-dir "my-dir" "my-file")
            _ (fs/create-dir dir)
            _ (fs/create-file file)]
        (if windows?
          (do
            (.setWritable (fs/file file) false)
            (.setWritable (fs/file dir) false))
          (do
            (fs/set-posix-file-permissions file "r--r--r--")
            (fs/set-posix-file-permissions dir "r--r--r--")))))
    (testing "deletes its directory and contents (read-only) on exit from the scope"
      (is (not (fs/exists? (fs/path @capture-dir "my-dir" "my-file"))))
      (is (not (fs/exists? (fs/path @capture-dir "my-dir"))))
      (is (not (fs/exists? @capture-dir))))))

;;
;; writable?
;;
(deftest writable?-test
  (files "dir" "file.txt")
  (is (= true (fs/writable? "dir")))
  (is (= true (fs/writable? "file.txt")))

  (.setWritable (fs/file "dir") false)
  (.setWritable (fs/file "file.txt") false)

  (is (= false (fs/writable? "dir")))
  (is (= false (fs/writable? "file.txt"))))

(deftest writable?-empty-string-test
  (is (true? (fs/writable? ""))))

;;
;; write-bytes 
;;
(deftest write-bytes-test
  (fs/write-bytes "file.bin" (.getBytes (String. "foo")))
  (is (= "foo" (String. (fs/read-all-bytes "file.bin"))))
  (fs/write-bytes "file.bin" (.getBytes (String. "bar")))
  (is (= "bar" (String. (fs/read-all-bytes "file.bin")))
      "existing file overwritten")
  (fs/write-bytes "file.bin" (.getBytes (String. "baz")) {:append true})
  (is (= "barbaz" (String. (fs/read-all-bytes "file.bin")))
      "existing file appended to"))

(deftest write-bytes-empty-string-test
  (is (thrown? FileSystemException (fs/write-bytes "" (.getBytes (String. "foo"))))))

;;
;; write-lines
;;
(deftest write-lines-test
  (fs/write-lines "file.txt" (repeat 3 "foo"))
  (is (= (repeat 3 "foo") (fs/read-all-lines "file.txt")))
  (fs/write-lines "file.txt" (repeat 3 "bar"))
  (is (= (repeat 3 "bar") (fs/read-all-lines "file.txt"))
      "existing file overwritten")
  (fs/write-lines "file.txt" (repeat 3 "baz") {:append true})
  (is (= (into (vec (repeat 3 "bar")) (repeat 3 "baz"))
         (fs/read-all-lines "file.txt"))
      "existing file appended to"))

(deftest write-lines-empty-string-test
  (is (thrown? FileSystemException (fs/write-lines "" ["foo"]))))

;;
;; xdg-*
;;
(deftest xdg-*-home-test
  (let [default-path (fs/path (fs/home) ".config")]
    (testing "yields path based on value of XDG_*_HOME env-var if present"
      (let [custom-path (if windows? "C:\\some\\path" "/some/path")]
        (with-redefs [fs/get-env {"XDG_CONFIG_HOME" custom-path}]
          (is (= (fs/path custom-path)
                 (fs/xdg-config-home)))
          (is (= (fs/path custom-path "clj-kondo")
                 (fs/xdg-config-home "clj-kondo"))))))
    (testing "yields default-path when env-var contains no absolute path"
      (with-redefs [fs/get-env {"XDG_CONFIG_HOME" ""}]
        (is (= default-path
               (fs/xdg-config-home)))))))

(deftest xdg-config-home-test
  (let [default-home (fs/path (fs/home) ".config")]
    (is (= default-home
           (fs/xdg-config-home)))
    (is (= (fs/path default-home "clj-kondo")
           (fs/xdg-config-home "clj-kondo")))))

(deftest xdg-cache-home-test
  (let [default-home (fs/path (fs/home) ".cache")]
    (is (= default-home
           (fs/xdg-cache-home)))
    (is (= (fs/path default-home "clj-kondo")
           (fs/xdg-cache-home "clj-kondo")))))

(deftest xdg-data-home-test
  (let [default-home (fs/path (fs/home) ".local" "share")]
    (is (= default-home
           (fs/xdg-data-home)))
    (is (= (fs/path default-home "clj-kondo")
           (fs/xdg-data-home "clj-kondo")))))

(deftest xdg-state-home-test
  (let [default-home (fs/path (fs/home) ".local" "state")]
    (is (= default-home
           (fs/xdg-state-home)))
    (is (= (fs/path default-home "clj-kondo")
           (fs/xdg-state-home "clj-kondo")))))
