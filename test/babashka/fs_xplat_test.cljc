(ns babashka.fs-xplat-test
  "Cross-platform conformance suite. Runs on JVM, ClojureScript and squint.
  Tests use the isolated temp-dir model (no cwd mutation) and the platform
  abstractions in babashka.fs-test-utils. Path comparisons go through
  `fs/unixify` so they hold on Windows too."
  (:require [babashka.fs :as fs]
            [babashka.fs-test-utils :include-macros true
             :refer [with-tmp string->bytes bytes->string file-time?
                     files list-tree slurp-str]]
            [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]))

;;;; Path manipulation

(deftest path-test
  (testing "single arg"
    (is (= "foo" (fs/unixify (fs/path "foo"))))
    (is (= "/foo" (fs/unixify (fs/path "/foo")))))
  (testing "two args"
    (is (= "foo/bar" (fs/unixify (fs/path "foo" "bar"))))
    (is (= "/bar" (fs/unixify (fs/path "foo" "/bar")))))
  (testing "three args"
    (is (= "a/b/c" (fs/unixify (fs/path "a" "b" "c"))))))

(deftest file-name-test
  (is (= "baz" (fs/file-name "foo/bar/baz")))
  (is (= "baz" (fs/file-name "baz"))))

(deftest parent-test
  (is (= "foo/bar" (fs/unixify (fs/parent "foo/bar/baz"))))
  (is (nil? (fs/parent "foo"))))

(deftest absolutize-test
  (is (fs/absolute? (fs/absolutize "foo")))
  (is (fs/absolute? (fs/absolutize "."))))

(deftest normalize-test
  (is (= "a/b" (fs/unixify (fs/normalize "a/b/c/.."))))
  (is (= "a/b/c" (fs/unixify (fs/normalize "a//b//c")))))

(deftest relativize-test
  (is (= "c/d" (fs/unixify (fs/relativize "a/b" "a/b/c/d"))))
  (is (= "../.." (fs/unixify (fs/relativize "a/b/c/d" "a/b")))))

(deftest split-ext-test
  (is (= ["foo.bar" "baz"] (fs/split-ext "foo.bar.baz")))
  (is (= ["foo" "bar.baz"] (fs/split-ext "foo.bar.baz" {:ext "bar.baz"})))
  (is (= ["foo.bar.baz" nil] (fs/split-ext "foo.bar.baz" {:ext "png"})))
  (is (= ["foo" nil] (fs/split-ext "foo"))))

(deftest extension-test
  (is (= "clj" (fs/extension "foo.clj")))
  (is (nil? (fs/extension "foo"))))

(deftest strip-ext-test
  (is (= "foo" (fs/strip-ext "foo.clj")))
  (is (= "foo.bar" (fs/strip-ext "foo.bar.baz"))))

(deftest starts-with-test
  (is (fs/starts-with? "foo/bar/baz" "foo/bar"))
  (is (fs/starts-with? "foo/bar" "foo/bar"))
  (is (not (fs/starts-with? "foo/bar" "foo/b"))))

(deftest ends-with-test
  (is (fs/ends-with? "foo/bar/baz" "bar/baz"))
  (is (fs/ends-with? "foo/bar" "foo/bar"))
  (is (not (fs/ends-with? "foo/bar" "oo/bar"))))

(deftest components-test
  (is (= ["foo" "bar" "baz"] (map str (fs/components "foo/bar/baz"))))
  (is (= ["foo"] (map str (fs/components "foo")))))

(deftest unixify-test
  (is (= "foo/bar" (fs/unixify "foo/bar"))))

;;;; Predicates

(deftest predicates-test
  (with-tmp [d]
    (let [f (str (fs/path d "file.txt"))]
      (fs/write-bytes f (string->bytes "hello"))
      (testing "exists?"
        (is (fs/exists? d))
        (is (fs/exists? f))
        (is (not (fs/exists? (str d "/nope")))))
      (testing "directory?"
        (is (fs/directory? d))
        (is (not (fs/directory? f))))
      (testing "regular-file?"
        (is (fs/regular-file? f))
        (is (not (fs/regular-file? d))))
      (testing "absolute?"
        (is (fs/absolute? d))
        (is (not (fs/absolute? "relative"))))
      (testing "relative?"
        (is (fs/relative? "relative"))
        (is (not (fs/relative? d))))
      (testing "readable?"
        (is (fs/readable? f)))
      (testing "writable?"
        (is (fs/writable? f)))
      (testing "hidden?"
        (is (not (fs/hidden? f)))
        ;; dotfiles are not hidden on Windows
        (when-not (fs/windows?)
          (is (fs/hidden? (fs/path d ".hidden"))))))))

;;;; File operations

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

(deftest copy-to-file-test
  (with-tmp [d]
    (files d "file" "dest-dir/")
    (is (= "dest-dir/file" (fs/unixify (fs/relativize d (fs/copy (fs/path d "file") (fs/path d "dest-dir/file"))))))
    (is (= ["dest-dir/file" "file"] (list-tree d)))))

(deftest copy-into-dir-test
  (with-tmp [d]
    (files d "file" "dest-dir/")
    (is (= "dest-dir/file" (fs/unixify (fs/relativize d (fs/copy (fs/path d "file") (fs/path d "dest-dir"))))))
    (is (= ["dest-dir/file" "file"] (list-tree d)))))

(deftest move-to-file-test
  (with-tmp [d]
    (files d "src-dir/foo.txt" "dest-dir/")
    (let [content (slurp-str (fs/path d "src-dir/foo.txt"))]
      (fs/move (fs/path d "src-dir/foo.txt") (fs/path d "dest-dir/foo.txt"))
      (is (= ["dest-dir/foo.txt" "src-dir/"] (list-tree d)))
      (is (= content (slurp-str (fs/path d "dest-dir/foo.txt")))))))

(deftest move-to-dir-test
  (with-tmp [d]
    (files d "src-dir/foo.txt" "dest-dir/")
    (let [content (slurp-str (fs/path d "src-dir/foo.txt"))]
      (fs/move (fs/path d "src-dir/foo.txt") (fs/path d "dest-dir"))
      (is (= ["dest-dir/foo.txt" "src-dir/"] (list-tree d)))
      (is (= content (slurp-str (fs/path d "dest-dir/foo.txt")))))))

(deftest size-test
  (with-tmp [d]
    (let [f (str (fs/path d "f.txt"))]
      (fs/write-bytes f (string->bytes "hello"))
      (is (= 5 (fs/size f))))))

(deftest create-dir-test
  (with-tmp [d]
    (is (fs/create-dir (fs/path d "foo")))
    (is (= ["foo/"] (list-tree d)))
    (is (fs/directory? (fs/path d "foo")))))

(deftest create-dirs-test
  (with-tmp [d]
    (let [nested (fs/path d "a" "b" "c")]
      (fs/create-dirs nested)
      (is (fs/directory? nested)))))

(deftest delete-tree-test
  (with-tmp [d]
    (files d "foo/bar/baz/file.txt")
    (is (= "foo" (fs/unixify (fs/relativize d (fs/delete-tree (fs/path d "foo"))))))
    (is (= [] (list-tree d)))))

(deftest delete-tree-nested-test
  (with-tmp [d]
    (files d "foo/bar/baz/file.txt")
    (is (= "foo/bar/baz" (fs/unixify (fs/relativize d (fs/delete-tree (fs/path d "foo/bar/baz"))))))
    (is (= ["foo/bar/"] (list-tree d)))))

(deftest delete-tree-ok-if-dir-missing-test
  (with-tmp [d]
    (is (nil? (fs/delete-tree (fs/path d "foo"))))
    (is (nil? (fs/delete-tree (fs/path d "foo/bar/baz"))))))

(deftest sym-link-test
  ;; symlink creation needs privileges on Windows
  (when-not (fs/windows?)
    (with-tmp [d]
      (let [target (str (fs/path d "target.txt"))
            link   (str (fs/path d "link.txt"))]
        (fs/write-bytes target (string->bytes "hi"))
        (fs/create-sym-link link target)
        (is (fs/sym-link? link))
        (is (fs/exists? link))
        (is (= target (str (fs/read-link link))))))))

(deftest touch-test
  (with-tmp [d]
    (let [f (str (fs/path d "f.txt"))]
      (is (not (fs/exists? f)))
      (fs/touch f)
      (is (fs/exists? f)))))

(deftest update-file-test
  (with-tmp [d]
    (let [f (str (fs/path d "f.txt"))]
      (fs/write-lines f ["hello"])
      (fs/update-file f str/upper-case)
      (is (str/includes? (first (fs/read-all-lines f)) "HELLO")))))

;;;; Glob / walk

(deftest glob-test
  (with-tmp [d]
    (fs/write-bytes (str (fs/path d "a.clj")) (string->bytes ""))
    (fs/write-bytes (str (fs/path d "b.clj")) (string->bytes ""))
    (fs/write-bytes (str (fs/path d "c.txt")) (string->bytes ""))
    (fs/create-dir (fs/path d "sub"))
    (fs/write-bytes (str (fs/path d "sub" "d.clj")) (string->bytes ""))
    (let [results (set (map fs/file-name (fs/glob d "*.clj")))]
      (is (= #{"a.clj" "b.clj"} results)))
    (let [results (set (map fs/file-name (fs/glob d "**.clj")))]
      (is (= #{"a.clj" "b.clj" "d.clj"} results)))))

(deftest walk-file-tree-test
  (with-tmp [d]
    (fs/write-bytes (str (fs/path d "f1.txt")) (string->bytes ""))
    (fs/create-dir (fs/path d "sub"))
    (fs/write-bytes (str (fs/path d "sub" "f2.txt")) (string->bytes ""))
    (let [visited (atom [])]
      (fs/walk-file-tree d {:visit-file (fn [p _] (swap! visited conj (fs/file-name p)) :continue)})
      (is (= #{"f1.txt" "f2.txt"} (set @visited))))))

;;;; System

(deftest cwd-test
  (is (fs/absolute? (fs/cwd))))

(deftest home-test
  (is (fs/absolute? (str (fs/home)))))

(deftest windows-test
  (is (boolean? (fs/windows?))))

(deftest which-test
  (is (some? (fs/which "node"))))

;;;; Temp

(deftest temp-dir-test
  (is (fs/directory? (fs/temp-dir))))

(deftest create-temp-dir-test
  (let [d (fs/create-temp-dir {:prefix "mytest-"})]
    (try
      (is (fs/directory? d))
      (is (str/includes? (fs/file-name d) "mytest-"))
      (finally (fs/delete-tree d)))))

;;;; Times

(deftest last-modified-time-test
  (with-tmp [d]
    (let [f (str (fs/path d "f.txt"))]
      (fs/write-bytes f (string->bytes "x"))
      (let [t (fs/last-modified-time f)]
        (is (file-time? t))
        (is (pos? (fs/file-time->millis t)))))))

;;;; Gzip

(deftest gzip-gunzip-test
  (with-tmp [d]
    (let [src (str (fs/path d "data.txt"))]
      (fs/write-bytes src (string->bytes "hello world"))
      (let [out (fs/gzip src {:dir d})]
        (is (fs/exists? out))
        (let [extracted (fs/gunzip out d {:replace-existing true})]
          (is (= "hello world" (bytes->string (fs/read-all-bytes extracted)))))))))

;;;; Posix

(deftest posix-round-trip-test
  (is (= "rwxr-xr-x" (fs/posix->str (fs/str->posix "rwxr-xr-x"))))
  (is (= "rw-r--r--" (fs/posix->str (fs/str->posix "rw-r--r--")))))

;;;; XDG

(deftest xdg-test
  (is (string? (str (fs/xdg-config-home))))
  (is (string? (str (fs/xdg-cache-home))))
  (is (string? (str (fs/xdg-data-home))))
  (is (string? (str (fs/xdg-state-home)))))
