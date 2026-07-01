(ns babashka.fs.macros)

(defmacro with-temp-dir
  "Evaluates body with `temp-dir` bound to the result of `(create-temp-dir opts)`.

  By default, the `temp-dir` will be removed with [[delete-tree]] on exit from the scope.

  Options:
  * see [[create-temp-dir]] for options that control directory creation
  * `:keep` - if `true` does not delete the directory on exit from macro scope.

  Example:
  ```
  (with-temp-dir [d]
    (let [t (path d \"extract\")
      (create-dir t)
      (gunzip path-to-zip t)
      (copy (path t \"the-one-file-I-wanted.txt\") (path permanent-dir \"file-I-extracted.txt\"))))
  ;; d no longer exists here
  ```
  "
  {:arglists '[[[temp-dir] & body]
               [[temp-dir opts] & body]]}
  [[temp-dir opts & more] & body]
  {:pre [(empty? more) (symbol? temp-dir)]}
  `(let [opts# ~opts
         ~temp-dir (babashka.fs/create-temp-dir opts#)]
     (try
       ~@body
       (finally
         (when-not (:keep opts#)
           (babashka.fs/delete-tree ~temp-dir {:force true}))))))
