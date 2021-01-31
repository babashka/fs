(ns babashka.fs-test
  (:require [babashka.fs :as fs]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.test :refer [deftest is testing]]
            #_[me.raynes.fs :as rfs]))

(def cwd (fs/real-path "."))

(deftest glob-test
  (is (= '("README.md") (map str
                             (fs/glob "." "README.md"))))
  (is (set/subset? #{"project.clj"
                     "test/babashka/fs_test.clj"
                     "src/babashka/fs.clj"}
                   (set (map str
                             (fs/glob "." "**.clj")))))
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
          sym-link (fs/sym-link (fs/file tmp-dir2 "sym-link") tmp-dir1)]
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
