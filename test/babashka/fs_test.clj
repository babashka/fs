(ns babashka.fs-test
  (:require [babashka.fs :as fs]
            #_[clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.test :refer [deftest is testing]]
            #_[me.raynes.fs :as rfs]))

(deftest glob-test
  (is (= '("README.md") (map (comp str fs/relativize)
                             (fs/glob "." "README.md"))))
  (is (set/subset? #{"test/babashka/fs_test.clj" "src/babashka/fs.clj"}
                   (set (map (comp str fs/relativize)
                             (fs/glob "." "**/*.clj")))))
  (testing "glob also matches directories and doesn't return the root directory"
    (is (= '("test-resources/foo/1" "test-resources/foo/foo")
           (map (comp str fs/relativize)
                (fs/glob "test-resources/foo" "**")))))
  (testing "*cwd*"
    (is (= '("test-resources/foo/1" "test-resources/foo/foo")
           (map (comp str fs/relativize)
                (binding [fs/*cwd* "test-resources/foo"]
                  (fs/glob "**")))))))

