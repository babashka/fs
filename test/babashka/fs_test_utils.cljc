(ns babashka.fs-test-utils
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))

(defn string->bytes [s]
  #?(:clj (.getBytes ^String s "UTF-8")
     :default (.from js/Buffer s)))

(defn bytes->string [b]
  #?(:clj (String. ^bytes b "UTF-8")
     :default (.toString b)))

(defn slurp-str
  "Read `p` as a UTF-8 string. Cross-platform stand-in for clojure.core/slurp."
  [p]
  (bytes->string (fs/read-all-bytes p)))

(defn caught
  "Invoke `thunk`, returning the exception it throws, or nil. Cross-platform
  stand-in for `thrown?` which needs a literal class symbol in `catch`."
  [thunk]
  (try (thunk) nil
       (catch #?(:clj Throwable :default :default) e e)))

(defn ex-msg
  "Message of exception `e`, cross-platform."
  [e]
  #?(:clj (ex-message e) :default (.-message e)))

(defn files
  "Create files/dirs under `base`. A spec ending in `/` is a dir, otherwise a
  file whose content is the spec string."
  [base & specs]
  (doseq [spec specs]
    (let [p (str (fs/path base spec))]
      (if (str/ends-with? spec "/")
        (fs/create-dirs p)
        (do (when-let [d (fs/parent p)]
              (fs/create-dirs d))
            (fs/write-bytes p (string->bytes spec)))))))

(defn- annotate-dirs [base entries]
  (->> entries
       (map (fn [e]
              (if (fs/directory? (fs/path base e))
                (str e "/")
                e)))
       sort))

(defn- collapse [sorted]
  (->> sorted
       reverse
       (reduce (fn [acc n]
                 (if (and (seq acc) (fs/starts-with? (last acc) n))
                   acc
                   (conj acc n)))
               [])
       sort
       vec))

(defn rel-entries
  "Sorted, dir-annotated `entries` relative to `base`, no collapse.
  Cross-platform port of the JVM suite's `normalized`."
  [base entries]
  (annotate-dirs base (map #(fs/unixify (fs/relativize base %)) entries)))

(defn list-tree
  "Sorted, collapsed, dir-annotated entries under `base`, relative to `base`.
  Cross-platform port of the JVM suite's `list-tree`."
  [base]
  (->> (fs/glob base "**" {:hidden true})
       (map #(fs/unixify (fs/relativize base %)))
       (annotate-dirs base)
       collapse))

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
