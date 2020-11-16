(ns babashka.nio-test
  (:require [babashka.nio :as nio]
            [clojure.set :as set]
            [clojure.test :refer [deftest is]]))

(deftest glob-test
  (is (= '("README.md") (map str
                             (nio/glob "." "README.md"))))
  (is (set/subset? #{"test/babashka/nio_test.clj" "src/babashka/nio.clj"}
                   (set (map str
                             (nio/glob "." "**/*.clj"))))))

