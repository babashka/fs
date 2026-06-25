(ns babashka.fs-test-utils
  (:require [babashka.fs :as fs]))

(defn tmp []
  (fs/create-temp-dir {:prefix "fs-node-test-"}))

(defmacro with-tmp [[sym] & body]
  `(let [~sym (tmp)]
     (try ~@body
          (finally (fs/delete-tree ~sym {:force true})))))
