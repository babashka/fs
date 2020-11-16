(ns babashka.io-test
  (:require [babashka.io :as bio]
            [clojure.set :as set]
            [clojure.test :refer [deftest is]]))

(deftest glob-test
  (is (= '("README.md") (map str
                             (bio/glob "." "README.md"))))
  (is (set/subset? #{"test/babashka/io_test.clj" "src/babashka/io.clj"}
                   (set (map str
                             (bio/glob "." "**/*.clj"))))))

