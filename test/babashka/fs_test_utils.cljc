(ns babashka.fs-test-utils
  (:require [babashka.fs :as fs]))

(defn string->bytes [s]
  #?(:clj (.getBytes ^String s "UTF-8")
     :default (.from js/Buffer s)))

(defn bytes->string [b]
  #?(:clj (String. ^bytes b "UTF-8")
     :default (.toString b)))

(defn tmp []
  (fs/create-temp-dir {:prefix "fs-node-test-"}))

(defmacro with-tmp [[sym] & body]
  `(let [~sym (tmp)]
     (try ~@body
          (finally (fs/delete-tree ~sym {:force true})))))
