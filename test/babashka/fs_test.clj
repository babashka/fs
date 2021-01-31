(ns babashka.fs-test
  (:require #_[me.raynes.fs :as rfs]
            [babashka.fs :as fs]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]))

(def cwd (fs/real-path "."))

(deftest glob-test
  (is (= '("README.md") (map str
                             (fs/glob "." "README.md"))))
  (is (set/subset? #{"project.clj"
                     "test/babashka/fs_test.clj"
                     "src/babashka/fs.cljc"}
                   (set (map str
                             (fs/glob "." "**.{clj,cljc}")))))
  (testing "glob also matches directories and doesn't return the root directory"
    (is (= '("test-resources/foo/1" "test-resources/foo/foo")
           (map str
                (fs/glob "test-resources/foo" "**"))))
    (is (= '("test-resources/foo/1" "test-resources/foo/foo")
           (map str
                (fs/glob "test-resources" "foo/**")))))
  (testing "symlink as root path"
    (let [tmp-dir1 (fs/create-temp-dir)
          _ (spit (fs/file tmp-dir1 "dude.txt") "contents")
          tmp-dir2 (fs/create-temp-dir)
          sym-link (fs/create-sym-link (fs/file tmp-dir2 "sym-link") tmp-dir1)]
      (is (empty? (fs/glob sym-link "**")))
      (is (= 1 (count (fs/glob sym-link "**" {:follow-links true}))))
      (is (= 1 (count (fs/glob (fs/real-path sym-link) "**"))))))
  (testing "glob with specific depth"
    (let [tmp-dir1 (fs/create-temp-dir)
          nested-dir (fs/file tmp-dir1 "foo" "bar" "baz")
          _ (fs/create-dirs nested-dir)
          _ (spit (fs/file nested-dir "dude.txt") "contents")]
      (is (= 1 (count (fs/glob tmp-dir1 "foo/bar/baz/*")))))))

(deftest file-name-test
  (is (= "fs" (fs/file-name cwd)))
  (is (= "fs" (fs/file-name (fs/file cwd))))
  (is (= "fs" (fs/file-name (fs/path cwd)))))

(deftest path-test
  (let [p (fs/path "foo" "bar" (io/file "baz"))]
    (is (instance? java.nio.file.Path p))
    (is (= "foo/bar/baz" (str p)))))

(deftest file-test
  (let [f (fs/file "foo" "bar" (fs/path "baz"))]
    (is (instance? java.io.File f))
    (is (= "foo/bar/baz" (str f)))))

(deftest copy-test
  (let [tmp-dir (fs/create-temp-dir)]
    (fs/copy "." tmp-dir #{:recursive})
    (let [cur-dir-count (count (fs/glob "." "**" #{:hidden}))
          tmp-dir-count (count (fs/glob tmp-dir "**" #{:hidden}))]
      (is (pos? cur-dir-count))
      (is (= cur-dir-count tmp-dir-count)))))

(deftest elements-test
  (let [paths (map str (fs/elements (fs/real-path ".")))]
    (is (= "fs" (last paths)))
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
  (let [tmp-dir1 (fs/create-temp-dir)
        nested-dir (fs/file tmp-dir1 "foo" "bar" "baz")
        _ (fs/create-dirs nested-dir)]
    (is (fs/exists? nested-dir))
    (fs/delete-tree nested-dir)
    (is (not (fs/exists? nested-dir)))))

(deftest move-test
  (let [tmp-dir1 (fs/create-temp-dir)
        f (fs/file tmp-dir1 "foo.txt")
        _ (spit f "foo")
        f2 (fs/file tmp-dir1 "bar.txt")]
    (is (not (fs/exists? f)))
    (is (fs/exists? f2))
    (is (= "foo" (str/trim (slurp f2))))))
