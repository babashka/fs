(ns babashka.fs-test-util
  (:require [babashka.fs :as fs]
            [babashka.process :as process]
            [clojure.string :as str])
  (:import [java.lang ProcessHandle]))

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

(defn os []
  (let [os-name (str/lower-case (System/getProperty "os.name"))]
    (condp re-find os-name
      #"win" :win
      #"mac" :mac
      #"(nix|nux|aix)" :unix
      #"sunos" :solaris
      :unknown)))

(defn jdk-major []
  (let [version (-> (System/getProperty "java.version")
                    (str/split #"\."))]
    (if (= "1" (first version))
      (Long/valueOf (second version))
      (Long/valueOf (first version)))))

(defn- umask->rwx [umask]
  (reduce (fn [acc n]
            (str acc (let [n (Long/parseLong (str n))]
                       (str 
                         (if (zero? (bit-and n 4)) "-" "r")
                         (if (zero? (bit-and n 2)) "-" "w")
                         (if (zero? (bit-and n 1)) "-" "x")))))
          ""
          (subs umask 1)))

(defonce umask (some-> (case (os)
                         ;; linux has good support for getting umask of current process
                         :unix (->> (process/shell {:out :string}
                                                   (format "sh -c \"cat /proc/%d/status\"" (.pid (ProcessHandle/current))))
                                    :out
                                    str/split-lines
                                    (keep #(re-matches #"Umask:\s+(\d+)" %))
                                    first
                                    last)
                         ;; on macos, we only have umask
                         :mac (-> (process/shell {:out :string}
                                                 "sh -c umask" )
                                  :out
                                  str/trim)
                         nil)
                       umask->rwx))

(defn umasked [permissions umask]
  (apply str (keep (fn [[p u]] (if (= \- u) p \-))
                   (map vector permissions umask))))

