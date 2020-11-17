(ns babashka.fs
  (:require [clojure.java.io :as io])
  (:import [java.io File]
           [java.nio.file Files FileSystems FileVisitResult
            LinkOption Path
            FileVisitor]))

(import '[java.io File]
        '[java.nio.file Files FileSystems FileVisitResult
         LinkOption Path
         FileVisitor])

(set! *warn-on-reflection* true)

(def ^:private fvr-lookup
  {:continue FileVisitResult/CONTINUE
   :skip-subtree FileVisitResult/SKIP_SUBTREE
   :skip-siblings FileVisitResult/SKIP_SIBLINGS
   :terminate FileVisitResult/TERMINATE})

(defn file-visit-result
  [x]
  (if (instance? FileVisitResult x) x
      (or (fvr-lookup x)
          (throw (Exception. "Expected: one of :continue, :skip-subtree, :skip-siblings, :terminate.")))))

(defn- ^Path as-path
  [path]
  (if (instance? Path path) path
      (.toPath (io/file path))))

(defn ^Path path
  "Coerces an as-file into a path if it isn't already one."
  [file]
  (as-path file))

(defn- ^java.io.File as-file
  "Coerces a path into a file if it isn't already one."
  [path]
  (if (instance? Path path) (.toFile ^Path path)
      (io/file path)))

(defn ^File file
  "Coerces a path into a file if it isn't already one."
  [file]
  (as-file file))

(defn ^Path real-path [path & link-options]
  (.toRealPath (as-path path) (into-array LinkOption link-options)))

(def ^:dynamic *cwd* (real-path "."))

(defn ^Path relativize
  ([other] (relativize *cwd* other))
  ([this other]
   (.relativize (as-path this) (as-path other))))

(defn hidden?
  ([] (hidden? *cwd*))
  ([path] (.isHidden (as-file path))))

(def ^:private continue (constantly :continue))

(defn walk-file-tree
  ([opts] (walk-file-tree *cwd* opts))
  ([path
    {:keys [pre-visit-dir post-visit-dir visit-file visit-file-failed]
     :or {pre-visit-dir continue
          post-visit-dir continue
          visit-file continue
          visit-file-failed continue}}]
   (Files/walkFileTree (as-path path)
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
  hidden files are not matched. This can be enabled by setting `:hidden` to
  `true` in `opts`."
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
                     (swap! results conj! (.toFile path)))))]
     (walk-file-tree base-path {:pre-visit-dir (fn [dir _attrs]
                                                 (if (and skip-hidden?
                                                          (hidden? dir))
                                                   :skip-subtree
                                                   (do
                                                     (match dir)
                                                     :continue)))
                                :visit-file (fn [path _attrs]
                                              (when-not (and skip-hidden?
                                                             (hidden? path))
                                                (match path))
                                              :continue)})
     (let [res (persistent! @results)]
       (if-let [fst (get res 0)]
         (if (= fst (.toFile base-path))
           (subvec res 1)
           res)
         res)))))
