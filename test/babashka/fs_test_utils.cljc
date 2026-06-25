(ns babashka.fs-test-utils
  (:require [babashka.fs :as fs]))

(defn string->bytes [s]
  #?(:clj (.getBytes ^String s "UTF-8")
     :default (.from js/Buffer s)))

(defn bytes->string [b]
  #?(:clj (String. ^bytes b "UTF-8")
     :default (.toString b)))

(defn file-time?
  "True if `t` is the platform's last-modified-time type."
  [t]
  #?(:clj (instance? java.nio.file.attribute.FileTime t)
     :default (instance? js/Date t)))

(defn tmp []
  (fs/create-temp-dir {:prefix "fs-node-test-"}))

(defmacro with-tmp [[sym] & body]
  `(let [~sym (tmp)]
     (try ~@body
          (finally (fs/delete-tree ~sym {:force true})))))
