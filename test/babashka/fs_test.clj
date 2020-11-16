(ns babashka.fs-test
  (:require [babashka.fs :as fs]
            [clojure.set :as set]
            [clojure.test :refer [deftest is testing]]))

(defn relative [path]
  (fs/relativize (fs/real-path ".") path))

(deftest glob-test
  (is (= '("README.md") (map (comp str relative)
                             (fs/glob "." "README.md"))))
  (is (set/subset? #{"test/babashka/fs_test.clj" "src/babashka/fs.clj"}
                   (set (map (comp str relative)
                             (fs/glob "." "**/*.clj")))))
  (testing "glob also matches directories"
    (is (= '("test-resources/foo" "test-resources/foo/foo")
           (map (comp str relative)
                (fs/glob "test-resources/foo" "**"))))))

