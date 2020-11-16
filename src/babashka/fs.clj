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
  "Given a path and glob pattern, returns matches as vector of paths. By
  default it will not search within hidden directories. This can be
  overriden by passing an opts map with a different implementation for :pre-visit-dir."
  ([path pattern] (glob path pattern nil))
  ([path pattern {:keys [:pre-visit-dir]
                  :or {pre-visit-dir (fn [dir _attrs]
                                       (if (hidden? dir)
                                         :visit/skip-subtree
                                         :visit/continue))}}]
   (let [base-path (real-path path)
         matcher (.getPathMatcher
                  (FileSystems/getDefault)
                  (str "glob:" pattern))
         results (atom [])
         match (fn [path](let [relative-path (.relativize base-path ^Path path)]
                           (when (.matches matcher relative-path)
                             (swap! results conj path))))]
     (walk-file-tree base-path {:pre-visit-dir (fn [dir attrs]
                                                 (match dir)
                                                 (pre-visit-dir dir attrs))
                                :visit-file (fn [path _attrs]
                                              (match path)
                                              :visit/continue)})
     @results)))
