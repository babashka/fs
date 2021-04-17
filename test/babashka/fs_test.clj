(ns babashka.fs-test
  (:require #_[me.raynes.fs :as rfs]
            [babashka.fs :as fs]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]))


(def windows? (-> (System/getProperty "os.name")
                  (str/lower-case)
                  (str/includes? "win")))

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

(deftest glob-test
  (is (= '("README.md") (map str
                             (fs/glob "." "README.md"))))
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
        (is (= 1 (count (fs/glob (fs/real-path sym-link) "**")))))))
  (testing "glob with specific depth"
    (let [tmp-dir1 (temp-dir)
          nested-dir (fs/file tmp-dir1 "foo" "bar" "baz")
          _ (fs/create-dirs nested-dir)
          _ (spit (fs/file nested-dir "dude.txt") "contents")]
      (is (= 1 (count (if windows?
                        (fs/glob tmp-dir1 "foo\\\\bar\\\\baz\\\\*")
                        (fs/glob tmp-dir1 "foo/bar/baz/*"))))))))

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
  (let [tmp-dir (temp-dir)
        tmp-file (fs/create-file (fs/path tmp-dir "tmp-file"))
        dest-path (fs/path tmp-dir "tmp-file-dest")]
    (fs/copy tmp-file dest-path)
    (is (fs/exists? dest-path))))

(deftest copy-tree-test
  (let [tmp-dir (temp-dir)]
    (fs/delete tmp-dir)
    (fs/copy-tree "." tmp-dir)
    (let [cur-dir-count (count (fs/glob "." "**" #{:hidden}))
          tmp-dir-count (count (fs/glob tmp-dir "**" #{:hidden}))]
      (is (pos? cur-dir-count))
      (is (= cur-dir-count tmp-dir-count)))))

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
        (is (not (fs/exists? tmp-dir1)))))))

(deftest move-test
  (let [tmp-dir1 (fs/create-temp-dir)
        f (fs/file tmp-dir1 "foo.txt")
        _ (spit f "foo")
        f2 (fs/file tmp-dir1 "bar.txt")]
    (fs/move f f2)
    (is (not (fs/exists? f)))
    (is (fs/exists? f2))
    (is (= "foo" (str/trim (slurp f2))))))

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
    (is (= java (fs/which java-executable)))
    (is (contains? (set (fs/which java-executable {:all true})) java))))

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
    (is (= ls (line-seq (io/reader (fs/file "README.md")))))))

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
    (is (= ["file" "ext"] (fs/split-ext "/path/to/file.ext")))
    (is (= ["hi.tar" "gz"] (fs/split-ext "some/path/hi.tar.gz")))
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
