(ns babashka.fs
  (:require [clojure.java.io :as io])
  (:import [java.nio.file Files FileSystems FileVisitResult
            LinkOption Path
            FileVisitor]))

(def ^:private fvr:continue FileVisitResult/CONTINUE)

(defn- ^Path as-path
  "Coerces an as-file into a path"
  [path]
  (.toRealPath (.toPath (io/file path)) (into-array LinkOption [])))

(set! *warn-on-reflection* true)

(defn glob
  "Given a path and glob pattern, returns matches as vector of java.nio.file Path."
  [path pattern]
  (let [base-path (as-path path)
        matcher (.getPathMatcher
                 (FileSystems/getDefault)
                 (str "glob:" pattern))
        results (atom [])]
    (Files/walkFileTree (as-path path)
                        (reify FileVisitor
                          (preVisitDirectory [_ dir attrs]
                            fvr:continue)
                          (postVisitDirectory [_ dir attrs]
                            fvr:continue)
                          (visitFile [_ path attrs]
                            (when (.matches matcher (.relativize base-path ^Path path))
                              (swap! results conj path))
                            fvr:continue)
                          (visitFileFailed [_ path attrs]
                            fvr:continue)))
    @results))
