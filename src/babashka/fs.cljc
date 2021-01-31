(ns babashka.fs
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.io File]
           [java.nio.file.attribute FileAttribute #_PosixFilePermissions]
           [java.nio.file CopyOption
            #?(:clj DirectoryStream) #?(:clj DirectoryStream$Filter)
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
  "Coerces f into a Path. Multiple-arg versions treat the first argument as
  parent and subsequent args as children relative to the parent."
  ([f]
   (as-path f))
  ([parent child]
   (as-path (io/file (as-file parent) (as-file child))))
  ([parent child & more]
   (reduce path (path parent child) more)))

(defn ^File file
  "Coerces f into a File. Multiple-arg versions treat the first argument
  as parent and subsequent args as children relative to the parent."
  ([f] (as-file f))
  ([f & fs]
   (apply io/file (map as-file (cons f fs)))))

(defn- ->link-opts ^"[Ljava.nio.file.LinkOption;"
  [nofollow-links]
  (into-array LinkOption
              (cond-> []
                nofollow-links
                (conj LinkOption/NOFOLLOW_LINKS))))

(defn ^Path real-path
  "Converts f into real path via Path#toRealPath."
  ([f] (real-path f nil))
  ([f {:keys [:nofollow-links]}]
   (.toRealPath (as-path f) (->link-opts nofollow-links))))

;;;; Predicates

(defn directory?
  "Returns true if f is a directory, using Files/isDirectory."
  ([f] (directory? f nil))
  ([f {:keys [:nofollow-links]}]
   (Files/isDirectory (as-path f)
                      (->link-opts nofollow-links))))

(defn hidden?
  "Returns true if f is hidden."
  [f] (Files/isHidden (as-path f)))

(defn absolute?
  "Returns true if f represents an absolute path."
  [f] (.isAbsolute (as-path f)))

(defn executable?
  "Returns true if f is executable."
  [f] (Files/isExecutable (as-path f)))

(defn readable?
  "Returns true if f is readable"
  [f] (Files/isReadable (as-path f)))

(defn writable?
  "Returns true if f is readable"
  [f] (Files/isWritable (as-path f)))

(defn relative?
  "Returns true if f represents a relative path."
  [f] (not (absolute? f)))

(defn exists?
  "Returns true if f exists."
  ([f] (exists? f nil))
  ([f {:keys [:nofollow-links]}]
   (Files/exists
    (as-path f)
    (->link-opts nofollow-links))))

;;;; End predicates

(defn elements
  "Returns all elements of f as paths."
  [f]
  (iterator-seq (.iterator (as-path f))))

(defn absolutize
  "Converts f into an absolute path via Path#toAbsolutePath."
  [f] (.toAbsolutePath (as-path f)))

(defn ^Path relativize
  "Returns relative path by comparing this with other."
  [this other]
  (.relativize (as-path this) (as-path other)))

(defn file-name
  "Returns farthest element from the root as string, if any."
  [x]
  (.getName (as-file x)))

(defn normalize
  "Normalizes f via Path#normalize."
  [f]
  (.normalize (as-path f)))

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

#?(:bb nil :clj
   (defn directory-stream
     "Returns a stream of all files in dir. The caller of this function is
  responsible for closing the stream, e.g. using with-open. The stream
  can consumed as a seq by calling seq on it. Accepts optional glob or
  accept function of one argument."
     (^DirectoryStream [dir]
      (Files/newDirectoryStream (as-path dir)))
     (^DirectoryStream [dir glob-or-accept]
      (if (string? glob-or-accept)
        (Files/newDirectoryStream (as-path dir) (str glob-or-accept))
        (let [accept* glob-or-accept]
          (Files/newDirectoryStream (as-path dir)
                                    (reify DirectoryStream$Filter
                                      (accept [_ entry]
                                        (boolean (accept* entry))))))))))

#?(:bb nil :clj
   (defn list-dir
     "Returns all paths in dir in vector. Uses directory-stream."
     ([dir]
      (with-open [stream (directory-stream dir)]
        (vec stream)))
     ([dir glob-or-accept]
      (with-open [stream (directory-stream dir glob-or-accept)]
        (vec stream)))))

(def ^:const file-separator File/separator)
(def ^:const path-separator File/pathSeparator)

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
   (let [base-path (absolutize root)
         skip-hidden? (not hidden)
         results (atom (transient []))
         past-root? (volatile! nil)
         [base-path pattern recursive]
         (let [pattern (str base-path "/" pattern)
               recursive (or (str/includes? pattern "**")
                             (str/includes? pattern file-separator))]
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

(defn- ->copy-opts [replace-existing copy-attributes nofollow-links]
  (into-array CopyOption
              (cond-> []
                replace-existing (conj StandardCopyOption/REPLACE_EXISTING)
                copy-attributes  (conj StandardCopyOption/COPY_ATTRIBUTES)
                nofollow-links   (conj LinkOption/NOFOLLOW_LINKS))))

(defn copy
  "Copies src file to dest file. Supported options: :recursive (copies
  tree using walk-file-tree), :replace-existing, :copy-attributes
  and :nofollow-links."
  ([src dest] (copy src dest nil))
  ([src dest {:keys [:replace-existing
                     :copy-attributes
                     :nofollow-links
                     :recursive]}]
   (let [copy-options (->copy-opts replace-existing copy-attributes nofollow-links)
         link-options (->link-opts nofollow-links)]
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

#_(defn posix-file-permissions [s]
  (cond (string? s)
        (PosixFilePermissions/fromString s)
        ;; (set? s)
        ;; (into #{} (map keyword->posix-file-permission) s)
        :else
        (throw (java.lang.IllegalArgumentException. (str "Not supported: " s " of type: " (class s))))))

#_(defn as-file-attributes [posix-file-permissions]
  (PosixFilePermissions/asFileAttribute posix-file-permissions))

(defn create-temp-dir
  "Creates a temporary directory using Files#createDirectories"

  ([]
   (Files/createTempDirectory
    (str (java.util.UUID/randomUUID))
    (into-array FileAttribute [])))
  ;; TODO: figure out if fixed args work to cover all the method overloads, else we need to use a map
  #_([prefix]
   (Files/createTempDirectory
    (str prefix)
    (into-array FileAttribute [])))
  #_([path file-attrs]
   (Files/createTempDirectory
    (str path)
    (into-array FileAttribute []))))

(defn create-sym-link
  "Create a soft link from path to target."
  [path target]
  (Files/createSymbolicLink
   (as-path path)
   (as-path target)
   (make-array FileAttribute 0)))

(defn delete
  "Deletes f via Path#delete. Returns nil if the delete was succesful, throws otherwise."
  [dir]
  (Files/delete (as-path dir)))

(defn delete-if-exists
  "Deletes f via Path#deleteIfExists if it exists. Returns true if the delete was succesful, false if the dir didn't exist."
  [f]
  (Files/deleteIfExists (as-path f)))

(defn delete-tree
  "Deletes a file tree."
  ([root] (delete-tree root nil))
  ([root {:keys [:nofollow-links] :as opts}]
   (when (directory? root opts)
     (doseq [path (list-dir root)]
       (delete-tree path opts))
     (delete root))))

(defn create-dir
  "Creates directories using Files#createDirectory"
  [path]
  (Files/createDirectory (as-path path) (into-array FileAttribute [])))

(defn create-dirs
  "Creates directories using Files#createDirectories"
  [path]
  (Files/createDirectories (as-path path) (into-array FileAttribute [])))

(defn move
  "Move or rename a file to a target file. Optional
  [copy-options](http://docs.oracle.com/javase/7/docs/api/java/nio/file/CopyOption.html)
  may be provided."
  ([source target] (move source target nil))
  ([source target {:keys [:replace-existing
                          :copy-attributes
                          :nofollow-links]}]
   (Files/move (as-path source)
               (as-path target)
               (->copy-opts replace-existing copy-attributes nofollow-links))))

(defn parent
  "Returns parent of f, is it exists."
  [f]
  (.getParent (as-path f)))

(defn last-modified
  "Returns last-modified timestamp via File#lastModified."
  [f]
  (.lastModified (as-file f)))

(defn size
  [f]
  (Files/size (as-path f)))
