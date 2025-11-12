(ns babashka.fs-linter-test
  (:require
   [babashka.test-report]
   [clj-kondo.core :as clj-kondo]
   [clj-kondo.hooks-api :as hooks-api]
   [clojure.string :as str]
   [clojure.test :refer [deftest is use-fixtures]]
   [matcher-combinators.test]))


(defn- lint [& lines]
  (let [code (->> (into ["(ns user (:require [babashka.fs :as fs]))"] lines)
                  (str/join "\n"))]
    (:findings (binding [hooks-api/*reload* true]
                 (with-in-str code (clj-kondo/run! {:config {:output {:format :edn}}
                                                    :lint ["-"]}))))))

(use-fixtures :once
  (fn init-cache [f]
    ;; this seems to init clj-kondo/.cache enough for our tests
    (clj-kondo/run! {:lint ["src"]})
    (f)))

(deftest with-temp-dir-no-args-found-by-kondo-test
  (is (match? [{:type :invalid-arity
                :message #"called with 0 args .*expects 1 or more"}]
              (lint "(fs/with-temp-dir)"))))

(deftest with-temp-dir-first-arg-not-vector-handled-by-hook-test
  (is (match? [{:type :babashka-fs/with-temp-dir-first-arg-not-vector
                :level :error
                :row 3 :col 3
                :end-row 3 :end-col 12
                :message "babashka.fs/with-temp-dir requires a vector for first arg"}]
              (lint "(fs/with-temp-dir"
                    ;123456789012
                    ;  ^^^^^^^^^
                    "  (+ 1 2 3))"))))

(deftest with-temp-dir-vector-arg-empty-handled-by-hook-test
  (is (match? [{:type :babashka-fs/with-temp-dir-vector-arg-needs-binding-symbol
                :level :error
                :row 2 :col 19
                :end-row 2 :end-col 21
                :message "babashka.fs/with-temp-dir vector arg requires binding-name symbol as first value"}]
              (lint
               ;123456789012345678901
               ;                  ^^
               "(fs/with-temp-dir [])"))))

(deftest with-temp-dir-vector-arg-binding-name-not-symbol-handled-by-hook-test
  (is (match? [{:type :babashka-fs/with-temp-dir-vector-arg-needs-binding-symbol
                :level :error
                :row 2 :col 21
                :end-row 2 :end-col 26
                :message "babashka.fs/with-temp-dir vector arg requires binding-name symbol as first value"}]
              (lint
               ;12345678901234567890123456
               ;                    ^^^^^
               "(fs/with-temp-dir [ :oops ])"))))

(deftest with-temp-dir-extra-value-in-vector-handled-by-hook-test
  (is (match? [{:type :babashka-fs/with-temp-dir-vector-arg-extra-value
                :level :error
                :row 2 :col 29
                :end-row 2 :end-col 35
                :message "babashka.fs/with-temp-dir vector arg accepts at most 2 values"}]
              (lint
               ;12345678901234567890123456789012345
               ;                            ^^^^^^
               "(fs/with-temp-dir [bname {} 'extra]"
               "  (spit bname \"foo\"))"))))

(deftest with-temp-dir-extra-values-in-vector-handled-by-hook-test
  (is (match? [{:type :babashka-fs/with-temp-dir-vector-arg-extra-value
                :level :error
                :row 2 :col 29
                :end-row 2 :end-col 36
                :message "babashka.fs/with-temp-dir vector arg accepts at most 2 values"}
               {:type :babashka-fs/with-temp-dir-vector-arg-extra-value
                :level :error
                :row 2 :col 37
                :end-row 2 :end-col 44
                :message "babashka.fs/with-temp-dir vector arg accepts at most 2 values"}]
              (lint
               ;12345678901234567890123456789012345678901234
               ;                            ^^^^^^^ ^^^^^^^
               "(fs/with-temp-dir [bname {} 'extra1 'extra2]"
               "  (spit bname \"foo\"))"))))

(deftest with-temp-dir-unused-binding-found-by-kondo-test
  (is (match? [{:type :unused-binding}]
              (lint "(fs/with-temp-dir [unused-binding])"))))

(deftest with-temp-dir-binding-name-with-no-options-is-fine-test
  (is (match? []
              (lint "(fs/with-temp-dir [bname]"
                    "  (spit bname \"foo\"))"))))

(deftest with-temp-dir-binding-name-with-options-is-fine-test
  (is (match? []
              (lint "(fs/with-temp-dir [bname {}]"
                    "  (spit bname \"foo\"))"))))

(deftest with-temp-dir-options-from-binding-is-fine-test
  (is (match? []
              (lint "(let [some-opts {}]"
                    "  (fs/with-temp-dir [bname some-opts]"
                    "    (spit bname \"foo\")))"))))

(deftest with-temp-dir-options-from-fn-call-is-fine-test
  (is (match? []
              (lint "(defn make-some-opts [] {})"
                    "(fs/with-temp-dir [bname (make-some-opts)]"
                    "  (spit bname \"foo\"))"))))

(deftest with-temp-dir-options-type-is-not-validated-test
  ;; our linter does not catch clearly invalid option types, for example:
  (is (match? []
              (lint "(fs/with-temp-dir [bname :bad-options]"
                    "  (spit bname \"foo\"))"))))
