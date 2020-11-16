(ns babashka.fs-test
  (:require [babashka.fs :as fs]
            [clojure.set :as set]
            [clojure.test :refer [deftest is]]))

(deftest glob-test
  (is (= '("README.md") (map str
                             (fs/glob "." "README.md"))))
  (is (set/subset? #{"test/babashka/fs_test.clj" "src/babashka/fs.clj"}
                   (set (map str
                             (fs/glob "." "**/*.clj"))))))

