(ns babashka.fs
  (:require [clojure.java.io :as io])
  (:import [java.io File]
           [java.nio.file Files FileSystems FileVisitResult
            LinkOption Path
            FileVisitor]))

(set! *warn-on-reflection* true)

(def ^:private fvr-lookup
  {:continue FileVisitResult/CONTINUE
   :skip-subtree FileVisitResult/SKIP_SUBTREE
   :skip-siblings FileVisitResult/SKIP_SIBLINGS
   :terminate FileVisitResult/TERMINATE})

(defn- file-visit-result
  [x]
  (if (instance? FileVisitResult x) x
      (or (fvr-lookup x)
          (throw (Exception. "Expected: one of :continue, :skip-subtree, :skip-siblings, :terminate.")))))

(defn- ^Path as-path
  [path]
  (if (instance? Path path) path
      (.toPath (io/file path))))

(defn ^Path path
  "Coerces f into a path if it isn't already one."
  [f]
  (as-path f))

(defn- ^java.io.File as-file
  "Coerces a path into a file if it isn't already one."
  [path]
  (if (instance? Path path) (.toFile ^Path path)
      (io/file path)))

(defn ^File file
  "Coerces f into a file if it isn't already one."
  [f]
  (as-file f))

(defn ^Path real-path
  "Converts f into real path via .toRealPath."
  [f & link-options]
  (.toRealPath (as-path f) (into-array LinkOption link-options)))

(def ^{:dynamic true
       :doc "Current working directory."}
  *cwd* (real-path "."))

(defn ^Path relativize
  "Returns relative path by comparing this with other."
  ([other] (relativize *cwd* other))
  ([this other]
   (.relativize (as-path this) (as-path other))))

(defn hidden?
  "Returns true if f is hidden."
  ([] (hidden? *cwd*))
  ([f] (.isHidden (as-file f))))

(defn file-name
  "Returns farthest element from the root as string, if any."
  [x]
  (.getName (as-file x)))

(def ^:private continue (constantly :continue))

(defn walk-file-tree
  "Walks f using Files/walkFileTree. Visitor functions: pre-visit-dir,
  post-visit-dir, visit-file, visit-file-failed. All visitor functions
  default to (constantly :continue). Supported return
  values: :continue, :skip-subtree, :skip-siblings, :terminate. A
  different return value will throw."
  ([opts] (walk-file-tree *cwd* opts))
  ([f
    {:keys [pre-visit-dir post-visit-dir visit-file visit-file-failed]
     :or {pre-visit-dir continue
          post-visit-dir continue
          visit-file continue
          visit-file-failed continue}}]
   (Files/walkFileTree (as-path f)
                       (reify FileVisitor
                         (preVisitDirectory [_ dir attrs]
                           (-> (pre-visit-dir dir attrs)
                               file-visit-result))
                         (postVisitDirectory [_ dir attrs]
                           (-> (post-visit-dir dir attrs)
                               file-visit-result))
                         (visitFile [_ path attrs]
                           (-> (visit-file path attrs)
                               file-visit-result))
                         (visitFileFailed [_ path attrs]
                           (-> (visit-file-failed path attrs)
                               file-visit-result))))))

(defn glob
  "Given a file and glob pattern, returns matches as vector of files. By default
  hidden files are not matched. This can be enabled by setting :hidden to
  true in opts."
  ([pattern] (glob *cwd* pattern))
  ([path pattern] (glob path pattern nil))
  ([path pattern {:keys [:hidden]}]
   (let [base-path (real-path path)
         skip-hidden? (not hidden)
         matcher (.getPathMatcher
                  (FileSystems/getDefault)
                  (str "glob:" pattern))
         results (atom (transient []))
         match (fn [^Path path]
                 (let [relative-path (.relativize base-path path)]
                   (when (.matches matcher relative-path)
                     (swap! results conj! (.toFile path)))))
         past-root? (volatile! nil)]
     (walk-file-tree base-path {:pre-visit-dir (fn [dir _attrs]
                                                 (if (and skip-hidden?
                                                          (hidden? dir))
                                                   :skip-subtree
                                                   (do
                                                     (if @past-root? (match dir)
                                                         (vreset! past-root? true))
                                                     :continue)))
                                :visit-file (fn [path _attrs]
                                              (when-not (and skip-hidden?
                                                             (hidden? path))
                                                (match path))
                                              :continue)})
     (persistent! @results))))
