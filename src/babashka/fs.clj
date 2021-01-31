(ns babashka.fs
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.io File]
           [java.nio.file.attribute FileAttribute]
           [java.nio.file CopyOption
            ;; DirectoryStream DirectoryStream$Filter
            Files
            FileSystems
            FileVisitOption
            FileVisitResult
            StandardCopyOption
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
  "Converts f into real path via Path#toRealPath."
  ([f] (real-path f nil))
  ([f {:keys [:nofollow-links]}]
   (.toRealPath (as-path f)
                (into-array LinkOption (cond-> []
                                         nofollow-links (conj LinkOption/NOFOLLOW_LINKS))))))

(defn absolute-path
  "Converts f into an absolute path via Path#toAbsolutePath."
  [f] (.toAbsolutePath (as-path f)))

(defn ^Path relativize
  "Returns relative path by comparing this with other."
  [this other]
  (.relativize (as-path this) (as-path other)))

(defn hidden?
  "Returns true if f is hidden."
  [f] (.isHidden (as-file f)))

(defn absolute?
  "Returns true if f is hidden."
  [f] (.isAbsolute (as-file f)))

(defn relative?
  "Returns true if f is hidden."
  [f] (not (absolute? f)))

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
   {:keys [pre-visit-dir post-visit-dir
           visit-file visit-file-failed
           follow-links max-depth]}]
  (let [pre-visit-dir (or pre-visit-dir continue)
        post-visit-dir (or post-visit-dir continue)
        visit-file (or visit-file continue)
        max-depth (or max-depth Integer/MAX_VALUE)
        visit-opts (set (cond-> []
                          follow-links (conj FileVisitOption/FOLLOW_LINKS)))
        visit-file-failed (or visit-file-failed
                              (fn [path _attrs]
                                (throw (Exception. (format "Visiting %s failed" (str path))))))]
    (Files/walkFileTree (as-path f)
                        visit-opts
                        max-depth
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

;; TBD if this will be exposed
#_:clj-kondo/ignore
#_(defn- directory-stream
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
  "Given a file and glob pattern, returns matches as vector of
  files. Patterns containing ** or / will cause a recursive walk over
  path. Glob interpretation is done using the rules described in
  https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String).

  Options:

  - :hidden: match hidden files.
  - :follow-links: follow symlinks."
  ([root pattern] (glob root pattern nil))
  ([root pattern {:keys [hidden follow-links max-depth]}]
   (let [base-path (absolute-path root)
         skip-hidden? (not hidden)
         results (atom (transient []))
         past-root? (volatile! nil)
         [base-path pattern recursive]
         (let [pattern (str base-path "/" pattern)
               recursive (or (str/includes? pattern "**")
                             (str/includes? pattern File/separator))]
           [base-path pattern recursive])
         matcher (.getPathMatcher
                  (FileSystems/getDefault)
                  (str "glob:" pattern))
         match (fn [^Path path]
                 (if (.matches matcher path)
                   (swap! results conj! path)
                   nil))]
     (walk-file-tree
      base-path
      {:max-depth max-depth
       :follow-links follow-links
       :pre-visit-dir (fn [dir _attrs]
                        (if (and @past-root?
                                 (or (not recursive)
                                     (and skip-hidden?
                                          (hidden? dir))))
                          (do
                            nil #_(prn :skipping dir)
                            :skip-subtree)
                          (do
                            (if @past-root? (match dir)
                                (vreset! past-root? true))
                            :continue)))
       :visit-file (fn [path _attrs]
                     (when-not (and skip-hidden?
                                    (hidden? path))
                       (match path))
                     :continue)})
     (let [results (persistent! @results)]
       (if (relative? root)
         (mapv #(relativize (real-path ".") %)
               results)
         results)))))

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

(defn create-temp-dir
  "Creates a temporary directory using Files#createDirectories"
  ([]
   (Files/createTempDirectory
    (str (java.util.UUID/randomUUID))
    (into-array java.nio.file.attribute.FileAttribute []))))

(defn sym-link
  "Create a soft link from path to target."
  [path target]
  (Files/createSymbolicLink
   (as-path path)
   (as-path target)
   (make-array FileAttribute 0)))

(defn delete
  "Deletes f via File#delete."
  [f]
  (.delete (as-file f)))

(defn create-dirs
  "Creates directories using Files#createDirectories"
  [path]
  (Files/createDirectories (as-path path) (into-array java.nio.file.attribute.FileAttribute [])))
