(ns babashka.fs-test
  "Cross-platform conformance suite. Runs on JVM, ClojureScript and squint.
  Tests use the isolated temp-dir model (no cwd mutation) and the platform
  abstractions in babashka.fs-test-utils. Path comparisons go through
  `fs/unixify` so they hold on Windows too. JVM-only tests live in
  babashka.fs-jvm-test."
  (:require [babashka.fs :as fs :include-macros true]
            [babashka.fs-test-utils :include-macros true
             :refer [string->bytes bytes->string file-time?
                     files list-tree rel-entries slurp-str caught ex-msg
                     fsnapshot set-sym-link-mtime!]]
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
  (let [f "some-dir/foo.ext"]
    (is (= "foo.ext" (fs/file-name f)))
    (is (= "foo.ext" (fs/file-name (fs/file f))))
    (is (= "foo.ext" (fs/file-name (fs/path f))))))

(deftest parent-test
  (is (= "dir" (fs/unixify (fs/parent "dir/foo"))))
  (is (nil? (fs/parent "foo"))))

(deftest root-test
  (is (some? (fs/root (fs/absolutize "foo"))))
  (is (nil? (fs/root "relative/path"))))

(deftest absolutize-test
  (is (fs/absolute? (fs/absolutize "foo")))
  (is (fs/absolute? (fs/absolutize "."))))

(deftest normalize-test
  (is (= "foo/bar/baz" (fs/unixify (fs/normalize "foo/bar/baz"))))
  (is (= "foo/bar/baz" (fs/unixify (fs/normalize "./foo/./bing/./boop/.././../bar/./baz/.")))))

(deftest relativize-test
  (is (= "c/d" (fs/unixify (fs/relativize "a/b" "a/b/c/d"))))
  (is (= "../.." (fs/unixify (fs/relativize "a/b/c/d" "a/b")))))

(deftest split-ext-test
  (testing "strings"
    (is (= ["name" "clj"] (fs/split-ext "name.clj")))
    (is (= ["/path/to/file" "ext"] (fs/split-ext "/path/to/file.ext")))
    (is (= ["some/path/hi.tar" "gz"] (fs/split-ext "some/path/hi.tar.gz")))
    (is (= [".dotfile" nil] (fs/split-ext ".dotfile")))
    (is (= ["foo/.dotfile" nil] (fs/split-ext "foo/.dotfile")))
    (is (= ["/home/.zshrc" nil] (fs/split-ext "/home/.zshrc")))
    (is (= ["name" nil] (fs/split-ext "name"))))
  (testing ":ext option"
    (is (= ["foo" "bar.baz"] (fs/split-ext "foo.bar.baz" {:ext "bar.baz"})))
    (is (= ["foo.bar.baz" nil] (fs/split-ext "foo.bar.baz" {:ext "png"}))))
  (testing "coerces paths and files"
    (is (= ["name" "clj"] (fs/split-ext (fs/file "name.clj"))))
    (is (= ["name" "clj"] (fs/split-ext (fs/path "name.clj"))))))

(deftest extension-test
  (is (= "clj" (fs/extension "file-name.clj")))
  (is (= "template" (fs/extension "file-name.html.template")))
  (is (nil? (fs/extension ".dotfile")))
  (is (nil? (fs/extension "foo/.dotfile")))
  (is (nil? (fs/extension "/home/.zshrc")))
  (is (nil? (fs/extension "bin/something"))))

(deftest strip-ext-test
  (is (= "file-name" (fs/strip-ext "file-name.clj")))
  (is (= "file-name.html" (fs/strip-ext "file-name.html.template")))
  (is (= "file-name" (fs/strip-ext "file-name.html.template" {:ext "html.template"})))
  (is (= "file-name.html.template" (fs/strip-ext "file-name.html.template" {:ext "html"})))
  (is (= "/path/to/file-name.html" (fs/strip-ext "/path/to/file-name.html.template")))
  (is (= "path/to/file-name" (fs/strip-ext "path/to/file-name.html.template" {:ext "html.template"})))
  (is (= "/path/to/file-name.html.template" (fs/strip-ext "/path/to/file-name.html.template" {:ext "html"})))
  (is (= ".dotfile" (fs/strip-ext ".dotfile")))
  (is (= ".dotfile" (fs/strip-ext ".dotfile" {:ext "dotfile"})))
  (is (= "bin/something" (fs/strip-ext "bin/something")))
  (is (= "test-resources/dir.dot/no-ext" (fs/strip-ext "test-resources/dir.dot/no-ext"))))

(deftest starts-with-test
  (is (fs/starts-with? "foo/bar/baz" "foo/bar"))
  (is (fs/starts-with? "foo/bar" "foo/bar"))
  (is (not (fs/starts-with? "foo/bar" "foo/b"))))

(deftest ends-with-test
  (is (= true (fs/ends-with? "one/two/three" "three")))
  (is (= true (fs/ends-with? "one/two/three" "two/three")))
  (is (= true (fs/ends-with? "one/two/three" "one/two/three")))
  (is (= false (fs/ends-with? "one/two/three" "one/three"))))

(deftest components-test
  (is (= ["foo" "bar" "baz" "bop.txt"] (map str (fs/components "foo/bar/baz/bop.txt")))))

(deftest unixify-test
  (is (= "README.md" (fs/unixify "README.md")))
  (let [file "C:\\Users\\Billy\\proj\\foobar\\README.md"]
    (if (fs/windows?)
      (is (= "C:/Users/Billy/proj/foobar/README.md" (fs/unixify file)))
      (is (= file (fs/unixify file))))))

;;;; Predicates

(deftest predicates-test
  (fs/with-temp-dir [d]
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

(deftest create-file-test
  (fs/with-temp-dir [d]
    (let [f (fs/path d "new-file")]
      (is (= (fs/unixify f) (fs/unixify (fs/create-file f))))
      (is (fs/regular-file? f)))))

(deftest create-temp-file-test
  (fs/with-temp-dir [d]
    (let [f (fs/create-temp-file {:dir d})]
      (is (fs/regular-file? f))
      (is (fs/starts-with? f d)))))

(deftest create-delete-test
  (fs/with-temp-dir [d]
    (let [f (fs/path d "f.txt")]
      (fs/create-file f)
      (is (fs/exists? f))
      (fs/delete f)
      (is (not (fs/exists? f))))))

(deftest write-bytes-test
  (fs/with-temp-dir [d]
    (let [f (fs/path d "file.bin")]
      (is (= (fs/unixify f) (fs/unixify (fs/write-bytes f (string->bytes "foo")))))
      (is (= "foo" (slurp-str f)))
      (fs/write-bytes f (string->bytes "bar"))
      (is (= "bar" (slurp-str f)) "existing file overwritten")
      (fs/write-bytes f (string->bytes "baz") {:append true})
      (is (= "barbaz" (slurp-str f)) "existing file appended to"))))

(deftest write-lines-test
  (fs/with-temp-dir [d]
    (let [f (fs/path d "file.txt")]
      (fs/write-lines f (repeat 3 "foo"))
      (is (= (vec (repeat 3 "foo")) (fs/read-all-lines f)))
      (fs/write-lines f (repeat 3 "bar"))
      (is (= (vec (repeat 3 "bar")) (fs/read-all-lines f)) "existing file overwritten")
      (fs/write-lines f (repeat 3 "baz") {:append true})
      (is (= (into (vec (repeat 3 "bar")) (repeat 3 "baz")) (fs/read-all-lines f))
          "existing file appended to"))))

(deftest read-all-bytes-test
  (fs/with-temp-dir [d]
    (let [f (fs/path d "f.txt")]
      (fs/write-bytes f (string->bytes "some\ncontent\nhere"))
      (is (= 17 (fs/size f)))
      (is (= "some\ncontent\nhere" (slurp-str f))))))

(deftest read-all-lines-test
  (fs/with-temp-dir [d]
    (let [f (fs/path d "f.txt")]
      (fs/write-bytes f (string->bytes "some\ncontent\nhere"))
      (is (= ["some" "content" "here"] (fs/read-all-lines f))))))

(deftest copy-to-file-test
  (fs/with-temp-dir [d]
    (files d "file" "dest-dir/")
    (is (= "dest-dir/file" (fs/unixify (fs/relativize d (fs/copy (fs/path d "file") (fs/path d "dest-dir/file"))))))
    (is (= ["dest-dir/file" "file"] (list-tree d)))))

(deftest copy-into-dir-test
  (fs/with-temp-dir [d]
    (files d "file" "dest-dir/")
    (is (= "dest-dir/file" (fs/unixify (fs/relativize d (fs/copy (fs/path d "file") (fs/path d "dest-dir"))))))
    (is (= ["dest-dir/file" "file"] (list-tree d)))))

(deftest copy-nofollow-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (let [real (fs/path d "real.txt")
            lnk (fs/path d "lnk.txt")]
        (fs/write-bytes real (string->bytes "hi"))
        (fs/create-sym-link lnk real)
        (testing ":nofollow-links copies the link itself"
          (is (fs/sym-link? (fs/copy lnk (fs/path d "c1") {:nofollow-links true}))))
        (testing "default follows the link"
          (is (not (fs/sym-link? (fs/copy lnk (fs/path d "c2"))))))))))

(deftest copy-attributes-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (let [src (fs/path d "src.txt")]
        (fs/write-bytes src (string->bytes "hi"))
        (fs/set-posix-file-permissions src "rwxr-xr-x")
        (is (= "rwxr-xr-x"
               (fs/posix->str (fs/posix-file-permissions
                               (fs/copy src (fs/path d "dst.txt") {:copy-attributes true})))))))))

(deftest copy-tree-test
  (fs/with-temp-dir [d]
    (files d "src-dir/.foo" "src-dir/a/a.txt" "src-dir/a/b/b.txt"
           "src-dir/a/b/c" "src-dir/foo.txt")
    (fs/copy-tree (fs/path d "src-dir") (fs/path d "dest-dir"))
    (is (= [".foo" "a/a.txt" "a/b/b.txt" "a/b/c" "foo.txt"]
           (list-tree (fs/path d "dest-dir"))))))

(deftest copy-tree-from-file-throws-test
  (fs/with-temp-dir [d]
    (files d "src-dir/dir/file.txt" "dest-dir/")
    (let [before (fsnapshot d)
          e (caught #(fs/copy-tree (fs/path d "src-dir/dir/file.txt") (fs/path d "dest-dir")))]
      (is (some? e))
      (is (re-find #"Not a directory" (ex-msg e)))
      (is (= before (fsnapshot d)) "filesystem unchanged"))))

(deftest copy-tree-to-file-throws-test
  (fs/with-temp-dir [d]
    (files d "src-dir/dir/file.txt" "dest-dir/file.txt")
    (let [before (fsnapshot d)
          e (caught #(fs/copy-tree (fs/path d "src-dir/dir") (fs/path d "dest-dir/file.txt")))]
      (is (some? e))
      (is (re-find #"Not a directory" (ex-msg e)))
      (is (= before (fsnapshot d)) "filesystem unchanged"))))

(deftest copy-tree-creates-missing-dest-dirs-test
  (fs/with-temp-dir [d]
    (files d "src-dir/foo/file.txt" "dest-dir/")
    (fs/copy-tree (fs/path d "src-dir/foo") (fs/path d "dest-dir/foo2/foo"))
    (is (= ["dest-dir/foo2/foo/file.txt" "src-dir/foo/file.txt"] (list-tree d)))))

(deftest copy-tree-fails-on-parent-to-child-test
  (fs/with-temp-dir [d]
    (files d "foo/bar/baz/somefile.txt")
    (is (= "foo" (fs/unixify (fs/relativize d (fs/copy-tree (fs/path d "foo") (fs/path d "foo")))))
        "copy to self is allowed and a no-op")
    (let [e (caught #(fs/copy-tree (fs/path d "foo") (fs/path d "foo/new-dir")))]
      (is (re-find #"under itself" (ex-msg e)))
      "copy to new dir under self throws")
    (let [e (caught #(fs/copy-tree (fs/path d "foo") (fs/path d "foo/bar")))]
      (is (re-find #"under itself" (ex-msg e)))
      "copy to existing dir under self throws")
    (is (= ["foo/bar/baz/somefile.txt"] (list-tree d)) "files/dirs are unchanged")
    (fs/copy-tree (fs/path d "foo") (fs/path d "foobar"))
    (is (fs/exists? (fs/path d "foobar/bar/baz/somefile.txt")))))

(deftest copy-tree-nofollow-src-link-throws-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (files d "src-dir/bar/baz/somefile.txt")
      (fs/create-sym-link (fs/path d "link-src-dir") "src-dir")
      (let [e (caught #(fs/copy-tree (fs/path d "link-src-dir") (fs/path d "dest-dir") {:nofollow-links true}))]
        (is (some? e))
        (is (re-find #"Not a directory" (ex-msg e)))))))

(deftest copy-tree-nofollow-inner-link-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (let [src (fs/path d "src")]
        (fs/create-dirs src)
        (fs/write-bytes (fs/path src "target.txt") (string->bytes "x"))
        (fs/create-sym-link (fs/path src "innerlink") "target.txt")
        (fs/copy-tree src (fs/path d "dst") {:nofollow-links true})
        (is (fs/sym-link? (fs/path d "dst" "innerlink")))))))

(deftest copy-tree-nofollow-dest-link-throws-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (files d "src-dir/bar/baz/somefile.txt")
      (fs/create-sym-link (fs/path d "link-dest-dir") "dest-dir")
      (let [e (caught #(fs/copy-tree (fs/path d "src-dir") (fs/path d "link-dest-dir") {:nofollow-links true}))]
        (is (some? e))
        (is (re-find #"Not a directory" (ex-msg e)))))))

(deftest copy-tree-follow-src-dest-links-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (files d "src-dir/src-bar/src-baz/src-file.txt"
             "dest-dir/dest-bar/dest-baz/dest-file.txt")
      (fs/create-sym-link (fs/path d "link-src-dir") "src-dir")
      (fs/create-sym-link (fs/path d "link-dest-dir") "dest-dir")
      (fs/copy-tree (fs/path d "link-src-dir") (fs/path d "link-dest-dir"))
      (is (= ["dest-dir/dest-bar/dest-baz/dest-file.txt"
              "dest-dir/src-bar/src-baz/src-file.txt"
              "link-dest-dir/"
              "link-src-dir/"
              "src-dir/src-bar/src-baz/src-file.txt"]
             (list-tree d))))))

(deftest copy-tree-follow-src-link-new-dest-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (files d "src-dir/bar/baz/somefile.txt")
      (fs/create-sym-link (fs/path d "link-src-dir") "src-dir")
      (fs/copy-tree (fs/path d "link-src-dir") (fs/path d "new-dest-dir"))
      (is (= ["link-src-dir/"
              "new-dest-dir/bar/baz/somefile.txt"
              "src-dir/bar/baz/somefile.txt"]
             (list-tree d))))))

(deftest move-to-file-test
  (fs/with-temp-dir [d]
    (files d "src-dir/foo.txt" "dest-dir/")
    (let [content (slurp-str (fs/path d "src-dir/foo.txt"))]
      (fs/move (fs/path d "src-dir/foo.txt") (fs/path d "dest-dir/foo.txt"))
      (is (= ["dest-dir/foo.txt" "src-dir/"] (list-tree d)))
      (is (= content (slurp-str (fs/path d "dest-dir/foo.txt")))))))

(deftest move-to-dir-test
  (fs/with-temp-dir [d]
    (files d "src-dir/foo.txt" "dest-dir/")
    (let [content (slurp-str (fs/path d "src-dir/foo.txt"))]
      (fs/move (fs/path d "src-dir/foo.txt") (fs/path d "dest-dir"))
      (is (= ["dest-dir/foo.txt" "src-dir/"] (list-tree d)))
      (is (= content (slurp-str (fs/path d "dest-dir/foo.txt")))))))

(deftest size-test
  (fs/with-temp-dir [d]
    (fs/create-dir (fs/path d "dir"))
    (fs/write-bytes (fs/path d "file") (string->bytes "hello"))
    (is (= 5 (fs/size (fs/path d "file"))))
    (is (not (neg? (fs/size (fs/path d "dir"))))
        "size of dirs is unspecified by underlying API")))

(deftest create-dir-test
  (fs/with-temp-dir [d]
    (is (fs/create-dir (fs/path d "foo")))
    (is (= ["foo/"] (list-tree d)))
    (is (fs/directory? (fs/path d "foo")))))

(deftest create-dirs-test
  (fs/with-temp-dir [d]
    (let [nested (fs/path d "a" "b" "c")]
      (fs/create-dirs nested)
      (is (fs/directory? nested)))))

(deftest delete-tree-test
  (fs/with-temp-dir [d]
    (files d "foo/bar/baz/file.txt")
    (is (= "foo" (fs/unixify (fs/relativize d (fs/delete-tree (fs/path d "foo"))))))
    (is (= [] (list-tree d)))))

(deftest delete-tree-nested-test
  (fs/with-temp-dir [d]
    (files d "foo/bar/baz/file.txt")
    (is (= "foo/bar/baz" (fs/unixify (fs/relativize d (fs/delete-tree (fs/path d "foo/bar/baz"))))))
    (is (= ["foo/bar/"] (list-tree d)))))

(deftest delete-tree-ok-if-dir-missing-test
  (fs/with-temp-dir [d]
    (is (nil? (fs/delete-tree (fs/path d "foo"))))
    (is (nil? (fs/delete-tree (fs/path d "foo/bar/baz"))))))

(deftest sym-link-test
  ;; symlink creation needs privileges on Windows
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (let [target (str (fs/path d "target.txt"))
            link (str (fs/path d "link.txt"))]
        (fs/write-bytes target (string->bytes "hi"))
        (fs/create-sym-link link target)
        (is (fs/sym-link? link))
        (is (fs/exists? link))
        (is (= target (str (fs/read-link link))))))))

(deftest directory?-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (fs/create-dir (fs/path d "dir"))
      (let [link (fs/path d "dir-link")]
        (fs/create-sym-link link (fs/path d "dir"))
        (is (= true (fs/directory? link) (fs/directory? link {:nofollow-links false}))
            "following links")
        (is (= false (fs/directory? link {:nofollow-links true}))
            "not following links: the link itself is not a directory")))))

(deftest regular-file?-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (let [target (fs/path d "file")
            link (fs/path d "file-link")]
        (fs/write-bytes target (string->bytes "x"))
        (fs/create-sym-link link target)
        (is (= true (fs/regular-file? link) (fs/regular-file? link {:nofollow-links false}))
            "following links")
        (is (= false (fs/regular-file? link {:nofollow-links true}))
            "not following links: the link itself is not a regular file")))))

(deftest exists?-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (let [link (fs/path d "link")]
        (fs/create-sym-link link (fs/path d "non-existent-target"))
        (is (= false (fs/exists? link)) "following link to non-existent target")
        (is (= false (fs/exists? link {:nofollow-links false})) "following")
        (is (= true (fs/exists? link {:nofollow-links true})) "not following: the link exists")))))

(deftest last-modified-time-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (let [target (fs/path d "file")
            link (fs/path d "link")
            t-target (fs/millis->file-time 1577836800000)
            t-link (fs/millis->file-time 1609459200000)]
        (fs/write-bytes target (string->bytes "x"))
        (fs/create-sym-link link target)
        (fs/set-last-modified-time target t-target)
        (testing "following the link reads the target's time"
          (is (= 1577836800000 (fs/file-time->millis (fs/last-modified-time link))))
          (is (= 1577836800000 (fs/file-time->millis (fs/last-modified-time link {:nofollow-links false})))))
        (testing "not following reads the link's own time, where settable"
          (when (set-sym-link-mtime! link t-link)
            (is (= 1609459200000 (fs/file-time->millis (fs/last-modified-time link {:nofollow-links true}))))))))))

;;;; Symlink move / delete-tree

(deftest move-bad-link-to-bad-link-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (fs/create-sym-link (fs/path d "bad-link1") "bad-target1")
      (fs/create-sym-link (fs/path d "bad-link2") "bad-target2")
      (fs/move (fs/path d "bad-link1") (fs/path d "bad-link2") {:replace-existing true})
      (is (= ["bad-link2"] (list-tree d)))
      (is (= "bad-target1" (fs/unixify (fs/read-link (fs/path d "bad-link2"))))))))

(deftest move-good-link-to-good-link-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (fs/create-dir (fs/path d "dir1"))
      (fs/create-dir (fs/path d "dir2"))
      (fs/create-sym-link (fs/path d "good-link1") "dir1")
      (fs/create-sym-link (fs/path d "good-link2") "dir2")
      (fs/move (fs/path d "good-link1") (fs/path d "good-link2") {:replace-existing true})
      (is (= ["dir1/" "dir2/" "good-link2/"] (list-tree d)))
      (is (= "dir1" (fs/unixify (fs/read-link (fs/path d "good-link2"))))))))

(deftest move-good-link-to-good-link-no-replace-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (fs/create-dir (fs/path d "dir1"))
      (fs/create-dir (fs/path d "dir2"))
      (fs/create-sym-link (fs/path d "good-link1") "dir1")
      (fs/create-sym-link (fs/path d "good-link2") "dir2")
      (is (some? (caught #(fs/move (fs/path d "good-link1") (fs/path d "good-link2") {:replace-existing false})))))))

(deftest move-good-link-under-dir-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (fs/create-dir (fs/path d "dir1"))
      (fs/create-dir (fs/path d "dir2"))
      (fs/create-sym-link (fs/path d "good-link1") "dir1")
      (fs/move (fs/path d "good-link1") (fs/path d "dir2"))
      (is (= ["dir1/" "dir2/good-link1"] (list-tree d)))
      (is (= "dir1" (fs/unixify (fs/read-link (fs/path d "dir2/good-link1"))))))))

(deftest move-file-to-good-link-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (fs/write-bytes (fs/path d "file1.txt") (string->bytes "x"))
      (fs/create-dir (fs/path d "dir1"))
      (fs/create-sym-link (fs/path d "good-link1") "dir1")
      (fs/move (fs/path d "file1.txt") (fs/path d "good-link1") {:replace-existing true})
      (is (= ["dir1/" "good-link1"] (list-tree d)))
      (is (= false (fs/sym-link? (fs/path d "good-link1")))))))

(deftest move-good-link-to-file-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (fs/write-bytes (fs/path d "file1.txt") (string->bytes "x"))
      (fs/create-dir (fs/path d "dir1"))
      (fs/create-sym-link (fs/path d "good-link1") "dir1")
      (fs/move (fs/path d "good-link1") (fs/path d "file1.txt") {:replace-existing true})
      (is (= ["dir1/" "file1.txt/"] (list-tree d)))
      (is (= true (fs/sym-link? (fs/path d "file1.txt")))))))

(deftest rename-good-link-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (fs/create-dir (fs/path d "dir1"))
      (fs/create-sym-link (fs/path d "good-link1") "dir1")
      (fs/move (fs/path d "good-link1") (fs/path d "good-link2"))
      (is (= ["dir1/" "good-link2/"] (list-tree d)))
      (is (= true (fs/sym-link? (fs/path d "good-link2"))))
      (is (= "dir1" (fs/unixify (fs/read-link (fs/path d "good-link2"))))))))

(deftest move-link-without-replace-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (fs/create-dir (fs/path d "dir1"))
      (fs/create-dir (fs/path d "dir2"))
      (fs/create-sym-link (fs/path d "good-link1") "dir1")
      (fs/create-sym-link (fs/path d "good-link2") "dir2")
      (is (some? (caught #(fs/move (fs/path d "good-link1") (fs/path d "good-link2"))))))))

(deftest delete-tree-does-not-follow-symlink-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (fs/create-dir (fs/path d "dir1"))
      (fs/create-dir (fs/path d "dir2"))
      (fs/write-bytes (fs/path d "dir2/foo") (string->bytes "x"))
      (fs/create-sym-link (fs/path d "dir1/link-to-dir2") "../dir2")
      (is (= true (fs/same-file? (fs/path d "dir1/link-to-dir2") (fs/path d "dir2"))) "precondition: link")
      (fs/delete-tree (fs/path d "dir1"))
      (is (= ["dir2/foo"] (list-tree d))))))

(deftest delete-tree-good-sym-link-root-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (fs/create-dirs (fs/path d "foo/bar/baz"))
      (fs/create-sym-link (fs/path d "good-link") "foo")
      (fs/delete-tree (fs/path d "good-link"))
      (is (= ["foo/bar/baz/"] (list-tree d)) "link was deleted, dir was not"))))

(deftest delete-tree-bad-sym-link-root-sym-link-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (fs/create-sym-link (fs/path d "bad-link") "bad-target")
      (fs/delete-tree (fs/path d "bad-link"))
      (is (= [] (list-tree d)) "bad link was deleted"))))

;;;; Owner

(deftest file-owner-test
  (fs/with-temp-dir [d]
    (fs/write-bytes (fs/path d "file") (string->bytes "x"))
    (is (= (str (fs/owner d)) (str (fs/owner (fs/path d "file")))))))

(deftest file-owner-sym-link-test
  ;; assumes the owner of "/" differs from the owner of a file in a temp dir
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (fs/write-bytes (fs/path d "file") (string->bytes "x"))
      (fs/create-sym-link (fs/path d "my-link") "/")
      (is (not= (fs/owner (fs/path d "file")) (fs/owner "/"))
          "sanity test: owners differ for root and a temp-dir file")
      (is (= (fs/owner "/")
             (fs/owner (fs/path d "my-link"))
             (fs/owner (fs/path d "my-link") {:nofollow-links false}))
          "following link")
      (is (= (fs/owner (fs/path d "file"))
             (fs/owner (fs/path d "my-link") {:nofollow-links true}))
          "not following link"))))

(deftest touch-test
  (fs/with-temp-dir [d]
    (let [f (str (fs/path d "f.txt"))]
      (is (not (fs/exists? f)))
      (fs/touch f)
      (is (fs/exists? f))))
  (testing ":time on an existing file"
    (fs/with-temp-dir [d]
      (let [f (fs/path d "f.txt")]
        (fs/write-bytes f (string->bytes "x"))
        (fs/touch f {:time 1000})
        (is (= 1000 (fs/file-time->millis (fs/last-modified-time f)))))))
  (testing ":time creates the file and sets the time"
    (fs/with-temp-dir [d]
      (let [f (fs/path d "new.txt")]
        (fs/touch f {:time 1000})
        (is (fs/exists? f))
        (is (= 1000 (fs/file-time->millis (fs/last-modified-time f)))))))
  (testing ":nofollow-links on a symlink"
    (when-not (fs/windows?)
      (fs/with-temp-dir [d]
        (let [f (fs/path d "file.txt")
              lnk (fs/path d "link.txt")
              t-file (fs/millis->file-time 1000)
              t-link (fs/millis->file-time 2000)]
          (fs/write-bytes f (string->bytes "x"))
          (fs/create-sym-link lnk f)
          (fs/touch f {:time t-file})
          (when (set-sym-link-mtime! lnk t-link)
            (fs/touch lnk {:time (fs/millis->file-time 3000) :nofollow-links true})
            (is (= 1000 (fs/file-time->millis (fs/last-modified-time f))))
            (is (= 3000 (fs/file-time->millis (fs/last-modified-time lnk {:nofollow-links true}))))))))))

(deftest canonicalize-missing-test
  (fs/with-temp-dir [d]
    (let [missing (fs/path d "does-not-exist")]
      (is (= (fs/unixify (fs/canonicalize d))
             (fs/unixify (fs/parent (fs/canonicalize missing)))))
      (is (= "does-not-exist" (fs/file-name (fs/canonicalize missing)))))))

(deftest update-file-test
  (fs/with-temp-dir [d]
    (let [f (str (fs/path d "f.txt"))]
      (fs/write-lines f ["hello"])
      (fs/update-file f str/upper-case)
      (is (str/includes? (first (fs/read-all-lines f)) "HELLO")))))

;;;; Glob / walk

(deftest glob-test
  (fs/with-temp-dir [d]
    (files d "README.md" "project.clj" ".gitignore"
           "dira1/foo.txt"
           "dira1/dirb1/README.md"
           "dira1/dirb1/source.clj"
           "dira1/dirb1/dirc1/"
           "dira2/dirb1/test.cljc")
    (testing "glob single"
      (is (= ["README.md"] (rel-entries d (fs/glob d "README.md")))))
    (testing "glob ** multiple with same filename auto-recursive"
      (is (= ["README.md" "dira1/dirb1/README.md"]
             (rel-entries d (fs/glob d "**README.md")))))
    (testing "glob ** but disable recursion"
      (is (= ["README.md"] (rel-entries d (fs/glob d "**README.md" {:recursive false})))))
    (testing "glob recursive by extension"
      (is (= ["dira1/dirb1/source.clj" "dira2/dirb1/test.cljc" "project.clj"]
             (rel-entries d (fs/glob d "**.{clj,cljc}")))))
    (testing "glob also matches directories and doesn't return the specified root directory"
      (is (= ["dira1/dirb1/README.md" "dira1/dirb1/dirc1/" "dira1/dirb1/source.clj"]
             (rel-entries d (fs/glob (fs/path d "dira1/dirb1") "**")))))
    (when-not (fs/windows?)
      (testing "hidden files are not matched by default"
        (is (= [] (rel-entries d (fs/glob d "*git*")))))
      (testing "hidden files matched when :hidden option specified"
        (is (= [".gitignore"] (rel-entries d (fs/glob d "*git*" {:hidden true})))))
      (testing "hidden files automatically matched when pattern starts with a dot"
        (is (= [".gitignore"] (rel-entries d (fs/glob d ".gitig*"))))))))

(deftest walk-file-tree-semantics-test
  (testing "a file root is visited via visit-file, no dir callbacks"
    (fs/with-temp-dir [d]
      (let [f (fs/path d "f.txt")
            log (atom [])]
        (fs/write-bytes f (string->bytes "x"))
        (fs/walk-file-tree f {:visit-file (fn [p _] (swap! log conj [:file (fs/file-name p)]) :continue)
                              :pre-visit-dir (fn [_ _] (swap! log conj :pre) :continue)
                              :post-visit-dir (fn [_ _] (swap! log conj :post) :continue)})
        (is (= [[:file "f.txt"]] @log)))))
  (testing ":skip-subtree skips the subtree and its post-visit-dir, not siblings"
    (fs/with-temp-dir [d]
      (files d "sub/child.txt" "top.txt")
      (let [posts (atom #{}) files-seen (atom #{})]
        (fs/walk-file-tree d {:pre-visit-dir (fn [p _] (if (= "sub" (fs/file-name p)) :skip-subtree :continue))
                              :post-visit-dir (fn [p _] (swap! posts conj (fs/file-name p)) :continue)
                              :visit-file (fn [p _] (swap! files-seen conj (fs/file-name p)) :continue)})
        (is (not (contains? @posts "sub")))
        (is (not (contains? @files-seen "child.txt")))
        (is (contains? @files-seen "top.txt")))))
  (testing ":max-depth boundary visits a directory as a file"
    (fs/with-temp-dir [d]
      (files d "sub/child.txt")
      (let [dirs (atom #{}) files-seen (atom #{})]
        (fs/walk-file-tree d {:max-depth 1
                              :pre-visit-dir (fn [p _] (swap! dirs conj (fs/file-name p)) :continue)
                              :visit-file (fn [p _] (swap! files-seen conj (fs/file-name p)) :continue)})
        (is (contains? @files-seen "sub"))
        (is (not (contains? @dirs "sub")))
        (is (not (contains? @files-seen "child.txt"))))))
  (testing ":max-depth 0 visits a directory root as a file"
    (fs/with-temp-dir [d]
      (files d "f.txt")
      (let [log (atom [])]
        (fs/walk-file-tree d {:max-depth 0
                              :visit-file (fn [_ _] (swap! log conj :file) :continue)
                              :pre-visit-dir (fn [_ _] (swap! log conj :pre) :continue)
                              :post-visit-dir (fn [_ _] (swap! log conj :post) :continue)})
        (is (= [:file] @log))))))

(deftest walk-symlink-cycle-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (fs/create-dirs (fs/path d "a"))
      (fs/create-sym-link (fs/path d "a" "loop") (str (fs/path d "a")))
      (let [failed (atom #{})]
        (fs/walk-file-tree (fs/path d "a")
                           {:follow-links true
                            :visit-file (fn [_ _] :continue)
                            :visit-file-failed (fn [p _] (swap! failed conj (fs/file-name p)) :continue)})
        (is (contains? @failed "loop"))))))

(deftest glob-syntax-error-test
  (fs/with-temp-dir [d]
    (files d "a.clj")
    (is (some? (caught #(fs/glob d "foo["))))
    (is (some? (caught #(fs/glob d "foo{a"))))
    (is (some? (caught #(fs/glob d "{a,{b,c}}"))))
    (is (nil? (caught #(fs/glob d "*.{clj,cljs}"))))))

#?(:cljs
   (deftest glob->regex-test
     (let [m? (fn [pat s] (.test (fs/glob->regex pat) s))]
       (testing "* and ** separator handling"
         (is (m? "*.txt" "b.txt"))
         (is (not (m? "*.txt" "a/b.txt")))
         (is (m? "**.txt" "a/b/c.txt")))
       (testing "? single non-separator char"
         (is (m? "?.clj" "a.clj"))
         (is (not (m? "?.clj" "ab.clj")))
         (is (not (m? "?" "/"))))
       (testing "char classes"
         (is (m? "[ab].clj" "a.clj"))
         (is (not (m? "[ab].clj" "c.clj")))
         (is (m? "[!a].clj" "b.clj"))
         (is (not (m? "[!a].clj" "a.clj")))
         (is (m? "[^a]" "^"))
         (is (m? "[^a]" "a")))
       (testing "braces"
         (is (m? "{a,b}.clj" "a.clj"))
         (is (m? "{a,b}.clj" "b.clj"))
         (is (not (m? "{a,b}.clj" "c.clj"))))
       (testing "escaped metachars are literal"
         (is (m? "foo\\[.txt" "foo[.txt"))
         (is (not (m? "foo\\[.txt" "fooX.txt"))))
       (testing "invalid syntax throws"
         (is (some? (caught #(fs/glob->regex "foo["))))
         (is (some? (caught #(fs/glob->regex "foo{a"))))
         (is (some? (caught #(fs/glob->regex "{a,{b,c}}"))))))))

(deftest glob-special-chars-test
  (testing "char class pattern"
    (fs/with-temp-dir [d]
      (files d "a.clj" "b.clj" "c.clj")
      (is (= ["a.clj" "b.clj"] (rel-entries d (fs/glob d "[ab].clj"))))))
  (testing "brackets in base path matched literally (#142)"
    (fs/with-temp-dir [d]
      (files d "foo [bar]/hello.txt")
      (is (= ["foo [bar]/hello.txt"]
             (rel-entries d (fs/match (fs/path d "foo [bar]") "glob:*.txt"))))))
  (testing "regex pattern is a full match, not a substring"
    (fs/with-temp-dir [d]
      (files d "foo" "foobar")
      (is (= ["foo"] (rel-entries d (fs/match d "regex:foo" {:recursive true}))))))
  (testing "caret in a char class is a literal, not negation"
    (fs/with-temp-dir [d]
      (files d "a" "b" "^")
      (is (= ["^" "a"] (rel-entries d (fs/glob d "[^a]")))))))

(deftest list-dirs-test
  (fs/with-temp-dir [d]
    (files d "d1/a.clj" "d2/b.clj")
    (is (= ["a.clj" "b.clj"]
           (sort (mapv fs/file-name (fs/list-dirs [(fs/path d "d1") (fs/path d "d2")] "*.clj")))))))

(deftest split-paths-test
  (is (= ["a" "b" "c"] (mapv str (fs/split-paths (str/join fs/path-separator ["a" "b" "c"]))))))

(deftest delete-if-exists-test
  (fs/with-temp-dir [d]
    (let [f (fs/path d "f.txt")]
      (fs/write-bytes f (string->bytes "x"))
      (is (fs/delete-if-exists f))
      (is (not (fs/exists? f)))
      (is (not (fs/delete-if-exists f))))))

(deftest real-path-test
  (when-not (fs/windows?)
    (fs/with-temp-dir [d]
      (let [real (fs/path d "real.txt")
            lnk (fs/path d "lnk.txt")]
        (fs/write-bytes real (string->bytes "x"))
        (fs/create-sym-link lnk real)
        (is (= (fs/unixify (fs/real-path real))
               (fs/unixify (fs/real-path lnk))))))))

(deftest create-link-test
  (fs/with-temp-dir [d]
    (let [f (fs/path d "f.txt")
          l (fs/path d "l.txt")]
      (fs/write-bytes f (string->bytes "hi"))
      (is (= (fs/unixify l) (fs/unixify (fs/create-link l f))))
      (is (fs/same-file? f l))
      (is (not (fs/sym-link? l)))
      (is (= "hi" (slurp-str l))))))

(deftest list-dir-test
  (fs/with-temp-dir [d]
    (files d "dir1/" "dir2/foo.txt" "file.txt" "source1.clj" "source2.clj")
    (is (= ["dir1/" "dir2/" "file.txt" "source1.clj" "source2.clj"]
           (rel-entries d (fs/list-dir d))))
    (is (= ["dir1/" "dir2/"]
           (rel-entries d (fs/list-dir d (fn [x] (fs/directory? x))))))
    (is (= [] (vec (fs/list-dir d (fn [_] false)))))
    (is (= ["source1.clj" "source2.clj"]
           (rel-entries d (fs/list-dir d "*.clj"))))))

(deftest walk-file-tree-test
  (fs/with-temp-dir [d]
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

(deftest expand-home-test
  (is (= (fs/unixify (fs/path (fs/home) "foo"))
         (fs/unixify (fs/expand-home "~/foo"))))
  (is (= "rel/path" (fs/unixify (fs/expand-home "rel/path")))))

(deftest windows-test
  (is (= (fs/windows?) (= "\\" fs/file-separator))))

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
  (fs/with-temp-dir [d]
    (let [f (str (fs/path d "f.txt"))]
      (fs/write-bytes f (string->bytes "x"))
      (let [t (fs/last-modified-time f)]
        (is (file-time? t))
        (is (pos? (fs/file-time->millis t)))))))

(deftest creation-time-test
  (fs/with-temp-dir [d]
    (let [f (str (fs/path d "f.txt"))]
      (fs/write-bytes f (string->bytes "x"))
      (let [t (fs/creation-time f)]
        (is (file-time? t))
        (is (pos? (fs/file-time->millis t)))))))

;;;; Attributes

(deftest read-attributes-test
  (fs/with-temp-dir [d]
    (let [f (fs/path d "f.txt")]
      (fs/write-bytes f (string->bytes "hello"))
      (let [m (fs/read-attributes f "*")]
        (is (file-time? (:lastModifiedTime m)))
        (is (file-time? (:creationTime m)))
        (is (= 5 (:size m)))
        (is (true? (:isRegularFile m)))
        (is (false? (:isDirectory m)))
        (is (false? (:isSymbolicLink m))))
      (is (= {:isDirectory false} (fs/read-attributes f "basic:isDirectory")))
      (is (= {:isDirectory true} (fs/read-attributes d "basic:isDirectory")))
      (is (file-time? (fs/get-attribute f "basic:lastModifiedTime"))))))

(deftest set-attribute-test
  (fs/with-temp-dir [d]
    (let [f (fs/path d "afile")]
      (fs/write-bytes f (string->bytes ""))
      (is (= 100 (-> (fs/set-attribute f "basic:lastModifiedTime" (fs/millis->file-time 100))
                     (fs/read-attributes "*") :lastModifiedTime fs/file-time->millis))))))

(deftest set-last-modified-time-test
  (fs/with-temp-dir [d]
    (let [dir (fs/path d "dir")]
      (fs/create-dir dir)
      (is (= (fs/unixify dir) (fs/unixify (fs/set-last-modified-time dir 0))))
      (is (= 0 (fs/file-time->millis (fs/last-modified-time dir)))))))

(deftest modified-since-test
  (fs/with-temp-dir [d]
    (let [anchor (fs/path d "anchor")
          f1 (fs/path d "f1")]
      (fs/write-bytes anchor (string->bytes "a"))
      (fs/write-bytes f1 (string->bytes "b"))
      (fs/set-last-modified-time anchor 1000)
      (fs/set-last-modified-time f1 2000)
      (is (= [(fs/unixify f1)]
             (mapv fs/unixify (fs/modified-since anchor f1))))
      (fs/set-last-modified-time anchor 2000)
      (is (= [] (vec (fs/modified-since anchor f1)))))))

;;;; Gzip

(deftest gzip-gunzip-test
  (fs/with-temp-dir [d]
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
  (is (fs/absolute? (fs/xdg-config-home)))
  (is (fs/absolute? (fs/xdg-cache-home)))
  (is (fs/absolute? (fs/xdg-data-home)))
  (is (fs/absolute? (fs/xdg-state-home))))
