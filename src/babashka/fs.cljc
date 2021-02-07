(ns babashka.fs
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :as walk])
  (:import [java.io File]
           [java.nio.file.attribute FileAttribute FileTime PosixFilePermissions]
           [java.nio.file CopyOption
            #?@(:bb [] :clj [DirectoryStream]) #?@(:bb [] :clj [DirectoryStream$Filter])
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

(defn components
  "Returns a seq of all components of f."
  [f]
  (seq (as-path f)))

(defn absolutize
  "Converts f into an absolute path via Path#toAbsolutePath."
  [f] (.toAbsolutePath (as-path f)))

(defn ^Path relativize
  "Returns relative path by comparing this with other."
  [this other]
  (.relativize (as-path this) (as-path other)))

(defn file-name
  "Returns the name of the file or directory. E.g. (file-name \"foo/bar/baz\") returns \"baz\"."
  [x]
  (.getName (as-file x)))

(defn normalize
  "Normalizes f via Path#normalize."
  [f]
  (.normalize (as-path f)))

(def ^:private continue (constantly :continue))

(defn walk-file-tree
  "Walks f using Files/walkFileTree. Visitor functions: :pre-visit-dir,
  :post-visit-dir, :visit-file, :visit-file-failed. All visitor functions
  default to (constantly :continue). Supported return
  values: :continue, :skip-subtree, :skip-siblings, :terminate. A
  different return value will throw."
  [f
   {:keys [:pre-visit-dir :post-visit-dir
           :visit-file :visit-file-failed
           :follow-links :max-depth]}]
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
   (defn- directory-stream
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
     "Returns all paths in dir as vector. For descending into subdirectories use glob."
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
     (let [results (persistent! @results)
           absolute-cwd (absolutize ".")]
       (if (relative? root)
         (mapv #(relativize absolute-cwd %)
               results)
         results)))))

(defn- ->copy-opts ^"[Ljava.nio.file.CopyOption;"
  [replace-existing copy-attributes atomic-move nofollow-links]
  (into-array CopyOption
              (cond-> []
                replace-existing (conj StandardCopyOption/REPLACE_EXISTING)
                copy-attributes  (conj StandardCopyOption/COPY_ATTRIBUTES)
                atomic-move      (conj StandardCopyOption/ATOMIC_MOVE)
                nofollow-links   (conj LinkOption/NOFOLLOW_LINKS))))

(defn copy
  "Copies src file to dest file.
  Options:
  - :replace-existing
  - :copy-attributes
  - :nofollow-links."
  ([src dest] (copy src dest nil))
  ([src dest {:keys [:replace-existing
                     :copy-attributes
                     :nofollow-links]}]
   (let [copy-options (->copy-opts replace-existing copy-attributes false nofollow-links)]
     (Files/copy (as-path src) (as-path dest)
                 copy-options))))

(defn copy-tree
  "Copies entire file tree. Supports same options as copy."
  ([src dest] (copy-tree src dest nil))
  ([src dest {:keys [:replace-existing
                     :copy-attributes
                     :nofollow-links]}]
   (let [copy-options (->copy-opts replace-existing copy-attributes false nofollow-links)
         link-options (->link-opts nofollow-links)
         from (real-path src {:nofollow-links nofollow-links})
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
                                           :continue))}))))

(defn posix->str
  "Converts a set of PosixFilePermission to a string."
  [p]
  (PosixFilePermissions/toString p))

(defn str->posix
  "Converts a string to a set of PosixFilePermission."
  [s]
  (PosixFilePermissions/fromString s))

(defn- ->posix-file-permissions [s]
  (cond (string? s)
        (str->posix s)
        ;; (set? s)
        ;; (into #{} (map keyword->posix-file-permission) s)
        :else
        s))

(defn- posix->file-attribute [x]
  (PosixFilePermissions/asFileAttribute x))

(defn- posix->attrs
  ^"[Ljava.nio.file.attribute.FileAttribute;" [posix-file-permissions]
  (let [attrs (if posix-file-permissions
                (-> posix-file-permissions
                    (->posix-file-permissions)
                    (posix->file-attribute)
                    vector)
                [])]
     (into-array FileAttribute attrs)))

(defn temp-dir
  "Returns java.io.tmpdir property as path."
  []
  (as-path (System/getProperty "java.io.tmpdir")))

(defn create-temp-dir
  "Creates a temporary directory using Files#createDirectories.

  (create-temp-dir): creates temp dir with random prefix.
  (create-temp-dir {:keys [:prefix :path :posix-file-permissions]}):

  create temp dir in path with prefix. If prefix is not provided, a random one
  is generated. If path is not provided, the directory is created as if called with (create-temp-dir). The :posix-file-permissions option is a string like \"rwx------\"."
  ([]
   (Files/createTempDirectory
    (str (java.util.UUID/randomUUID))
    (make-array FileAttribute 0)))
  ([{:keys [:prefix :path :posix-file-permissions]}]
   (let [attrs (posix->attrs posix-file-permissions)
         prefix (or prefix (str (java.util.UUID/randomUUID)))]
     (if path
       (Files/createTempDirectory
        (as-path path)
        prefix
        attrs)
       (Files/createTempDirectory
        prefix
        attrs)))))

(defn create-sym-link
  "Create a soft link from path to target."
  [path target]
  (Files/createSymbolicLink
   (as-path path)
   (as-path target)
   (make-array FileAttribute 0)))

(defn delete
  "Deletes f. Returns nil if the delete was successful,
  throws otherwise. Does not follow symlinks."
  ;; We don't follow symlinks, since the link can target a dir and you should be
  ;; using delete-tree to delete that.
  [dir]
  (Files/delete (as-path dir)))

(defn delete-if-exists
  "Deletes f if it exists. Returns true if the delete was successful,
  false if f didn't exist. Does not follow symlinks."
  [f]
  (Files/deleteIfExists (as-path f)))

(defn sym-link? [f]
  (Files/isSymbolicLink (as-path f)))

(defn delete-tree
  "Deletes a file tree using walk-file-tree. Similar to rm -rf. Does not follow symlinks."
  [root]
  (walk-file-tree root
                  {:visit-file (fn [path _]
                                 (delete path)
                                 :continue)
                   :post-visit-dir (fn [path _]
                                     (delete path)
                                     :continue)}))

(defn create-dir
  "Creates dir using Files#createDirectory. Does not create parents."
  ([path]
   (create-dir path nil))
  ([path {:keys [:posix-file-permissions]}]
   (let [attrs (posix->attrs posix-file-permissions)]
     (Files/createDirectory (as-path path) attrs))))

(defn create-dirs
  "Creates directories using Files#createDirectories. Also creates parents if needed."
  ([path] (create-dirs path nil))
  ([path {:keys [:posix-file-permissions]}]
   (Files/createDirectories (as-path path) (posix->attrs posix-file-permissions))))

(defn create-file
  "Creates empty file using Files#createFile."
  ([path]
   (create-file path nil))
  ([path {:keys [:posix-file-permissions]}]
   (let [attrs (posix->attrs posix-file-permissions)]
     (Files/createFile (as-path path) attrs))))

(defn move
  "Move or rename a file to a target file via Files/move."
  ([source target] (move source target nil))
  ([source target {:keys [:replace-existing
                          :atomic-move
                          :nofollow-links]}]
   (Files/move (as-path source)
               (as-path target)
               (->copy-opts replace-existing false atomic-move nofollow-links))))

(defn parent
  "Returns parent of f, is it exists."
  [f]
  (.getParent (as-path f)))

(defn size
  [f]
  (Files/size (as-path f)))

(defn delete-on-exit
  "Requests delete on exit via File#deleteOnExit. Returns f."
  [f]
  (.deleteOnExit (as-file f))
  f)

(defn set-posix-file-permissions
  "Sets posix file permissions on f. Accepts a string like \"rwx------\" or a set of PosixFilePermission."
  [f posix-file-permissions]
  (Files/setPosixFilePermissions (as-path f) (->posix-file-permissions posix-file-permissions)))

(defn posix-file-permissions
  "Gets f's posix file permissions. Use posix->str to view as a string."
  ([f] (posix-file-permissions f nil))
  ([f {:keys [:nofollow-links]}]
   (Files/getPosixFilePermissions (as-path f) (->link-opts nofollow-links))))

(defn same-file?
  "Returns true if this is the same file as other."
  [this other]
  (Files/isSameFile (as-path this) (as-path other)))

(defn read-all-bytes
  "Returns contents of file as byte array."
  [f]
  (Files/readAllBytes (as-path f)))

(defn read-all-lines
  [f]
  (vec (Files/readAllLines (as-path f))))

;;;; Attributes, from github.com/nate/fs

(defn get-attribute
  ([path attribute]
   (get-attribute path attribute nil))
  ([path attribute {:keys [:nofollow-links]}]
   (Files/getAttribute (as-path path)
                       attribute
                       (->link-opts {:nofollow-links nofollow-links}))))

(defn- keyize
  [key-fn m]
  (let [f (fn [[k v]] (if (string? k) [(key-fn k) v] [k v]))]
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn read-attributes
  "Reads attributes via Files/readAttributes. Keywordizes string keys into keywords. This can be changed by passing a :key-fn in the options map."
  ([path attributes]
   (read-attributes path attributes nil))
  ([path ^String attributes {:keys [:nofollow-links :key-fn]}]
   (->>  (Files/readAttributes (as-path path)
                               attributes
                               (->link-opts {:nofollow-links nofollow-links}))
         (into {})
         (keyize (or key-fn keyword)))))

(defn set-attribute
  ([path attribute value]
   (set-attribute path attribute value nil))
  ([path attribute value {:keys [:nofollow-links]}]
   (Files/setAttribute (as-path path)
                       attribute
                       value
                       (->link-opts {:nofollow-links nofollow-links}))))

(defn file-time->instant
  "Converts a java.nio.file.attribute.FileTime to a java.time.Instant."
  [^FileTime ft]
  (.toInstant ft))

(defn instant->file-time
  "Converts a java.time.Instant to a java.nio.file.attribute.FileTime."
  [instant]
  (FileTime/from instant))

(defn file-time->millis
  "Converts a java.nio.file.attribute.FileTime to epoch millis (long)."
  [^FileTime ft]
  (.toMillis ft))

(defn millis->file-time
  "Converts epoch millis (long) to a java.nio.file.attribute.FileTime."
  [millis]
  (FileTime/fromMillis millis))

(defn- ->file-time [x]
  (cond (int? x) (millis->file-time x)
        (instance? java.time.Instant x) (instant->file-time x)
        :else x))

(defn last-modified-time
  "Returns last modified time as a java.nio.file.attribute.FileTime."
  ([f]
   (last-modified-time f nil))
  ([f {:keys [nofollow-links] :as opts}]
   (get-attribute f "basic:lastModifiedTime" opts)))

(defn set-last-modified-time
  "Sets last modified time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime)."
  ([f time]
   (set-last-modified-time f time nil))
  ([f time {:keys [nofollow-links] :as opts}]
   (set-attribute f "basic:lastModifiedTime" (->file-time time) opts)))

(defn creation-time
  "Returns creation time as FileTime."
  ([f]
   (creation-time f nil))
  ([f {:keys [nofollow-links] :as opts}]
   (get-attribute f "basic:creationTime" opts)))

(defn set-creation-time
  "Sets creation time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime)."
  ([f time]
   (set-creation-time f time nil))
  ([f time {:keys [nofollow-links] :as opts}]
   (set-attribute f "basic:creationTime" (->file-time time) opts)))

(defn list-dirs
  "Similar to list-dir but accepts multiple roots and returns the concatenated results."
  [dirs glob-or-accept]
  (mapcat #(list-dir % glob-or-accept) dirs))

(defn split-paths
  "Splits a string joined by the OS-specific path-seperator into a vec of paths."
  [^String joined-paths]
  (mapv path (.split joined-paths path-separator)))

(defn exec-paths
  "Returns executable paths (using the PATH environment variable). Same
  as (split-paths (System/getenv \"PATH\"))."
  []
  (split-paths (System/getenv "PATH")))

(defn which
  "Locates a program in (exec-paths) similar to the which Unix command.
  When using the :all option, a vec of results instead of a single result is returned."
  ([program] (which program nil))
  ([program {:keys [:all]}]
   (loop [paths (babashka.fs/exec-paths)
          results []]
     (if-let [p (first paths)]
       (let [f (babashka.fs/path p program)]
         (if (executable? f)
           (if all
             (recur (rest paths) (conj results f))
             f)
           (recur (rest paths) results)))
       (if all results (first results))))))

;; the above can be implemented using:

;; user=> (first (filter fs/executable? (fs/list-dirs (filter fs/exists? (fs/exec-path)) "java")))
;; #object[sun.nio.fs.UnixPath 0x1dd74143 "/Users/borkdude/.jenv/versions/11.0/bin/java"]
;; although the which impl is faster

(defn starts-with?
  "Returns true if path this starts with path other."
  [this other]
  (.startsWith (as-path this) (as-path other)))

(defn ends-with?
  "Returns true if path this ends with path other."
  [this other]
  (.endsWith (as-path this) (as-path other)))
