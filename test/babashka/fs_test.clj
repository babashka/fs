(ns babashka.fs-test
  (:require [babashka.fs :as fs]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.test :refer [deftest is testing]]
            #_[me.raynes.fs :as rfs]))

(def cwd (fs/real-path "."))

(defn rel [other]
  (fs/relativize cwd other))

(deftest glob-test
  (is (= '("README.md") (map (comp str rel)
                             (fs/glob "." "README.md"))))
  (is (set/subset? #{"test/babashka/fs_test.clj" "src/babashka/fs.clj"}
                   (set (map (comp str rel)
                             (fs/glob "." "**/*.clj")))))
  (testing "glob also matches directories and doesn't return the root directory"
    (is (= '("test-resources/foo/1" "test-resources/foo/foo")
           (map (comp str rel)
                (fs/glob "test-resources/foo" "**"))))))

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
  (let [tmp-dir (fs/tmp-dir)]
    (fs/copy "." tmp-dir #{:recursive})
    (let [cur-dir-count (count (fs/glob "." "**" #{:hidden}))
          tmp-dir-count (count (fs/glob tmp-dir "**" #{:hidden}))]
      (is (pos? cur-dir-count))
      (is (= cur-dir-count tmp-dir-count)))))
