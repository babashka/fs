(ns babashka.fs
  (:require [clojure.java.io :as io])
  (:import [java.nio.file Files FileSystems FileVisitResult
            LinkOption Path
            FileVisitor]))

(set! *warn-on-reflection* true)

(def ^:private fvr:continue FileVisitResult/CONTINUE)

(defn- ^Path as-path
  "Coerces an as-file into a path"
  [path]
  (if (instance? Path path) path
      (.toPath (io/file path))))

(defn ^Path real-path [path & link-options]
  (.toRealPath (as-path path) (into-array LinkOption link-options)))

(defn ^Path path [file]
  (as-path (io/file file)))

(defn ^Path relativize [this other]
  (.relativize (as-path this) (as-path other)))

(defn glob
  "Given a path and glob pattern, returns matches as vector of java.nio.file Path."
  [path pattern]
  (let [base-path (real-path path)
        matcher (.getPathMatcher
                 (FileSystems/getDefault)
                 (str "glob:" pattern))
        results (atom [])]
    (Files/walkFileTree base-path
                        (reify FileVisitor
                          (preVisitDirectory [_ dir attrs]
                            fvr:continue)
                          (postVisitDirectory [_ dir attrs]
                            fvr:continue)
                          (visitFile [_ path attrs]
                            (let [relative-path (.relativize base-path ^Path path)]
                              (when (.matches matcher relative-path)
                                (swap! results conj relative-path)))
                            fvr:continue)
                          (visitFileFailed [_ path attrs]
                            fvr:continue)))
    @results))
