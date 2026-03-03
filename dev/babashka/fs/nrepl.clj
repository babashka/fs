(ns babashka.fs.nrepl
  (:require [nrepl.cmdline :as nrepl-cmdline]
            [clojure.java.io :as io]))

(defn save-port-file
  "Babashka fs runs the repl and tests from a scratch test dir under target,
  we'll search upward for the project root and write our .nrepl-port file there."
  [{:keys [port] :as _server} _options]
  (let [cwd (io/file (System/getProperty "user.dir"))
        project-dir-indicator-file ".clj-kondo" 
        project-dir (loop [dir cwd]
                      (cond
                        (nil? dir) (throw (ex-info (format "Upward search from %s for project root dir (containing %s) not found"
                                                           cwd project-dir-indicator-file) {}))
                        (.exists (io/file dir project-dir-indicator-file)) dir 
                        :else (recur (.getParentFile dir))))
        port-file (io/file project-dir ".nrepl-port")]
    (println "Writing:" (str port-file))
    (.deleteOnExit ^java.io.File port-file)
    (spit port-file port)))

(defn -main
  [& args]
  (with-redefs [nrepl-cmdline/save-port-file save-port-file]
    (apply nrepl-cmdline/-main args)))
