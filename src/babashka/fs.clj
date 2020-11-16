(ns babashka.fs
  (:require [clojure.java.io :as io])
  (:import [java.io File]
           [java.nio.file Files FileSystems FileVisitResult
            LinkOption Path
            FileVisitor]))

(set! *warn-on-reflection* true)

(def ^:private keyword->constant
  {:visit/continue FileVisitResult/CONTINUE
   :visit/skip-subtree FileVisitResult/SKIP_SUBTREE})

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

(defn ^Path relativize [this other]
  (.relativize (as-path this) (as-path other)))

(defn hidden? [path]
  (.isHidden (as-file path)))

(def ^:private continue (constantly :visit/continue))

(defn walk-file-tree [path
                      {:keys [pre-visit-dir post-visit-dir visit-file visit-file-failed]
                       :or {pre-visit-dir continue
                            post-visit-dir continue
                            visit-file continue
                            visit-file-failed continue}}]
  (Files/walkFileTree (as-path path)
                      (reify FileVisitor
                        (preVisitDirectory [_ dir attrs]
                          (-> (pre-visit-dir dir attrs)
                              keyword->constant))
                        (postVisitDirectory [_ dir attrs]
                          (-> (post-visit-dir dir attrs)
                              keyword->constant))
                        (visitFile [_ path attrs]
                          (-> (visit-file path attrs)
                              keyword->constant))
                        (visitFileFailed [_ path attrs]
                          (-> (visit-file-failed path attrs)
                              keyword->constant)))))

(defn glob
  "Given a file and glob pattern, returns matches as vector of files. By default
  hidden files are not matched. This can be enabled by setting `:hidden` to
  `true` in `opts`."
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
                                                   :visit/skip-subtree
                                                   (do
                                                     (match dir)
                                                     :visit/continue)))
                                :visit-file (fn [path _attrs]
                                              (when-not (and skip-hidden?
                                                             (hidden? path))
                                                (match path))
                                              :visit/continue)})
     (let [res (persistent! @results)]
       (if-let [fst (get res 0)]
         (if (= fst (.toFile base-path))
           (subvec res 1)
           res)
         res)))))
