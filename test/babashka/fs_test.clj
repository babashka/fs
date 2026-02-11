(ns babashka.fs-test
  (:require
   [babashka.fs :as fs]
   [babashka.fs-test-util :as util]
   [babashka.test-report]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [matcher-combinators.matchers :as m]
   [matcher-combinators.test])
  (:import [java.io FileNotFoundException]))

(set! *warn-on-reflection* true)

(use-fixtures :each
  (fn [f]
    (util/clean-cwd)
    ;; tests are currently use source tree as test scenario, copy what we need
    (doseq [f [".gitignore" "project.clj" "LICENSE" "README.md"]]
      (fs/copy (fs/file "../.." f) f))
    (doseq [f ["src" "test" "test-resources"]]
      (fs/copy-tree (fs/file "../.." f) f))
    (f)))

(def windows? (-> (System/getProperty "os.name")
                  (str/lower-case)
                  (str/starts-with? "windows")))

(defn temp-dir []
  (-> (fs/create-temp-dir)
      (fs/delete-on-exit)))

(defn- files [& files]
  (util/clean-cwd)
  (doseq [f files]
    (if (str/ends-with? f "/")
      (fs/create-dirs f)
      (let [d (fs/parent f)]
        (when d (fs/create-dirs d))
        (spit f f)))))

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
      (is (match? (m/in-any-order
                    ["." "./da1" "./da1/da2" "./da1/da2/da3" "./da1/da2/da3/da4"])
                  (map fs/unixify @walked-dirs)))
      (is (match? (m/in-any-order ["./f0.ext"
                                   "./da1/f1.ext"
                                   "./da1/da2/f2.ext"
                                   "./da1/da2/da3/f3.ext"])
                  (map fs/unixify @walked-files)))))
  (testing "max-depth 2"
    (let [walked-dirs (volatile! [])
          walked-files (volatile! [])]
      (fs/walk-file-tree "." {:post-visit-dir (fn [d _] (vswap! walked-dirs conj d) :continue)
                              :visit-file (fn [f _] (vswap! walked-files conj f) :continue)
                              :max-depth 2})
      (is (match? (m/in-any-order
                   ["." "./da1"])
                  (map fs/unixify @walked-dirs)))
      (is (match? (m/in-any-order ["./f0.ext"
                                   "./da1/f1.ext"
                                   "./da1/da2" ;; notice that non-descended dirs are matched as files
                                   ])
                  (map fs/unixify @walked-files)))))
  (testing "max-depth 0"
    (let [walked-dirs (volatile! [])
          walked-files (volatile! [])]
      (fs/walk-file-tree "." {:post-visit-dir (fn [d _] (vswap! walked-dirs conj d) :continue)
                              :visit-file (fn [f _] (vswap! walked-files conj f) :continue)
                              :max-depth 0})
      (is (match? [] (map fs/unixify @walked-dirs)))
      (is (match? ["."] (map fs/unixify @walked-files)))))
  (is (fs/walk-file-tree "." {:pre-visit-dir (fn [_ _] :terminate)}))
  (is (fs/walk-file-tree "." {:pre-visit-dir (fn [_ _] java.nio.file.FileVisitResult/TERMINATE)}))
  (is (thrown-with-msg?
       Exception #":continue, :skip-subtree, :skip-siblings, :terminate"
       (fs/walk-file-tree "." {:pre-visit-dir (fn [_ _])}))))

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
      (is (match? (m/in-any-order ["README.md" "dira1/dirb1/README.md"]) (map fs/unixify readme-match)))
      (is (every? #(instance? java.nio.file.Path %) readme-match))))
  (testing "match multiple recursive by extension"
    (is (match? (m/in-any-order ["project.clj"
                                 "dira1/dirb1/source.clj"
                                 "dira2/dirb1/test.cljc"])
                (map fs/unixify
                     (fs/match "." "regex:.*\\.cljc?" {:recursive true})))))
  (testing "match also matches directories and doesn't return the root directory"
    (is (match? (m/in-any-order
                 ["dira1/dirb1/dirc1"
                  "dira1/dirb1/README.md"
                  "dira1/dirb1/source.clj"])
                (map fs/unixify
                     (fs/match "dira1/dirb1" "regex:.*" {:recursive true}))))
    (is (match? (m/in-any-order
                 ["dira1/dirb1"
                  "dira1/dirb1/dirc1"
                  "dira1/dirb1/README.md"
                  "dira1/dirb1/source.clj"])
                (map fs/unixify
                     (fs/match "dira1" "regex:dirb1.*" {:recursive true})))))
  (when-not windows?
    (testing "symlink as root path"
      (let [sym-link (fs/create-sym-link "sym-link" "dira1")
            target (fs/read-link sym-link)]
        (is (= (str target) "dira1"))
        (is (match? [] (fs/match sym-link "regex:.*")))
        (is (match? ["sym-link/foo.txt"]
                    (map fs/unixify (fs/match sym-link "regex:.*" {:follow-links true}))))
        (is (match? ["dira1/foo.txt"]
                    (map fs/unixify (fs/match (fs/read-link sym-link) "regex:.*"))))))))

(deftest match-at-specific-depth-test
  (files "foo/bar/baz/dude.txt")
  (is (match? ["foo/bar/baz/dude.txt"]
              (map fs/unixify
                   (if windows?
                     (fs/match "." "regex:foo\\\\bar\\\\baz\\\\.*" {:recursive true})
                     (fs/match "." "regex:foo/bar/baz/.*" {:recursive true}))))))

(when-not windows?
  (deftest match-on-root-with-special-chars-test
    (files "some-dir/foo*{[]}/test.txt")
    (is (match? ["some-dir/foo*{[]}/test.txt"]
                (map str (fs/match "some-dir/foo*{[]}" "glob:*.txt"))))
    (is (match? ["some-dir/foo*{[]}/test.txt"]
                (map str (fs/match "some-dir/foo*{[]}" "regex:.*\\.txt"))))))

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
      (is (match? (m/in-any-order ["README.md" "dira1/dirb1/README.md"]) (map fs/unixify readme-match)))
      (is (every? #(instance? java.nio.file.Path %) readme-match))))
  (testing "glob ** but disable recursion"
    (let [readme-match (fs/glob "." "**README.md" {:recursive false})]
      (is (match? (m/in-any-order ["README.md"]) (map fs/unixify readme-match)))
      (is (every? #(instance? java.nio.file.Path %) readme-match))))
  (testing "glob recursive by extension"
    (is (match? (m/in-any-order
                 ["project.clj"
                  "dira1/dirb1/source.clj"
                  "dira2/dirb1/test.cljc"])
                (map fs/unixify
                     (fs/glob "." "**.{clj,cljc}")))))
  (testing "glob also matches directories and doesn't return the specified root directory"
    (is (match? (m/in-any-order
                 ["dira1/dirb1/dirc1"
                  "dira1/dirb1/README.md"
                  "dira1/dirb1/source.clj"])
                (map fs/unixify
                     (fs/glob "dira1/dirb1" "**"))))
    (is (match? (m/in-any-order
                 ["dira1/dirb1"
                  "dira1/dirb1/dirc1"
                  "dira1/dirb1/README.md"
                  "dira1/dirb1/source.clj"])
                (map fs/unixify
                     (fs/glob "dira1" "dirb1**")))))
  (when-not windows?
    (testing "symlink as root path"
      (let [sym-link (fs/create-sym-link "sym-link" "dira1")]
        (is (match? [] (fs/glob sym-link "*")))
        (is (match? ["sym-link/foo.txt"]
                    (map fs/unixify
                         (fs/glob sym-link "*" {:follow-links true}))))
        (is (match? ["dira1/foo.txt"]
                    (map fs/unixify
                         (fs/glob (fs/read-link sym-link) "*"))))))
    (testing "hidden files"
      (testing "are not matched by default"
        (is (match? [] (map fs/unixify
                            (fs/glob "." "*git*")))))
      (testing "matched when :hidden option specified"
        (is (match? [".gitignore"]
                    (map fs/unixify
                         (fs/glob "." "*git*" {:hidden true})))))
      (testing "automatically matched when pattern starts with a dot"
        (is (match? [".gitignore"]
                    (map fs/unixify
                         (fs/glob "." ".gitig*"))))))))

(deftest glob-with-specific-depth-test
  (files "foo/bar/baz/dude.txt")
  (is (match? ["foo/bar/baz/dude.txt"]
              (map fs/unixify
                   (if windows?
                     (fs/glob "." "foo\\\\bar\\\\baz\\\\*")
                     (fs/glob "." "foo/bar/baz/*"))))))

(deftest glob-windows-friendly-test
  (files "foo/bar/baz/dude.clj"
         "foo/bar/baz/dude2.clj")
  (is (match? (m/in-any-order
               ["foo/bar/baz/dude.clj"
                "foo/bar/baz/dude2.clj"])
              (map fs/unixify
                   (fs/glob "." "foo/bar/baz/*.clj")))))

(deftest glob-returns-directories-test
  (files "foo/")
  (is (match? ["foo"] (map fs/unixify
                           (fs/glob "." "*" {:max-depth 1})))))

(deftest create-dir-test
  (files)
  (is (fs/create-dir "foo"))
  (is (match? ["foo"] (map fs/unixify
                           (fs/glob "." "**"))))
  (is (fs/directory? "foo")))

(deftest create-link-test
  (when-not windows?
    (files "dudette.txt")
    (let [link (fs/create-link (fs/file "." "hard-link.txt") (fs/file "dudette.txt"))]
      (is (.exists (fs/file link)))
      (is (= 2 (fs/get-attribute (fs/file link) "unix:nlink")))
      (is (.exists (fs/file "dudette.txt")))
      (is (fs/same-file? (fs/file "dudette.txt")
                         (fs/file "hard-link.txt")))
      (is (= (slurp (fs/file "hard-link.txt"))
             (slurp (fs/file "dudette.txt")))))))

(deftest directory?-test
  (files "dir/file.txt")
  (is (= true (fs/directory? "dir")))
  (is (= false (fs/directory? "dir/file.txt")))
  (is (= false (fs/directory? "idontexist")))
  (is (= false (fs/directory? (fs/path "dir" "idontexist")))))

(deftest regular-file?-test
  (files "dir/file.txt")
  (is (= false (fs/regular-file? "dir")))
  (is (= true (fs/regular-file? "dir/file.txt")))
  (is (= false (fs/regular-file? "idontexist")))
  (is (= false (fs/regular-file? (fs/path "dir" "idontexist")))))

(deftest parent-test
  (is (= (fs/path "dir") (fs/parent "dir/foo")))
  (is (= nil (fs/parent "foo"))))

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

(deftest file-name-test
  (let [f "some-dir/foo.ext"]
    (is (= "foo.ext" (fs/file-name f)))
    (is (= "foo.ext" (fs/file-name (fs/file f))))
    (is (= "foo.ext" (fs/file-name (fs/path f))))))

(deftest path-test
  (let [p (fs/path "foo" "bar" (io/file "baz"))]
    (is (instance? java.nio.file.Path p))
    (is (= "foo/bar/baz" (fs/unixify p)))))

(deftest file-test
  (let [f (fs/file "foo" "bar" (fs/path "baz"))]
    (is (instance? java.io.File f))
    (is (= "foo/bar/baz" (fs/unixify f)))))

(deftest copy-to-file-test
  (files "file" "dest-dir/")
  (fs/copy "file" "dest-dir/file")
  (is (match? (m/in-any-order
               ["file" "dest-dir" "dest-dir/file"])
              (map fs/unixify (fs/glob "." "**")))))

(deftest copy-into-dir-test
  (files "file" "dest-dir/")
  (fs/copy "file" "dest-dir")
  (is (match? (m/in-any-order
               ["file" "dest-dir" "dest-dir/file"])
              (map fs/unixify (fs/glob "." "**")))))

(deftest copy-input-stream-test
  (files "file" "dest-dir/")
  (with-open [is (io/input-stream (fs/file "file"))]
    (fs/copy is "dest-dir/file"))
  (is (match? (m/in-any-order
               ["file" "dest-dir" "dest-dir/file"])
              (map fs/unixify (fs/glob "." "**")))))

(deftest copy-tree-test
  (files "src-dir/foo.txt"
         "src-dir/.foo"
         "src-dir/a/a.txt"
         "src-dir/a/b/b.txt"
         "src-dir/a/b/c")
  (fs/copy-tree "src-dir" "dest-dir")
  (is (match? (m/in-any-order
               ["dest-dir/foo.txt"
                "dest-dir/.foo"
                "dest-dir/a"
                "dest-dir/a/a.txt"
                "dest-dir/a/b"
                "dest-dir/a/b/b.txt"
                "dest-dir/a/b/c"])
              (map fs/unixify (fs/glob "dest-dir" "**" {:hidden true})))))

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
  (is (match? (m/in-any-order
               ["src-dir"
                "src-dir/foo"
                "src-dir/foo/file.txt"
                "dest-dir"
                 ;; our new entries
                "dest-dir/foo2"
                "dest-dir/foo2/foo"
                "dest-dir/foo2/foo/file.txt"])
              (map fs/unixify (fs/glob "." "**")))))

(deftest copy-tree-nested-ro-dir-test
  (files "src-dir/foo/bar/")
  ;; https://github.com/babashka/fs/issues/122
  (.setReadOnly (fs/file "src-dir" "foo"))
  (fs/copy-tree  "src-dir" "dest-dir")
  (is (match? (m/in-any-order
               ["src-dir"
                "src-dir/foo"
                "src-dir/foo/bar"
                 ;; our new entries
                "dest-dir"
                "dest-dir/foo"
                "dest-dir/foo/bar"])
              (map fs/unixify (fs/glob "." "**"))))
  (when (not windows?)
    ;; you can always write to directories on Windows, even if they are read-only
    ;; https://answers.microsoft.com/en-us/windows/forum/all/all-folders-are-now-read-only-windows-10/0ca1880f-e997-46af-bd85-042a53fc078e
    (is (not (fs/writable? "dest-dir/foo")))))

(deftest components-test
  (is (match? ["foo" "bar" "baz" "bop.txt"]
              (map str (fs/components "foo/bar/baz/bop.txt")))))

(deftest list-dir-test
  (files "file.txt"
         "source1.clj"
         "source2.clj"
         "dir1/"
         "dir2/foo.txt")
  (is (match? (m/in-any-order
               ["./file.txt"
                "./source1.clj"
                "./source2.clj"
                "./dir1"
                "./dir2"])
              (map fs/unixify (fs/list-dir "."))))
  (is (match? (m/in-any-order
               ["./dir1"
                "./dir2"])
              (map fs/unixify (fs/list-dir "." (fn accept [x] (fs/directory? x))))))
  (is (match? [] (fs/list-dir "." (fn accept [_] false))))
  (is (match? (m/in-any-order
               ["./source1.clj"
                "./source2.clj"])
              (map fs/unixify (fs/list-dir "." "*.clj")))))

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

(deftest delete-tree-test
  (files "foo/bar/baz/file.txt")
  (fs/delete-tree "foo")
  (is (match? [] (fs/glob "." "**"))))

(deftest delete-tree-nested-test
  (files "foo/bar/baz/file.txt")
  (fs/delete-tree "foo/bar/baz")
  (is (match? (m/in-any-order
               ["foo"
                "foo/bar"])
              (map fs/unixify (fs/glob "." "**")))))

(deftest delete-tree-ok-if-dir-missing-test
  (files)
  (is (do (fs/delete-tree "foo")
          true))
  (is (do (fs/delete-tree "foo/bar/baz")
          true)))

(when (not windows?)
  (deftest delete-tree-does-not-follow-symlink-test
    (files "dir1/"
           "dir2/foo")
    (fs/create-sym-link "dir1/link-to-dir2" "../dir2")
    (is (= true (fs/same-file? "dir1/link-to-dir2" "dir2")) "precondition: link")
    (is (match? (m/in-any-order
                 ["dir1"
                  "dir1/link-to-dir2"
                  "dir2"
                  "dir2/foo"])
                (map fs/unixify (fs/glob "." "**"))) "precondition: files")
    (fs/delete-tree "dir1")
    (is (match? (m/in-any-order
                 ["dir2"
                  "dir2/foo"])
                (map fs/unixify (fs/glob "." "**"))))))

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

(deftest move-to-file-test
  (files "src-dir/foo.txt"
         "dest-dir/")
  (let [foo-content (str/trim (slurp "src-dir/foo.txt"))]
    (fs/move "src-dir/foo.txt" "dest-dir/foo.txt")
    (is (match? (m/in-any-order
                  ["src-dir"
                   "dest-dir"
                   "dest-dir/foo.txt"])
                (map fs/unixify (fs/glob "." "**") ))
    (is (= foo-content (str/trim (slurp "dest-dir/foo.txt")))))))

(deftest move-to-dir-test
  (files "src-dir/foo.txt"
         "dest-dir/")
  (let [foo-content (str/trim (slurp "src-dir/foo.txt"))]
    (fs/move "src-dir/foo.txt" "dest-dir")
    (is (match? (m/in-any-order
                  ["src-dir"
                   "dest-dir"
                   "dest-dir/foo.txt"])
                (map fs/unixify (fs/glob "." "**") ))
    (is (= foo-content (str/trim (slurp "dest-dir/foo.txt")))))))

(deftest set-attribute-test
  (files "afile")
  (is (= 100 (-> (fs/set-attribute "afile" "basic:lastModifiedTime" (fs/millis->file-time 100))
                 (fs/read-attributes "*") :lastModifiedTime fs/file-time->millis))))

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

(deftest predicate-test
  (is (boolean? (fs/readable? (fs/path "."))))
  (is (boolean? (fs/writable? (fs/path ".")))))

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

(deftest writable?-test
  (files "dir" "file.txt")
  (is (= true (fs/writable? "dir")))
  (is (= true (fs/writable? "file.txt")))

  (.setWritable (fs/file "dir") false)
  (.setWritable (fs/file "file.txt") false)

  (is (= false (fs/writable? "dir")))
  (is (= false (fs/writable? "file.txt"))))

(deftest normalize-test
  (is (= "foo/bar/baz" (fs/unixify (fs/normalize "foo/bar/baz"))))
  (is (= "foo/bar/baz" (fs/unixify (fs/normalize "./foo/./bing/./boop/.././../bar/./baz/.")))))

(deftest temp-dir-test
  (let [tmp-dir-in-temp-dir (fs/create-temp-dir {:path (fs/temp-dir)})]
    (is (fs/starts-with? tmp-dir-in-temp-dir (fs/temp-dir)))))

(deftest ends-with?-test
  (is (= true (fs/ends-with? "one/two/three" "three")))
  (is (= true (fs/ends-with? "one/two/three" "two/three")))
  (is (= true (fs/ends-with? "one/two/three" "one/two/three")))
  (is (= false (fs/ends-with? "one/two/three" "one/three"))))

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

(deftest delete-if-exists-test
  (files "dude")
  (is (= true (fs/delete-if-exists "dude")))
  (is (= false (fs/delete-if-exists "dude"))))

(deftest size-test
  (files "dir/")
  (spit "file" "hello")
  (is (= 5 (fs/size "file")))
  (is (not (neg? (fs/size "dir")))
      "size of dirs is unspecified by underlying API"))

(deftest set-posix-test
  (when-not windows?
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

(deftest read-all-bytes-test
  (spit "README.md" "some\ncontent\nhere")
  (let [bs (fs/read-all-bytes "README.md")]
    (is (bytes? bs))
    (is (= (fs/size "README.md") (count bs)))))

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

(deftest get-attribute-test
  (let [lmt (fs/get-attribute "." "basic:lastModifiedTime")]
    (is lmt)
    (is (= lmt (fs/last-modified-time ".")))))

(deftest file-time-test
  (let [lmt (fs/get-attribute "." "basic:lastModifiedTime")]
    (is (instance? java.time.Instant (fs/file-time->instant lmt)))
    (is (= lmt (fs/instant->file-time (fs/file-time->instant lmt)))))
  (let [tmpdir (temp-dir)
        _ (fs/set-last-modified-time tmpdir 0)]
    (is (= 0 (-> (fs/last-modified-time tmpdir)
                 (fs/file-time->millis)))))
  (let [tmpdir (temp-dir)
        _ (fs/set-creation-time tmpdir 0)]
    ;; macOS doesn't allow you to alter the creation time
    (is (is (number? (-> (fs/creation-time tmpdir)
                         (fs/file-time->millis)))))))

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

(deftest extension-test
  (is (= "clj" (fs/extension "file-name.clj")))
  (is (= "template" (fs/extension "file-name.html.template")))
  (is (nil? (fs/extension ".dotfile")))
  (is (nil? (fs/extension "bin/something"))))

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

(deftest modified-since-test
  (testing "with sleep"
    (let [td0 (fs/create-temp-dir)
          anchor (fs/file td0 "f0")
          _ (spit anchor "content")
          _ (Thread/sleep 50)
          td1 (fs/create-temp-dir)
          f1 (fs/file td1 "f1")
          _ (spit f1 "content")
          f2 (fs/file td1 "f2")
          _ (spit f2 "content")]
      (is (= #{f1} (into #{} (map fs/file (fs/modified-since anchor f1)))))
      (is (= #{f1 f2} (into #{} (map fs/file (fs/modified-since anchor td1)))))
      (is (= #{f1 f2} (into #{} (map fs/file (fs/modified-since td0 td1)))))
      (fs/set-last-modified-time anchor (fs/last-modified-time f1))
      (is (not (seq (fs/modified-since anchor f1))))))
  (testing "without sleep"
    (let [td0 (fs/create-temp-dir)
          anchor (fs/file td0 "f0")
          now (java.time.Instant/now)
          _ (spit anchor "content")
          _ (fs/set-last-modified-time anchor now)
          later (.plusNanos (java.time.Instant/now) 10000)
          td1 (fs/create-temp-dir)
          f1 (fs/file td1 "f1")
          _ (spit f1 "content")
          _ (fs/set-last-modified-time f1 later)
          f2 (fs/file td1 "f2")
          _ (spit f2 "content")
          _ (fs/set-last-modified-time f2 later)]
      (is (= #{f1} (into #{} (map fs/file (fs/modified-since anchor f1)))))
      (is (= #{f1 f2} (into #{} (map fs/file (fs/modified-since anchor td1)))))
      (is (= #{f1 f2} (into #{} (map fs/file (fs/modified-since td0 td1)))))
      (fs/set-last-modified-time anchor (fs/last-modified-time f1))
      (is (not (seq (fs/modified-since anchor f1)))))))

(deftest zip-unzip-test
  (let [td (fs/create-temp-dir)
        td-out (fs/path td "out")
        zip-file (fs/path td "foo.zip")
        _ (fs/zip zip-file "README.md")]
    (fs/unzip zip-file td-out)
    (is (fs/exists? (fs/path td-out "README.md")))
    (is (= (slurp "README.md") (slurp (fs/file td-out "README.md"))))
    (is (thrown? java.nio.file.FileAlreadyExistsException (fs/unzip zip-file td-out)))
    (testing "no exception when replacing existing"
      (is (do (fs/unzip zip-file td-out {:replace-existing true})
              true))))
  (testing "Entry within directory can become before directory"
    (let [td (fs/create-temp-dir)
          td-out (fs/path td "out")
          zip-file (fs/path "test-resources" "bencode-1.1.0.jar")]
      (fs/unzip zip-file td-out)
      (is (fs/exists? (fs/file td-out "bencode" "core.clj")))))
  (testing "Zip directory"
    (let [td (fs/create-temp-dir)
          td-out (fs/path td "out")
          zip-file (fs/path td "foo.zip")]
      (fs/zip zip-file "src")
      (fs/unzip zip-file td-out)
      (is (fs/exists? (fs/file td-out "src")))))
  (testing "Zip directory and file"
    ;; NOTE: currently the API works more like unix zip than tools.build zip:
    ;;  zip /tmp/out2.zip src README.md
    (let [td (fs/create-temp-dir)
          td-out (fs/path td "out")
          zip-file (fs/path td "foo.zip")]
      (fs/zip zip-file ["src" "README.md"])
      (fs/unzip zip-file td-out)
      (is (fs/exists? (fs/file td-out "src")))
      (is (fs/exists? (fs/file td-out "README.md")))))
  (testing "Elide parent dir"
    (let [td (fs/create-temp-dir)
          td-out (fs/path td "out")
          zip-file (fs/path td "foo.zip")]
      (fs/zip zip-file "src" {:root "src"})
      (fs/unzip zip-file td-out)
      (is (not (fs/exists? (fs/file td-out "src"))))
      (is (fs/exists? (fs/file td-out "babashka")))
      (is (fs/directory? (fs/file td-out "babashka")))
      (is (fs/exists? (fs/file td-out "babashka" "fs.cljc")))
      (is (not (fs/directory? (fs/file td-out "babashka" "fs.cljc"))))))
  (testing "Use extract-fn with :name key"
    (let [td (fs/create-temp-dir)
          td-out (fs/path td "out")
          zip-file (fs/path td "foo.zip")]
      (fs/zip zip-file ["src" "README.md"])
      (fs/unzip zip-file td-out {:extract-fn #(str/ends-with? (:name %) ".cljc")})
      ;; only files that have names ending in .cljc should present
      (is (fs/exists? (fs/file td-out "src" "babashka" "fs.cljc")))
      (is (not (fs/exists? (fs/file td-out "README.md"))))
      ;; directories are not subject to extract-fn
      (is (fs/exists? (fs/file td-out "src" "babashka")))
      (is (fs/directory? (fs/file td-out "src" "babashka")))))
  (testing "Use extract-fn and :entry key"
    (let [td (fs/create-temp-dir)
          td-out1 (fs/path td "out1")
          td-out2 (fs/path td "out2")
          zip-file (fs/path td "foo.zip")
          times (atom #{})
          readme-time (atom nil)]
      (fs/zip zip-file ["LICENSE" "README.md"])
      ;; find out times for files by extracting to out1
      (fs/unzip zip-file td-out1
                {:extract-fn #(let [time (.getTime ^java.util.zip.ZipEntry (:entry %))]
                                (swap! times conj time)
                                (when (= "README.md" (:name %))
                                  (reset! readme-time time))
                                true)})
      ;; extract files to out2 that have the same time as README.md
      (fs/unzip zip-file td-out2
                {:extract-fn #(= (.getTime ^java.util.zip.ZipEntry (:entry %)) @readme-time)})
      ;; README.md should be extracted for sure
      (is (fs/exists? (fs/file td-out2 "README.md")))
      ;; LICENSE sometimes has the same time as README.md
      (let [fs-path (fs/file td-out2 "LICENSE")]
        (if (= 1 (count @times))
          (is (fs/exists? fs-path))
          (is (not (fs/exists? fs-path))))))))

(deftest gzip-gunzip-test
  (let [td (fs/create-temp-dir)
        td-out (fs/path td "out")
        gz-file (fs/path td-out "README.md.gz")]
    (is (= (str gz-file)
           (fs/gzip "README.md" {:dir td-out})))
    (is (fs/gunzip gz-file td-out))
    (is (fs/exists? (fs/path td-out "README.md")))
    (is (= (slurp "README.md") (slurp (fs/file td-out "README.md"))))
    (is (thrown? java.nio.file.FileAlreadyExistsException (fs/gunzip gz-file td-out)))
    (testing "no exception when replacing existing"
      (is (do (fs/gunzip gz-file td-out {:replace-existing true})
              true)))
    (testing "accepts out-file for specifying a custom gzip filename"
      (let [out-file "doc.md.gz"]
        (is (= (str (fs/path td-out out-file))
               (fs/gzip "README.md" {:dir      td-out
                                     :out-file out-file})))
        (is (fs/exists? (fs/path td-out out-file)))))))

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
      (is (not (fs/exists? (fs/path @capture-dir "my-dir")))
        (is (not (fs/exists? @capture-dir)))))))

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
  (testing "without nothing to expand"
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

(deftest uri->path-test
  (is (instance? java.nio.file.Path
                 (fs/path (.toURI (fs/file "."))))))

(deftest invalid-path
  (testing "illegal windows path"
    ;; a `:` outside of the drive letter is illegal but should not
    ;; throw.
    (is (false? (fs/exists? "c:/123:456")))))

(deftest write-bytes-test
  (let [f (-> (fs/path (fs/temp-dir) (str (gensym)))
              fs/delete-on-exit)]
    (fs/write-bytes f (.getBytes (String. "foo")))
    (is (= "foo" (String. (fs/read-all-bytes f))))
    ;; again, truncation behavior:
    (fs/write-bytes f (.getBytes (String. "foo")))
    (is (= "foo" (String. (fs/read-all-bytes f))))
    (fs/write-bytes f (.getBytes (String. "bar")) {:append true})
    (is (= "foobar" (String. (fs/read-all-bytes f))))))

(deftest write-lines-test
  (let [f (-> (fs/path (fs/temp-dir) (str (gensym)))
              fs/delete-on-exit)]
    (fs/write-lines f (repeat 3 "foo"))
    (is (= (repeat 3 "foo") (fs/read-all-lines f)))
    ;; again, truncation behavior:
    (fs/write-lines f (repeat 3 "foo"))
    (is (= (repeat 3 "foo") (fs/read-all-lines f)))
    (fs/write-lines f (repeat 3 "foo") {:append true})
    (is (= (repeat 6 "foo") (fs/read-all-lines f)))))

(deftest test-update-file
  (let [file (doto (fs/file (fs/temp-dir) (str (gensym)))
               (fs/delete-on-exit))]
    (testing "Throws if file doesn't exist"
      (is (thrown? FileNotFoundException (= "foooo" (fs/update-file file str "foooo")))))

    (spit file "foo")
    (is (= "foobar" (fs/update-file file #(str % "bar"))))
    (is (= "foobar" (slurp file)))
    (is (= "foobarbazbatcat" (fs/update-file file str "baz" "bat" "cat")))
    (is (= "foobarbazbatcat" (slurp file)))

    (let [new-val (fs/update-file file str (rand))]
      (is (= new-val (slurp file)))))

  (let [file (-> (fs/file (fs/temp-dir) (str (gensym)))
                 fs/delete-on-exit)]
    (spit file ", ")
    (is (= "foo, bar, baz" (fs/update-file file str/join ["foo" "bar" "baz"]))))

  (let [path (-> (fs/file (fs/temp-dir) (str (gensym)))
                 fs/delete-on-exit
                 fs/path)]
    (spit (fs/file path) "foo")
    (is (= "foobar" (fs/update-file path str "bar")))))

(deftest unixify-test
  (when windows?
    (is (str/includes? (fs/unixify (fs/absolutize "README.md")) "/"))
    (is (not (str/includes? (fs/unixify "README.md") "/")))
    (is (not (str/includes? (fs/unixify (fs/absolutize "README.md")) fs/file-separator)))))

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

(deftest file-owner-test
  (testing "works for files as well"
    (let [dir (doto (fs/create-temp-dir)
                fs/delete-on-exit)
          file-in-dir (fs/create-temp-file {:dir dir})]
      (is (= (str (fs/owner dir)) (str (fs/owner file-in-dir)))))))

(deftest issue-135-test
  (let [uri (java.net.URI/create (str "jar:file:" (-> (fs/cwd) fs/path .toUri .getPath) "/test-resources/bencode-1.1.0.jar"))
        fs (java.nio.file.FileSystems/newFileSystem uri ^java.util.Map (identity {}))
        path-in-zip (.getPath ^java.nio.file.FileSystem fs "/bencode" (into-array String []))
        zip-path (fs/path path-in-zip "core.clj")]
    (is zip-path)
    (is (= "/bencode/core.clj" (str zip-path)))))
