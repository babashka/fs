(ns babashka.fs-test
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing]])
  (:import [java.io FileNotFoundException]))

(def windows? (-> (System/getProperty "os.name")
                  (str/lower-case)
                  (str/starts-with? "windows")))

(defn normalize [p]
  (if windows?
    (str/replace p "\\" "/")
    (str p)))

(defn temp-dir []
  (-> (fs/create-temp-dir)
      (fs/delete-on-exit)))

(deftest walk-test
  (let [dir-counter (volatile! 0)
        file-counter (volatile! 0)]
    (fs/walk-file-tree "." {:post-visit-dir (fn [_ _] (vswap! dir-counter inc) :continue)
                            :visit-file (fn [_ _] (vswap! file-counter inc) :continue)
                            :max-depth 2})
    (is (pos? @dir-counter))
    (is (pos? @file-counter)))
  (testing "max-depth 0"
    (let [dir-counter (volatile! 0)
          file-counter (volatile! 0)]
      (fs/walk-file-tree "." {:post-visit-dir (fn [_ _] (vswap! dir-counter inc) :continue)
                              :visit-file (fn [_ _] (vswap! file-counter inc) :continue)
                              :max-depth 0})
      (is (zero? @dir-counter))
      (is (= 1 @file-counter))))
  (is (fs/walk-file-tree "." {:pre-visit-dir (fn [_ _] :terminate)}))
  (is (fs/walk-file-tree "." {:pre-visit-dir (fn [_ _] java.nio.file.FileVisitResult/TERMINATE)}))
  (is (thrown-with-msg?
       Exception #":continue, :skip-subtree, :skip-siblings, :terminate"
       (fs/walk-file-tree "." {:pre-visit-dir (fn [_ _])}))))

(deftest match-test
  (let [readme-match (fs/match "." "regex:README.md")]
    (is (= '("README.md") (map str readme-match)))
    (is (every? #(instance? java.nio.file.Path %) readme-match)))
  (is (set/subset? #{"project.clj"
                     "test/babashka/fs_test.clj"
                     "src/babashka/fs.cljc"}
                   (set (map normalize
                             (fs/match "." "regex:.*\\.cljc?" {:recursive true})))))
  (testing "match also matches directories and doesn't return the root directory"
    (is (set/subset? #{"test-resources/foo/1" "test-resources/foo/foo"}
                     (set (map normalize
                               (fs/match "test-resources/foo" "regex:.*" {:recursive true})))))
    (is (set/subset? #{"test-resources/foo/1" "test-resources/foo/foo"}
                     (set (map normalize
                               (fs/match "test-resources" "regex:foo.*" {:recursive true}))))))
  (when-not windows?
    (testing "symlink as root path"
      (let [tmp-dir1 (temp-dir)
            _ (spit (fs/file tmp-dir1 "dude.txt") "contents")
            tmp-dir2 (temp-dir)
            sym-link (fs/create-sym-link (fs/file tmp-dir2 "sym-link") tmp-dir1)
            target (fs/read-link sym-link)]
        (is (= (str target) (str tmp-dir1)))
        (is (empty? (fs/match sym-link "regex:.*")))
        (is (= 1 (count (fs/match sym-link "regex:.*" {:follow-links true}))))
        (is (= 1 (count (fs/match (fs/real-path sym-link) "regex:.*")))))))
  (testing "match with specific depth"
    (let [tmp-dir1 (temp-dir)
          nested-dir (fs/file tmp-dir1 "foo" "bar" "baz")
          _ (fs/create-dirs nested-dir)
          _ (spit (fs/file nested-dir "dude.txt") "contents")]
      (is (= 1 (count (if windows?
                        (fs/match tmp-dir1 "regex:foo\\\\bar\\\\baz\\\\.*" {:recursive true})
                        (fs/match tmp-dir1 "regex:foo/bar/baz/.*" {:recursive true}))))))))

(deftest glob-test
  (let [readme-match (fs/glob "." "README.md")]
    (is (= '("README.md") (map str readme-match)))
    (is (every? #(instance? java.nio.file.Path %) readme-match)))
  (is (set/subset? #{"project.clj"
                     "test/babashka/fs_test.clj"
                     "src/babashka/fs.cljc"}
                   (set (map normalize
                             (fs/glob "." "**.{clj,cljc}")))))
  (testing "glob also matches directories and doesn't return the root directory"
    (is (set/subset? #{"test-resources/foo/1" "test-resources/foo/foo"}
                     (set (map normalize
                               (fs/glob "test-resources/foo" "**")))))
    (is (set/subset? #{"test-resources/foo/1" "test-resources/foo/foo"}
                     (set (map normalize
                               (fs/glob "test-resources" "foo/**"))))))
  (when-not windows?
    (testing "symlink as root path"
      (let [tmp-dir1 (temp-dir)
            _ (spit (fs/file tmp-dir1 "dude.txt") "contents")
            tmp-dir2 (temp-dir)
            sym-link (fs/create-sym-link (fs/file tmp-dir2 "sym-link") tmp-dir1)]
        (is (empty? (fs/glob sym-link "**")))
        (is (= 1 (count (fs/glob sym-link "**" {:follow-links true}))))
        (is (= 1 (count (fs/glob (fs/real-path sym-link) "**"))))))
    (testing ":hidden option should be disabled by default"
      (is (empty? (map normalize (fs/glob "." "*git*"))))
      (testing "should be enabled (when not provided) when pattern starts with a dot"
        (is (= '(".gitignore") (map normalize (fs/glob "." ".gitig*")))))))
  (testing "glob with specific depth"
    (let [tmp-dir1 (temp-dir)
          nested-dir (fs/file tmp-dir1 "foo" "bar" "baz")
          _ (fs/create-dirs nested-dir)
          _ (spit (fs/file nested-dir "dude.txt") "contents")]
      (is (= 1 (count (if windows?
                        (fs/glob tmp-dir1 "foo\\\\bar\\\\baz\\\\*")
                        (fs/glob tmp-dir1 "foo/bar/baz/*")))))))
  (testing "windows globbing now can be similar to unix"
    (when windows?
      (let [tmp-dir1 (temp-dir)
            nested-dir (fs/file tmp-dir1 "foo" "bar" "baz")
            _ (fs/create-dirs nested-dir)
            _ (spit (fs/file nested-dir "dude.clj") "contents")
            _ (spit (fs/file nested-dir "dude2.clj") "contents")]
        (is (= 2 (count (fs/glob tmp-dir1 "foo/bar/baz/*.clj")))))))
  (testing "return also directories"
    (let [tmp-dir1 (temp-dir)
          nested-dir (fs/file tmp-dir1 "foo")
          _ (fs/create-dirs nested-dir)]
      (is (= 1 (count (map fs/directory? (fs/glob tmp-dir1 "*" {:max-depth 1}))))))))

(deftest create-dir-test
  (is (fs/create-dir (fs/path (temp-dir) "foo"))))

(deftest create-link-test
  (when-not windows?
    (let [tmp-dir (temp-dir)
          _       (spit (fs/file tmp-dir "dudette.txt") "some content")
          link    (fs/create-link (fs/file tmp-dir "hard-link.txt") (fs/file tmp-dir "dudette.txt"))]
      (is (.exists (fs/file link)))
      (is (= 2 (fs/get-attribute (fs/file link) "unix:nlink")))
      (is (.exists (fs/file tmp-dir "dudette.txt")))
      (is (fs/same-file? (fs/file tmp-dir "dudette.txt")
                         (fs/file tmp-dir "hard-link.txt")))
      (is (= (slurp (fs/file tmp-dir "hard-link.txt"))
             (slurp (fs/file tmp-dir "dudette.txt")))))))

(deftest regular-file?-test
  (let [tmp-dir  (temp-dir)
        tmp-file (fs/path tmp-dir "tmp.txt")]
    (spit (fs/file tmp-file) "")
    (is (not (fs/regular-file? tmp-dir)))
    (is (fs/regular-file? tmp-file))))

(deftest parent-test
  (let [tmp-dir (temp-dir)]
    (is (-> (fs/create-dir (fs/path tmp-dir "foo"))
            fs/parent
            (= tmp-dir)))))

(deftest file-name-test
  (let [tmp-dir (fs/path (temp-dir) "foo")]
    (fs/create-dir tmp-dir)
    (is (= "foo" (fs/file-name tmp-dir)))
    (is (= "foo" (fs/file-name (fs/file tmp-dir))))
    (is (= "foo" (fs/file-name (fs/path tmp-dir))))))

(deftest path-test
  (let [p (fs/path "foo" "bar" (io/file "baz"))]
    (is (instance? java.nio.file.Path p))
    (is (= "foo/bar/baz" (normalize p)))))

(deftest file-test
  (let [f (fs/file "foo" "bar" (fs/path "baz"))]
    (is (instance? java.io.File f))
    (is (= "foo/bar/baz" (normalize f)))))

(deftest copy-test
  (let [tmp-dir-1 (temp-dir)
        src-file (fs/create-file (fs/path tmp-dir-1 "tmp-file"))
        dest-dir (temp-dir)
        dest-file (fs/path dest-dir "tmp-file")]
    (fs/copy src-file dest-file)
    (is (fs/exists? dest-file))
    (fs/delete dest-file)
    (is (not (fs/exists? dest-file)))
    (testing "copying into dir"
      (fs/copy src-file dest-dir)
      (is (fs/exists? dest-file)))
    (testing "copying input-stream"
      (fs/delete dest-file)
      (fs/copy (io/input-stream (fs/file src-file)) dest-file)
      (is (fs/exists? dest-file))
      (is (= (slurp (fs/file src-file)) (slurp (fs/file dest-file)))))))

(deftest copy-tree-test
  (let [tmp-dir (temp-dir)]
    (fs/delete tmp-dir)
    (fs/copy-tree "." tmp-dir)
    (let [cur-dir-count (count (fs/glob "." "**" {:hidden true}))
          tmp-dir-count (count (fs/glob tmp-dir "**" {:hidden true}))]
      (is (pos? cur-dir-count))
      (is (= cur-dir-count tmp-dir-count)))))

(deftest copy-tree-on-file-test
  ;; cf. python3 -c 'import shutil; shutil.copytree("foo/bar1", "foo2")'
  (let [tmp-dir (temp-dir)]
    (is (thrown-with-msg? IllegalArgumentException #"Not a directory"
                          (fs/copy-tree (fs/file "test-resources" "foo" "1") (fs/file tmp-dir))))
    (spit (fs/file tmp-dir "1") "")
    (is (thrown-with-msg? IllegalArgumentException #"Not a directory"
                          (fs/copy-tree (fs/file "test-resources" "foo") (fs/file tmp-dir "1"))))
    (fs/delete-tree tmp-dir)))

(deftest copy-tree-create-nested-dest-test
  ;; https://github.com/babashka/fs/issues/42
  ;; foo2 doesn't exist
  (fs/with-temp-dir [tmp {}]
    (fs/copy-tree "test-resources/foo" (fs/file tmp "foo2" "foo"))
    (is (fs/exists? (fs/file tmp"foo2" "foo" "1"))
        "The nested destination directory is not created when it doesn't exist")))

(deftest copy-tree-nested-ro-dir-test
  (fs/with-temp-dir [tmp {}]
    ;; https://github.com/babashka/fs/issues/122
    (fs/create-dirs (fs/path tmp "src" "foo" "bar"))
    (.setReadOnly (fs/file tmp "src" "foo"))
    (fs/copy-tree (fs/path tmp "src") (fs/path tmp "dst"))
    (is (fs/exists? (fs/path tmp "dst" "foo" "bar")))
    (when (not windows?)
      ;; you can always write to directories on Windows, even if they are read-only
      ;; https://answers.microsoft.com/en-us/windows/forum/all/all-folders-are-now-read-only-windows-10/0ca1880f-e997-46af-bd85-042a53fc078e
      (is (not (fs/writable? (fs/path tmp "dst" "foo")))))))

(deftest components-test
  (let [paths (map normalize (fs/components (fs/path (temp-dir) "foo")))]
    (is (= "foo" (last paths)))
    (is (> (count paths) 1))))

(deftest list-dir-test
  (let [paths (map str (fs/list-dir (fs/real-path ".")))]
    (is (> (count paths) 1)))
  (let [paths (map str (fs/list-dir (fs/real-path ".") (fn accept [x] (fs/directory? x))))]
    (is (> (count paths) 1)))
  (let [paths (map str (fs/list-dir (fs/real-path ".") (fn accept [_] false)))]
    (is (zero? (count paths))))
  (let [paths (map str (fs/list-dir (fs/real-path ".") "*.clj"))]
    (is (pos? (count paths)))))

(deftest delete-permission-assumptions-test
  (let [tmp-dir (temp-dir)
        dir (fs/path tmp-dir "my-dir")
        file (fs/path tmp-dir "my-dir" "my-file")
        _ (fs/create-dir dir)]
    (when (not windows?)
      (testing "On Unix, read-only files can be deleted"
        (fs/create-file file)
        (fs/set-posix-file-permissions file "r--r--r--")
        (is (nil? (fs/delete file))))
      (testing "On Unix, files in a read-only directory cannot be deleted"
        (fs/create-file file)
        (fs/set-posix-file-permissions dir "r--------")
        (is (thrown? java.nio.file.AccessDeniedException (fs/delete file)))
        (fs/set-posix-file-permissions dir "--x------")
        (is (thrown? java.nio.file.AccessDeniedException (fs/delete file)))
        (fs/set-posix-file-permissions dir "r-x------")
        (is (thrown? java.nio.file.AccessDeniedException (fs/delete file)))
        (fs/set-posix-file-permissions dir "rwx------")
        (is (nil? (fs/delete file)))))
    (when windows?
      (testing "On Windows, .setWritable is idempotent"
        (fs/create-file file)
        (.setWritable (fs/file file) true)
        (.setWritable (fs/file file) true)
        (is (nil? (fs/delete file))))
      (testing "On Windows, read-only files can't be deleted"
        (fs/create-file file)
        (.setWritable (fs/file file) false)
        (is (thrown? Exception (fs/delete file)))
        (.setWritable (fs/file file) true)
        (is (nil? (fs/delete file))))
      (testing "On Windows, files in a read-only directory can be deleted"
        (fs/create-file file)
        (.setWritable (fs/file dir) false)
        (is (nil? (fs/delete file)))))))

(deftest delete-tree-test
  (let [tmp-dir1 (temp-dir)
        nested-dir (fs/file tmp-dir1 "foo" "bar" "baz")
        tmp-file (fs/file nested-dir "tmp-file")
        _ (fs/create-dirs nested-dir)]
    (is (fs/exists? nested-dir))
    (fs/create-file (fs/file nested-dir "tmp-file"))
    (is (fs/exists? tmp-file))
    (fs/delete-tree nested-dir)
    (is (not (fs/exists? nested-dir)))
    (testing "No exception when tree doesn't exist"
      (is (do (fs/delete-tree nested-dir)
              true))))
  (when-not windows?
    (testing "delete-tree does not follow symlinks"
      (let [tmp-dir1 (temp-dir)
            tmp-dir2 (temp-dir)
            tmp-file (fs/path tmp-dir2 "foo")
            _ (fs/create-file tmp-file)
            link-path (fs/path tmp-dir1 "link-to-tmp-dir2")
            link (fs/create-sym-link link-path tmp-dir2)]
        (is (fs/exists? tmp-file))
        (fs/delete-tree tmp-dir1)
        (is (not (fs/exists? link)))
        (is (fs/exists? tmp-file))
        (is (fs/exists? tmp-dir2))
        (is (not (fs/exists? tmp-dir1)))))

    (testing "delete-tree force deletes read-only directories and files"
      (let [tmp-dir (temp-dir)
            dir (fs/path tmp-dir "my-dir")
            file (fs/path tmp-dir "my-dir" "my-file")
            _ (fs/create-dir dir)
            _ (fs/create-file file)]
        (if windows?
          (do
            (.setWritable (fs/file file) false)
            (.setWritable (fs/file dir) false))
          (do
            (fs/set-posix-file-permissions file "r--r--r--")
            (fs/set-posix-file-permissions dir "r--r--r--")))
        (is (fs/exists? dir))
        (fs/delete-tree tmp-dir {:force true})
        (is (not (fs/exists? tmp-dir)))
        (is (not (fs/exists? dir)))))))

(deftest move-test
  (let [src-dir (fs/create-temp-dir)
        dest-dir (fs/create-temp-dir)
        f (fs/file src-dir "foo.txt")
        _ (spit f "foo")
        f2 (fs/file dest-dir "foo.txt")]
    (fs/move f f2)
    (is (not (fs/exists? f)))
    (is (fs/exists? f2))
    (is (= "foo" (str/trim (slurp f2))))
    (fs/delete f2)
    (is (not (fs/exists? f2)))
    (testing "moving into dir"
      (spit f "foo")
      (is (fs/exists? f))
      (fs/move f dest-dir)
      (is (not (fs/exists? f)))
      (is (fs/exists? f2))
      (is (= "foo" (str/trim (slurp f2)))))))

(deftest set-attribute-test
  (let [dir (fs/create-temp-dir)
        tmp-file (fs/create-file (fs/path dir "tmp-file"))]
    (is (= 100 (-> (fs/set-attribute tmp-file "basic:lastModifiedTime" (fs/millis->file-time 100))
                   (fs/read-attributes "*") :lastModifiedTime fs/file-time->millis)))))

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

(deftest normalize-test
  (is (not (str/includes? (fs/normalize (fs/absolutize ".")) "."))))

(deftest temp-dir-test
  (let [tmp-dir-in-temp-dir (fs/create-temp-dir {:path (fs/temp-dir)})]
    (is (fs/starts-with? tmp-dir-in-temp-dir (fs/temp-dir)))))

(deftest ends-with?-test
  (is (fs/ends-with? (fs/temp-dir) (last (fs/temp-dir)))))

(deftest posix-test
  (when-not windows?
    (is (str/includes? (-> (fs/posix-file-permissions ".")
                           (fs/posix->str))
                       "rwx"))
    (is (= (fs/posix-file-permissions ".")
           (-> (fs/posix-file-permissions ".")
               (fs/posix->str)
               (fs/str->posix))))
    (is (= "rwx------"
           (-> (fs/create-temp-dir {:posix-file-permissions "rwx------"})
               (fs/posix-file-permissions)
               (fs/posix->str))))))

(deftest delete-if-exists-test
  (let [tmp-file (fs/create-file (fs/path (temp-dir) "dude"))]
    (is (true? (fs/delete-if-exists tmp-file)))
    (is (false? (fs/delete-if-exists tmp-file)))))

(deftest size-test
  (is (pos? (fs/size (fs/temp-dir)))))

(deftest set-posix-test
  (when-not windows?
    (let [tmp-file (fs/create-file (fs/path (temp-dir) "foo"))]
      (is (fs/set-posix-file-permissions tmp-file
                                         "rwx------"))
      (is (= "rwx------"
             (fs/posix->str (fs/posix-file-permissions tmp-file)))))))

(deftest same-file?
  (fs/same-file? (fs/path ".") (fs/real-path ".")))

(deftest read-all-bytes-test
  (let [bs (fs/read-all-bytes "README.md")]
    (is (bytes? bs))
    (is (= (fs/size "README.md") (count bs)))))

(deftest read-all-lines-test
  (let [ls (fs/read-all-lines "README.md")]
    (is (= ls (line-seq (io/reader (fs/file "README.md"))))))
  (let [ls (fs/read-all-lines "test-resources/iso-8859.txt" {:charset "iso-8859-1"})]
    (is (= ls ["áéíóú" "España"]))))

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
  (is (= nil (fs/extension ".dotfile")))
  (is (= nil (fs/extension "bin/something"))))

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
  (let [td0 (fs/create-temp-dir)
        anchor (fs/file td0 "f0")
        _ (spit anchor "content")
        _ (Thread/sleep 10)
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
      (is (not (fs/directory? (fs/file td-out "babashka" "fs.cljc")))))))

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
        (is (not (fs/exists? @capture-dir)))))))

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
    (is (= (fs/exists? "c:/123:456") false))))

(deftest write-bytes-test
  (let [f (fs/path (fs/temp-dir) (str (gensym)))]
    (fs/write-bytes f (.getBytes (String. "foo")))
    (is (= "foo" (String. (fs/read-all-bytes f))))
    ;; again, truncation behavior:
    (fs/write-bytes f (.getBytes (String. "foo")))
    (is (= "foo" (String. (fs/read-all-bytes f))))
    (fs/write-bytes f (.getBytes (String. "bar")) {:append true})
    (is (= "foobar" (String. (fs/read-all-bytes f))))))

(deftest write-lines-test
  (let [f (fs/path (fs/temp-dir) (str (gensym)))]
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

  (let [file (fs/file (fs/temp-dir) (str (gensym)))]
    (spit file ", ")
    (is (= "foo, bar, baz" (fs/update-file file str/join ["foo" "bar" "baz"])))))

(deftest unixify-test
  (when windows?
    (is (str/includes? (fs/unixify (fs/normalize "README.md")) "/"))
    (is (not (str/includes? (fs/unixify (fs/normalize "README.md")) fs/file-separator)))))

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
  (let [uri (java.net.URI/create (str "jar:file:" (fs/unixify (fs/cwd)) "/test-resources/bencode-1.1.0.jar"))
        fs (java.nio.file.FileSystems/newFileSystem uri {})
        path-in-zip (.getPath ^java.nio.file.FileSystem fs "/bencode" (into-array String []))]
    (is (fs/path path-in-zip "core.clj"))))
