(ns babashka.fs-test-util
  (:require [clojure.string :as str]
            [babashka.fs :as fs]))

(defn clean-cwd []
  (when-not (str/ends-with? (System/getProperty "user.dir") (str "target" fs/file-separator "test-cwd"))
      (throw (ex-info "tests mutate cwd, must run tests via: bb test-cwd" {})))
    (doseq [f (fs/list-dir ".")]
      (fs/delete-tree f {:force true})))
