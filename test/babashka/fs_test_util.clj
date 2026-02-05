(ns babashka.fs-test-util
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))

(defn clean-cwd []
  (when-not (str/ends-with? (System/getProperty "user.dir") (str "target" fs/file-separator "test-cwd"))
      (throw (ex-info "tests mutate cwd, must run tests via: bb test-cwd" {})))
    (doseq [f (fs/list-dir ".")]
      (fs/delete-tree f {:force true})))

(defn path->str
  "Converts x to string unless x is nil"
  [x]
  (when-not (nil? x)
    (fs/unixify x)))

(defn fsnapshot []
  (->> (#'fs/path-seq ".")
       (map (fn [p]
              {:path (fs/unixify (path->str p))
               :content (when (fs/regular-file? p)
                          (slurp (fs/file p)))
               :attr (dissoc (fs/read-attributes p "*") :lastAccessTime :fileKey)}))
       (sort-by :path)
       (into [])))
