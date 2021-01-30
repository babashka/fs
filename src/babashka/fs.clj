(ns babashka.fs
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.io File]
           [java.nio.file CopyOption
            DirectoryStream DirectoryStream$Filter
            Files FileSystems FileVisitResult StandardCopyOption
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

(defn- ^java.io.File as-file
  "Coerces a path into a file if it isn't already one."
  [path]
  (if (instance? Path path) (.toFile ^Path path)
      (io/file path)))

(defn ^Path path
  "Coerces f into a path. Multiple-arg versions treat the first argument as
  parent and subsequent args as children relative to the parent."
  ([f]
   (as-path f))
  ([parent child]
   (as-path (io/file (as-file parent) (as-file child))))
  ([parent child & more]
   (reduce path (path parent child) more)))

(defn ^File file
  "Coerces f into a file. Multiple-arg versions treat the first argument
  as parent and subsequent args as children relative to the parent."
  ([f] (as-file f))
  ([f & fs]
   (apply io/file (map as-file (cons f fs)))))

(defn ^Path real-path
  "Converts f into real path via .toRealPath."
  ([f] (real-path f nil))
  ([f {:keys [:nofollow-links]}]
   (.toRealPath (as-path f)
                (into-array LinkOption (cond-> []
                                         nofollow-links (conj LinkOption/NOFOLLOW_LINKS))))))

(defn ^Path relativize
  "Returns relative path by comparing this with other."
  [this other]
  (.relativize (as-path this) (as-path other)))

(defn hidden?
  "Returns true if f is hidden."
  [f] (.isHidden (as-file f)))

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
  [f
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
                              file-visit-result)))))

;; TBD if this will be exposed
#_:clj-kondo/ignore
(defn- directory-stream
  (^DirectoryStream [path]
   (Files/newDirectoryStream (as-path path)))
  (^DirectoryStream [path {:keys [:glob :accept]}]
   (if glob
     (Files/newDirectoryStream (as-path path) (str glob))
     (let [accept* accept]
       (Files/newDirectoryStream (as-path path)
                                 (reify DirectoryStream$Filter
                                   (accept [_ entry]
                                     (accept* entry))))))))

(defn glob
  "Given a file and glob pattern, returns matches as vector of files. By
  default hidden files are not matched. This can be enabled by
  setting :hidden to true in opts. Patterns starting with **/ will
  cause a walk over the entire file tree."
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
                     (swap! results conj! path))))
         past-root? (volatile! nil)
         recursive (str/starts-with? pattern "**/")]
     (walk-file-tree base-path {:pre-visit-dir (fn [dir _attrs]
                                                 (if (or (and @past-root? (not recursive))
                                                         (and skip-hidden?
                                                              (hidden? dir)))
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

(defn directory?
  ([f] (directory? f nil))
  ([f {:keys [:nofollow-links]}]
   (let [opts (cond-> []
                nofollow-links (conj LinkOption/NOFOLLOW_LINKS))]
     (Files/isDirectory (as-path f)
                        ^"[Ljava.nio.file.LinkOption;"
                        (into-array LinkOption opts)))))

(defn copy
  "Copies src file to dest file. Supported options: :recursive (copy
  file tree), :replace-existing, :copy-attributes
  and :nofollow-links."
  ([src dest] (copy src dest nil))
  ([src dest {:keys [:replace-existing
                     :copy-attributes
                     :nofollow-links
                     :recursive]}]
   (let [copy-options (into-array CopyOption
                                  (cond-> []
                                    replace-existing (conj StandardCopyOption/REPLACE_EXISTING)
                                    copy-attributes  (conj StandardCopyOption/COPY_ATTRIBUTES)
                                    nofollow-links   (conj LinkOption/NOFOLLOW_LINKS)))
         link-options (into-array LinkOption
                                  (cond-> []
                                    nofollow-links (conj LinkOption/NOFOLLOW_LINKS)))]
     (if recursive
       (let [from (real-path src {:nofollow-links nofollow-links})
             to (real-path dest {:nofollow-links nofollow-links})]
         (walk-file-tree from {:pre-visit-dir (fn [dir _attrs]
                                                (let [rel (relativize from dir)
                                                      to-dir (path to rel)]
                                                  (when-not (Files/exists to-dir link-options)
                                                    (Files/copy ^Path dir to-dir
                                                                ^"[Ljava.nio.file.CopyOption;"
                                                                copy-options)))
                                                :continue)
                               :visit-file (fn [from-path _attrs]
                                             (let [rel (relativize from from-path)
                                                   to-file (path to rel)]
                                               (Files/copy ^Path from-path to-file
                                                           ^"[Ljava.nio.file.CopyOption;"
                                                           copy-options)
                                               :continue))}))
       (Files/copy (as-path src) (as-path dest)
                   ^"[Ljava.nio.file.CopyOption;"
                   copy-options)))))

(defn tmp-dir
  ([]
   (Files/createTempDirectory (str (java.util.UUID/randomUUID))
                              (into-array java.nio.file.attribute.FileAttribute []))))
