(ns babashka.fs-xplat-test
  "Cross-platform conformance suite. Runs on JVM, ClojureScript and squint.
  Tests here use the isolated temp-dir model (no cwd mutation) and the
  platform abstractions in babashka.fs-test-utils."
  (:require [babashka.fs :as fs]
            [babashka.fs-test-utils :include-macros true
             :refer [with-tmp string->bytes bytes->string]]
            [clojure.test :refer [deftest is testing]]))

(deftest create-delete-test
  (with-tmp [d]
    (let [f (fs/path d "f.txt")]
      (fs/create-file f)
      (is (fs/exists? f))
      (fs/delete f)
      (is (not (fs/exists? f))))))

(deftest write-read-test
  (with-tmp [d]
    (let [f (str (fs/path d "f.txt"))]
      (testing "write-bytes / read-all-bytes"
        (fs/write-bytes f (string->bytes "hello"))
        (is (= "hello" (bytes->string (fs/read-all-bytes f)))))
      (testing "write-lines / read-all-lines"
        (fs/write-lines f ["line1" "line2"])
        (is (= ["line1" "line2"] (fs/read-all-lines f))))
      (testing "append"
        (fs/write-lines f ["line3"] {:append true})
        (is (= ["line1" "line2" "line3"] (fs/read-all-lines f)))))))

(deftest copy-test
  (with-tmp [d]
    (let [src (str (fs/path d "src.txt"))
          dst (str (fs/path d "dst.txt"))]
      (fs/write-bytes src (string->bytes "data"))
      (fs/copy src dst)
      (is (= "data" (bytes->string (fs/read-all-bytes dst)))))))

(deftest move-test
  (with-tmp [d]
    (let [src (str (fs/path d "src.txt"))
          dst (str (fs/path d "dst.txt"))]
      (fs/write-bytes src (string->bytes "data"))
      (fs/move src dst)
      (is (not (fs/exists? src)))
      (is (= "data" (bytes->string (fs/read-all-bytes dst)))))))
