(ns babashka.fs
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :as walk])
  (:import [java.io File]
           [java.net URI]
           [java.nio.file StandardOpenOption CopyOption
            #?@(:bb [] :clj [DirectoryStream]) #?@(:bb [] :clj [DirectoryStream$Filter])
            Files
            FileSystems
            FileVisitOption
            FileVisitResult
            StandardCopyOption
            LinkOption Path
            FileVisitor]
           [java.nio.file.attribute BasicFileAttributes FileAttribute FileTime PosixFilePermissions]
           [java.nio.charset Charset]
           [java.util.zip GZIPInputStream GZIPOutputStream ZipInputStream ZipOutputStream ZipEntry]
           [java.io File BufferedInputStream FileInputStream FileOutputStream]))

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

(defn- as-path
  ^Path [path]
  (if (instance? Path path) path
      (if (instance? URI path)
        (java.nio.file.Paths/get ^URI path)
        (.toPath (io/file path)))))

(defn- as-file
  "Coerces a path into a file if it isn't already one."
  ^java.io.File [path]
  (if (instance? Path path) (.toFile ^Path path)
      (io/file path)))

(defn- get-env [k]
  (System/getenv k))

(defn path
  "Coerces f into a Path. Multiple-arg versions treat the first argument as
  parent and subsequent args as children relative to the parent."
  (^Path [f]
   (as-path f))
  (^Path [parent child]
   (as-path (io/file (as-file parent) (as-file child))))
  (^Path [parent child & more]
   (reduce path (path parent child) more)))

(defn file
  "Coerces f into a File. Multiple-arg versions treat the first argument
  as parent and subsequent args as children relative to the parent."
  (^File [f] (as-file f))
  (^File [f & fs]
   (apply io/file (map as-file (cons f fs)))))

(defn- ->link-opts ^"[Ljava.nio.file.LinkOption;"
  [nofollow-links]
  (into-array LinkOption
              (cond-> []
                nofollow-links
                (conj LinkOption/NOFOLLOW_LINKS))))

(defn real-path
  "Converts f into real path via Path#toRealPath."
  (^Path [f] (real-path f nil))
  (^Path [f {:keys [:nofollow-links]}]
   (.toRealPath (as-path f) (->link-opts nofollow-links))))

(defn owner
  "Returns the owner of a file. Call `str` on it to get the owner name
  as a string."
  ([f] (owner f nil))
  ([f {:keys [:nofollow-links]}]
   (Files/getOwner (as-path f) (->link-opts nofollow-links))))

;;;; Predicates

(defn regular-file?
  "Returns true if f is a regular file, using Files/isRegularFile."
  ([f] (regular-file? f nil))
  ([f {:keys [:nofollow-links]}]
   (Files/isRegularFile (as-path f)
                        (->link-opts nofollow-links))))

(defn directory?
  "Returns true if f is a directory, using Files/isDirectory."
  ([f] (directory? f nil))
  ([f {:keys [:nofollow-links]}]
   (Files/isDirectory (as-path f)
                      (->link-opts nofollow-links))))

(def ^:private simple-link-opts
  (into-array LinkOption []))

(defn- directory-simple?
  [^Path f] (Files/isDirectory f simple-link-opts))

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
  "Returns true if f is writable"
  [f] (Files/isWritable (as-path f)))

(defn relative?
  "Returns true if f represents a relative path."
  [f] (not (absolute? f)))

(defn exists?
  "Returns true if f exists."
  ([f] (exists? f nil))
  ([f {:keys [:nofollow-links]}]
   (try
     (Files/exists
      (as-path f)
      (->link-opts nofollow-links))
     (catch Exception _e
       false))))

;;;; End predicates

(defn components
  "Returns a seq of all components of f as paths, i.e. split on the file
  separator."
  [f]
  (seq (as-path f)))

(defn absolutize
  "Converts f into an absolute path via Path#toAbsolutePath."
  [f] (.toAbsolutePath (as-path f)))

(defn relativize
  "Returns relative path by comparing this with other."
  ^Path [this other]
  (.relativize (as-path this) (as-path other)))

(defn normalize
  "Normalizes f via Path#normalize."
  [f]
  (.normalize (as-path f)))

(defn canonicalize
  "Returns the canonical path via
  java.io.File#getCanonicalPath. If `:nofollow-links` is set, then it
  will fall back on `absolutize` + `normalize.` This function can be used
  as an alternative to `real-path` which requires files to exist."
  (^Path [f] (canonicalize f nil))
  (^Path [f {:keys [:nofollow-links]}]
   (if nofollow-links
     (-> f absolutize normalize)
     (as-path (.getCanonicalPath (as-file f))))))

(defn file-name
  "Returns the name of the file or directory. E.g. (file-name \"foo/bar/baz\") returns \"baz\"."
  [x]
  (.getName (as-file x)))

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
                              (fn [_path _attrs]
                                :continue))]
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
                          (visitFileFailed [_ path ex]
                            (-> (visit-file-failed path ex)
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
     "Returns all paths in dir as vector. For descending into subdirectories use `glob.`
     - `glob-or-accept` - a glob string such as \"*.edn\" or a (fn accept [^java.nio.file.Path p]) -> truthy"
     ([dir]
      (with-open [stream (directory-stream dir)]
        (vec stream)))
     ([dir glob-or-accept]
      (with-open [stream (directory-stream dir glob-or-accept)]
        (vec stream)))))

(def file-separator File/separator)
(def path-separator File/pathSeparator)

(def ^:private win?
  (-> (System/getProperty "os.name")
      (str/lower-case)
      (str/includes? "win")))

(defn match
  "Given a file and match pattern, returns matches as vector of
  paths. Pattern interpretation is done using the rules described in
  https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String).

  Options:

  * `:hidden` - match hidden paths - note: on Windows paths starting with
  a dot are not hidden, unless their hidden attribute is set.
  * `:follow-links` - follow symlinks.
  * `:recursive` - match recursively.
  * `:max-depth` - max depth to descend into directory structure.

  Examples:
  `(fs/match \".\" \"regex:.*\\\\.clj\" {:recursive true})`"
  ([root pattern] (match root pattern nil))
  ([root pattern {:keys [hidden follow-links max-depth recursive]}]
   (let [base-path (-> root absolutize normalize)
         base-path (if win?
                     (str/replace base-path file-separator (str "\\" file-separator))
                     (str base-path))
         skip-hidden? (not hidden)
         results (atom (transient []))
         past-root? (volatile! nil)
         [prefix pattern] (str/split pattern #":")
         pattern (let [separator (when-not (str/ends-with? base-path file-separator)
                                   ;; we need to escape the file separator on Windows
                                   (str (when win? "\\")
                                        file-separator))]
                   (str base-path
                        separator
                        (if win?
                          (str/replace pattern "/" "\\\\")
                          pattern)))
         pattern (str prefix ":" pattern)
         matcher (.getPathMatcher
                  (FileSystems/getDefault)
                  pattern)
         match (fn [^Path path]
                 (when (.matches matcher path)
                   (swap! results conj! path))
                 nil)]
     (walk-file-tree
      base-path
      {:max-depth max-depth
       :follow-links follow-links
       :pre-visit-dir (fn [dir _attrs]
                        (if (and @past-root?
                                 (or (not recursive)
                                     (and skip-hidden?
                                          (hidden? dir))))
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
     (let [results (persistent! @results)
           absolute-cwd (absolutize "")]
       (if (relative? root)
         (mapv #(relativize absolute-cwd %)
               results)
         results)))))

(defn glob
  "Given a file and glob pattern, returns matches as vector of
  paths. Patterns containing `**` or `/` will cause a recursive walk over
  path, unless overriden with :recursive. Similarly: :hidden will be enabled (when not set)
  when `pattern` starts with a dot.
  Glob interpretation is done using the rules described in
  https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String).

  Options:

  * `:hidden` - match hidden paths. Implied when `pattern` starts with a dot. Note: on Windows files starting with a dot are not hidden, unless their hidden attribute is set.
  * `:follow-links` - follow symlinks.
  * `:recursive` - force recursive search. Implied when `pattern` contains `**` or `/`.

  Examples:
  `(fs/glob \".\" \"**.clj\")`"
  ([root pattern] (glob root pattern nil))
  ([root pattern opts]
   (let [recursive (:recursive opts
                               (or (str/includes? pattern "**")
                                   (str/includes? pattern file-separator)
                                   (when win?
                                     (str/includes? pattern "/"))))
         hidden    (:hidden opts (str/starts-with? pattern "."))]
     (match root (str "glob:" pattern) (assoc opts :recursive recursive :hidden hidden)))))

(defn- ->copy-opts ^"[Ljava.nio.file.CopyOption;"
  [replace-existing copy-attributes atomic-move nofollow-links]
  (into-array CopyOption
              (cond-> []
                replace-existing (conj StandardCopyOption/REPLACE_EXISTING)
                copy-attributes  (conj StandardCopyOption/COPY_ATTRIBUTES)
                atomic-move      (conj StandardCopyOption/ATOMIC_MOVE)
                nofollow-links   (conj LinkOption/NOFOLLOW_LINKS))))

(defn copy
  "Copies src file to dest dir or file.
  Options:
  * `:replace-existing`
  * `:copy-attributes`
  * `:nofollow-links` (used to determine to copy symbolic link itself or not)."
  ([src dest] (copy src dest nil))
  ([src dest {:keys [replace-existing
                     copy-attributes
                     nofollow-links]}]
   (let [copy-options (->copy-opts replace-existing copy-attributes false nofollow-links)
         dest (as-path dest)
         dest (if (directory-simple? dest)
                (path dest (file-name src))
                dest)
         input-stream? (instance? java.io.InputStream src)]
     (if input-stream?
       (Files/copy ^java.io.InputStream src dest copy-options)
       (Files/copy (as-path src) dest copy-options)))))

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

(defn create-dir
  "Creates dir using `Files#createDirectory`. Does not create parents."
  ([path]
   (create-dir path nil))
  ([path {:keys [:posix-file-permissions]}]
   (let [attrs (posix->attrs posix-file-permissions)]
     (Files/createDirectory (as-path path) attrs))))

(defn create-dirs
  "Creates directories using `Files#createDirectories`. Also creates parents if needed.
  Doesn't throw an exception if the dirs exist already. Similar to `mkdir -p`"
  ([path] (create-dirs path nil))
  ([path {:keys [:posix-file-permissions]}]
   (Files/createDirectories (as-path path) (posix->attrs posix-file-permissions))))

(defn copy-tree
  "Copies entire file tree from src to dest. Creates dest if needed
  using `create-dirs`, passing it the `:posix-file-permissions`
  option. Supports same options as copy."
  ([src dest] (copy-tree src dest nil))
  ([src dest {:keys [:replace-existing
                     :copy-attributes
                     :nofollow-links]
              :as opts}]
   ;; cf. Python
   (when-not (directory? src)
     (throw (IllegalArgumentException. (str "Not a directory: " src))))
   ;; cf. Python
   (when (and (exists? dest)
              (not (directory? dest)))
     (throw (IllegalArgumentException. (str "Not a directory: " dest))))
   ;; cf. Python
   (create-dirs dest opts)
   (let [copy-options (->copy-opts replace-existing copy-attributes false nofollow-links)
         link-options (->link-opts nofollow-links)
         from (real-path src {:nofollow-links nofollow-links})
         ;; using canonicalize here because real-path requires the path to exist
         to (canonicalize dest {:nofollow-links nofollow-links})]
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
                                           :continue)
                                         :continue)}))))

(defn temp-dir
  "Returns `java.io.tmpdir` property as path."
  []
  (as-path (System/getProperty "java.io.tmpdir")))

(defn create-temp-dir
  "Creates a temporary directory using Files#createDirectories.

  `(create-temp-dir)`: creates temp dir with random prefix.
  `(create-temp-dir {:keys [:prefix :dir :posix-file-permissions]})`:

  create temp dir in dir path with prefix. If prefix is not provided, a random one
  is generated. If path is not provided, the directory is created as if called with `(create-temp-dir)`. The `:posix-file-permissions` option is a string like `\"rwx------\"`."
  ([]
   (Files/createTempDirectory
    (str (java.util.UUID/randomUUID))
    (make-array FileAttribute 0)))
  ([{:keys [:prefix :dir :posix-file-permissions] :as opts}]
   (let [attrs (posix->attrs posix-file-permissions)
         prefix (or prefix (str (java.util.UUID/randomUUID)))
         dir (or dir (:path opts))]
     (if dir
       (Files/createTempDirectory
        (as-path dir)
        prefix
        attrs)
       (Files/createTempDirectory
        prefix
        attrs)))))

(defn create-temp-file
  "Creates an empty temporary file using Files#createTempFile.

  - `(create-temp-file)`: creates temp file with random prefix and suffix.
  - `(create-temp-dir {:keys [:prefix :suffix :path :posix-file-permissions]})`: create
  temp file in path with prefix. If prefix and suffix are not
  provided, random ones are generated. The `:posix-file-permissions`
  option is a string like `\"rwx------\"`."
  ([]
   (Files/createTempFile
    (str (java.util.UUID/randomUUID))
    (str (java.util.UUID/randomUUID))
    (make-array FileAttribute 0)))
  ([{:keys [:dir :prefix :suffix :posix-file-permissions] :as opts}]
   (let [attrs (posix->attrs posix-file-permissions)
         prefix (or prefix (str (java.util.UUID/randomUUID)))
         suffix (or suffix (str (java.util.UUID/randomUUID)))
         dir (or dir
                 ;; backwards compat
                 (:path opts))]
     (if dir
       (Files/createTempFile
        (as-path dir)
        prefix
        suffix
        attrs)
       (Files/createTempFile
        prefix
        suffix
        attrs)))))

(defn create-sym-link
  "Create a soft link from path to target."
  [path target]
  (Files/createSymbolicLink
   (as-path path)
   (as-path target)
   (make-array FileAttribute 0)))

(defn create-link
  "Create a hard link from path to target."
  [path target]
  (Files/createLink
   (as-path path)
   (as-path target)))

(defn delete
  "Deletes f. Returns nil if the delete was successful,
  throws otherwise. Does not follow symlinks."
  ;; We don't follow symlinks, since the link can target a dir and you should be
  ;; using delete-tree to delete that.
  [f]
  (Files/delete (as-path f)))

(defn delete-if-exists
  "Deletes f if it exists. Returns true if the delete was successful,
  false if f didn't exist. Does not follow symlinks."
  [f]
  (Files/deleteIfExists (as-path f)))

(defn sym-link?
  "Determines if `f` is a symbolic link via `java.nio.file.Files/isSymbolicLink`."
  [f]
  (Files/isSymbolicLink (as-path f)))

(defn delete-tree
  "Deletes a file tree using `walk-file-tree`. Similar to `rm -rf`. Does not follow symlinks."
  [root]
  (when (exists? root)
    (walk-file-tree root
                    {:visit-file (fn [path _]
                                   (delete path)
                                   :continue)
                     :post-visit-dir (fn [path _]
                                       (delete path)
                                       :continue)})))

(defn create-file
  "Creates empty file using `Files#createFile`."
  ([path]
   (create-file path nil))
  ([path {:keys [:posix-file-permissions]}]
   (let [attrs (posix->attrs posix-file-permissions)]
     (Files/createFile (as-path path) attrs))))

(defn move
  "Move or rename a file to a target dir or file via `Files/move`."
  ([source target] (move source target nil))
  ([source target {:keys [:replace-existing
                          :atomic-move
                          :nofollow-links]}]
   (let [target (as-path target)]
     (if (directory-simple? target)
       (Files/move (as-path source)
                   (path target (file-name source))
                   (->copy-opts replace-existing false atomic-move nofollow-links))
       (Files/move (as-path source)
                   target
                   (->copy-opts replace-existing false atomic-move nofollow-links))))))

(defn parent
  "Returns parent of f. Akin to `dirname` in bash."
  [f]
  (.getParent (as-path f)))

(defn size
  "Returns the size of a file (in bytes)."
  [f]
  (Files/size (as-path f)))

(defn delete-on-exit
  "Requests delete on exit via `File#deleteOnExit`. Returns f."
  [f]
  (.deleteOnExit (as-file f))
  f)

(defn set-posix-file-permissions
  "Sets posix file permissions on f. Accepts a string like `\"rwx------\"` or a set of PosixFilePermission."
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

(defn- ->charset ^Charset [charset]
  (if (string? charset)
    (Charset/forName charset)
    charset))

(defn read-all-lines
  "Read all lines from a file."
  ([f]
   (vec (Files/readAllLines (as-path f))))
  ([f {:keys [charset]
       :or {charset "utf-8"}}]
   (vec (Files/readAllLines
         (as-path f)
         (->charset charset)))))

;;;; Attributes, from github.com/corasaurus-hex/fs

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

(defn read-attributes*
  "Reads attributes via Files/readAttributes."
  ([path attributes]
   (read-attributes* path attributes nil))
  ([path attributes {:keys [:nofollow-links]}]
   (let [p (as-path path)
         link-opts (->link-opts {:nofollow-links nofollow-links})
         attrs
         ;; prevent reflection warning
         (if (instance? String attributes)
           (Files/readAttributes p
                                 ^String attributes
                                 link-opts)
           (Files/readAttributes p
                                 ^Class attributes
                                 link-opts))]
     attrs)))

(defn read-attributes
  "Same as `read-attributes*` but turns attributes into a map and keywordizes keys.
  Keywordizing can be changed by passing a :key-fn in the options map."
  ([path attributes]
   (read-attributes path attributes nil))
  ([path attributes {:keys [:nofollow-links :key-fn] :as opts}]
   (->> (read-attributes* path attributes opts)
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
  "Similar to list-dir but accepts multiple roots and returns the concatenated results.
  - `glob-or-accept` - a glob string such as \"*.edn\" or a (fn accept [^java.nio.file.Path p]) -> truthy"
  [dirs glob-or-accept]
  (mapcat #(list-dir % glob-or-accept) dirs))

(defn split-ext
  "Splits path on extension If provided, a specific extension `ext`, the
  extension (without dot), will be used for splitting.  Directories
  are not processed."
  ([path] (split-ext path nil))
  ([path {:keys [ext]}]
   (let [path-str (str path)
         file-name (file-name path)]
     (let [ext (if ext
                 (str "." ext)
                 (when-let [last-dot (str/last-index-of file-name ".")]
                   (subs file-name last-dot)))]
       (if (and ext
                (str/ends-with? path-str ext)
                (not= path-str ext))
         (let [loc (str/last-index-of path-str ext)]
           [(subs path-str 0 loc)
            (subs path-str (inc loc))])
         [path-str nil])))))

(defn strip-ext
  "Strips extension via `split-ext`."
  ([path]
   (strip-ext path nil))
  ([path {:keys [ext] :as opts}]
   (first (split-ext path opts))))

(defn extension
  "Returns the extension of a file via `split-ext`."
  [path]
  (-> path split-ext last))

(defn split-paths
  "Splits a path list given as a string joined by the OS-specific path-separator into a vec of paths.
  On UNIX systems, the separator is ':', on Microsoft Windows systems it is ';'."
  [^String joined-paths]
  (mapv path (.split joined-paths path-separator)))

(defn exec-paths
  "Returns executable paths (using the PATH environment variable). Same
  as `(split-paths (System/getenv \"PATH\"))`."
  []
  (split-paths (System/getenv "PATH")))

(defn- filename-only?
  "Returns true if `f` is exactly a file name (i.e. with no absolute or
  relative path information."
  [f]
  (let [f-as-path (as-path f)]
    (= f-as-path (.getFileName f-as-path))))

(defn which
  "Returns Path to first executable `program` found in `:paths` `opt`, similar to the which Unix command.
  Default for `:paths` is `(exec-paths)`.

  On Windows, searches for `program` with filename extensions specified in `:win-exts` `opt`.
  Default is `[\"com\" \"exe\" \"bat\" \"cmd\"]`.
  If `program` already includes an extension from `:win-exts`, it will be searched as-is first.

  When `program` is a relative or absolute path, `:paths` is not consulted. On Windows, the `:win-exts`
  variants are still searched. On other OSes, the path for `program` will be returned if executable,
  else nil."
  ([program] (which program nil))
  ([program opts]
   (let [exts (if win?
                (let [exts (or (:win-exts opts)
                               ["com" "exe" "bat" "cmd"])
                      ext (extension program)]
                  (if (and ext (contains? (set exts) ext))
                    ;; this program name already contains the expected extension so we
                    ;; first search with that and then try the others to find e.g. foo.bat.cmd
                    (into [nil] exts)
                    exts))
                [nil])
         paths (or (:paths opts) (babashka.fs/exec-paths))
         ;; if program is exactly a file name, then search all the path entries
         ;; otherwise, only search relative to current directory (absolute paths will throw)
         candidate-paths (if (filename-only? program)
                           paths
                           [nil])]
     (loop [paths candidate-paths
            results []]
       (if (seq paths)
         (let [p (first paths)
               fs (loop [exts exts
                         candidates []]
                    (if (seq exts)
                      (let [ext (first exts)
                            program (str program (when ext (str "." ext)))
                            f (if (babashka.fs/relative? program)
                                (babashka.fs/path p program)
                                (babashka.fs/path program))]
                        (if (and (executable? f) (not (directory? f)))
                          (recur (rest exts)
                                 (conj candidates f))
                          (recur (rest exts)
                                 candidates)))
                      candidates))]
           (if (seq fs)
             (if (:all opts)
               (recur (rest paths) (into results fs))
               (first fs))
             (recur (rest paths) results)))
         (if (:all opts) results (first results)))))))

(defn which-all
  "Returns every Path to `program` found in (`exec-paths`). See `which`."
  ([program] (which-all program nil))
  ([program opts]
   (which program (assoc opts :all true))))

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

;;;; Modified since

(defn- last-modified-1
  "Returns max last-modified of regular file f. Returns 0 if file does not exist."
  [f]
  (if (exists? f)
    (file-time->millis
     (last-modified-time f))
    0))

(defn- last-modified
  "Returns max last-modified of f or of all files within f"
  [f]
  (if (exists? f)
    (if (regular-file? f)
      (last-modified-1 f)
      (apply max 0
             (map last-modified-1
                  (filter regular-file? (file-seq (file f))))))
    0))

(defn- expand-file-set
  [file-set]
  (if (coll? file-set)
    (mapcat expand-file-set file-set)
    (filter regular-file? (file-seq (file file-set)))))

(defn modified-since
  "Returns seq of regular files (non-directories, non-symlinks) from file-set that were modified since the anchor path.
  The anchor path can be a regular file or directory, in which case
  the recursive max last modified time stamp is used as the timestamp
  to compare with.  The file-set may be a regular file, directory or
  collection of files (e.g. returned by glob). Directories are
  searched recursively."
  [anchor file-set]
  (let [lm (last-modified anchor)]
    (map path (filter #(> (last-modified-1 %) lm) (expand-file-set file-set)))))

;;;; End modified since

;;;; Zip

(defn unzip
  "Unzips `zip-file` to `dest` directory (default `\".\"`).

   Options:
   * `:replace-existing` - `true` / `false`: overwrite existing files"
  ([zip-file] (unzip zip-file "."))
  ([zip-file dest] (unzip zip-file dest nil))
  ([zip-file dest {:keys [replace-existing]}]
   (let [output-path (as-path dest)
         zip-file (as-path zip-file)
         _ (create-dirs dest)
         cp-opts (->copy-opts replace-existing nil nil nil)]
     (with-open
      [fis (Files/newInputStream zip-file (into-array java.nio.file.OpenOption []))
       zis (ZipInputStream. fis)]
       (loop []
         (let [entry (.getNextEntry zis)]
           (when entry
             (let [entry-name (.getName entry)
                   new-path (.resolve output-path entry-name)]
               (if (.isDirectory entry)
                 (create-dirs new-path)
                 (do
                   (create-dirs (parent new-path))
                   (Files/copy ^java.io.InputStream zis
                               new-path
                               cp-opts))))
             (recur))))))))

;; partially borrowed from tools.build
(defn- add-zip-entry
  [^ZipOutputStream output-stream ^File file fpath]
  (let [dir (.isDirectory file)
        attrs (Files/readAttributes (as-path file) BasicFileAttributes
                                    (->link-opts []))
        entry (doto (ZipEntry. (str fpath))
                (.setLastModifiedTime (.lastModifiedTime attrs)))]
    (.putNextEntry output-stream entry)
    (when-not dir
      (with-open [fis (BufferedInputStream. (FileInputStream. file))]
        (io/copy fis output-stream)))

    (.closeEntry output-stream)))

;; partially borrowed from tools.build
(defn- copy-to-zip
  [^ZipOutputStream jos f path-fn]
  (let [files (file-seq (file f))]
    (run! (fn [^File f]
            (let [dir (.isDirectory f)
                  fpath (str f)
                  fpath (if (and dir (not (.endsWith fpath "/"))) (str fpath "/") fpath)
                  ;; only use unix-style paths in jars
                  fpath (str/replace fpath \\ \/)
                  fpath (path-fn fpath)]
              (when-not (str/blank? fpath)
                (add-zip-entry jos f fpath))))
          files)))

;; partially borrowed from tools.build
(defn zip
  "Zips entry or entries into zip-file. An entry may be a file or
  directory. Directories are included recursively and their names are
  preserved in the zip file. Currently only accepts relative entries.

  Options:
  * `:root`: directory which will be elided in zip. E.g.: `(fs/zip [\"src\"] {:root \"src\"})`
  * `:path-fn`: a single-arg function from file system path to zip entry path.
  "
  ([zip-file entries]
   (zip zip-file entries nil))
  ([zip-file entries opts]
   (let [entries (if (or (string? entries)
                         (instance? File entries)
                         (instance? Path entries))
                   [entries]
                   entries)
         path-fn (or (:path-fn opts)
                     (when-let [root (:root opts)]
                       #(str/replace % (re-pattern (str "^" (java.util.regex.Pattern/quote root) "/")) ""))
                     identity)]
     (assert (every? relative? entries)
             "All entries must be relative")
     (with-open [zos (ZipOutputStream.
                      (FileOutputStream. (file zip-file)))]
       (doseq [zpath entries]
         (copy-to-zip zos zpath path-fn))))))

;;;; End zip

;;;; GZip

(defn gunzip
  "Extracts `gz-file` to `dest` directory (default `\".\"`).

   Options:
   * `:replace-existing` - `true` / `false`: overwrite existing files"
  ([gz-file] (gunzip gz-file "."))
  ([gz-file dest] (gunzip gz-file dest nil))
  ([gz-file dest {:keys [replace-existing]}]
   (let [output-path (as-path dest)
         dest-filename (str/replace-first gz-file #"\.gz$" "")
         gz-file (as-path gz-file)
         _ (create-dirs dest)
         cp-opts (->copy-opts replace-existing nil nil nil)
         new-path (.resolve output-path dest-filename)]
     (with-open
      [fis (Files/newInputStream gz-file (into-array java.nio.file.OpenOption []))
       gzis (GZIPInputStream. fis)]
       (create-dirs (parent new-path))
       (Files/copy ^java.io.InputStream gzis
                   new-path
                   cp-opts)))))

(defn gzip
  "Gzips `source-file` and writes the output to `dir/out-file`.
  If `out-file` is not provided, the `source-file` name with `.gz` appended is used.
  If `dir` is not provided, the current directory is used.
  Returns the created gzip file."
  ([source-file]
   (gzip source-file {:dir "."}))
  ([source-file {:keys [dir out-file] :or {dir "."}}]
   (assert source-file "source-file must be specified")
   (assert (-> source-file io/file .exists) "source-file does not exist")
   (let [output-path (as-path dir)
         ^String dest-filename (if (not out-file)
                                 (str source-file ".gz")
                                 (str out-file))
         new-path (.resolve output-path dest-filename)]
     (create-dirs (parent new-path))
     (with-open [source-input-stream (io/input-stream source-file)
                 gzos                (GZIPOutputStream.
                                      (FileOutputStream. (file new-path)))]
       (io/copy source-input-stream
                gzos))
     (str new-path))))

;;;; End gzip

(defmacro with-temp-dir
  "Evaluate body with binding-name bound to a temporary directory.

  The directory is created by passing `options` to `create-temp-dir`,
  and will be removed with `delete-tree` on exit from the scope.

  `options` is a map with the keys as for create-temp-dir."
  {:arglists '[[[binding-name options] & body]]}
  [[binding-name options & more] & body]
  {:pre [(empty? more) (symbol? binding-name)]}
  `(let [~binding-name (create-temp-dir ~options)]
     (try
       ~@body
       (finally
         (delete-tree ~binding-name)))))

(def ^:private cached-home-dir
  (delay (path (System/getProperty "user.home"))))

(def ^:private cached-users-dir
  (delay (parent @cached-home-dir)))

(defn home
  "With no arguments, returns the current value of the `user.home`
  system property. If a `user` is passed, returns that user's home
  directory as found in the parent of home with no args."
  (^Path [] @cached-home-dir)
  (^Path [user] (if (empty? user) @cached-home-dir
                    (path @cached-users-dir user))))

(defn expand-home
  "If `path` begins with a tilde (`~`), expand the tilde to the value
  of the `user.home` system property. If the `path` begins with a
  tilde immediately followed by some characters, they are assumed to
  be a username. This is expanded to the path to that user's home
  directory. This is (naively) assumed to be a directory with the same
  name as the user relative to the parent of the current value of
  `user.home`."
  ^Path [f]
  (let [p (as-path f)
        path-str (str p)]
    (if (.startsWith path-str "~")
      (let [sep (.indexOf path-str File/separator)]
        (if (neg? sep)
          (home (subs path-str 1))
          (path (home (subs path-str 1 sep)) (subs path-str (inc sep)))))
      p)))

(defn windows?
  "Returns true if OS is Windows."
  []
  win?)

(defn cwd
  "Returns current working directory as path"
  []
  (as-path (System/getProperty "user.dir")))

(defn- ->open-option [k]
  (case k
    :append
    StandardOpenOption/APPEND
    :create
    StandardOpenOption/CREATE
    :truncate-existing
    StandardOpenOption/TRUNCATE_EXISTING
    :write
    StandardOpenOption/WRITE
    ;; default
    k))

(defn- ->open-options ^"[Ljava.nio.file.OpenOption;"
  [opts]
  (into-array java.nio.file.OpenOption
              (reduce-kv (fn [acc k v]
                           (if v
                             (conj acc (->open-option k))
                             acc)) [] opts)))

(defn write-bytes
  "Writes `bytes` to `path` via `java.nio.file.Files/write`.
  Supported options:
  * `:create` (default `true`)
  * `:truncate-existing` (default `true`)
  * `:write` (default `true`)
  * `:append` (default `false`)
  * or any `java.nio.file.StandardOption`.

  Examples:

  ``` clojure
  (fs/write-bytes f (.getBytes (String. \"foo\"))) ;; overwrites + truncates or creates new file
  (fs/write-bytes f (.getBytes (String. \"foo\")) {:append true})
  ```"
  ([path bytes] (write-bytes path bytes nil))
  ([path bytes {:keys [append
                       ;; default when no options are given:
                       create
                       truncate-existing
                       write] :as opts}]
   (let [path (as-path path)
         opts (->open-options opts)]
     (java.nio.file.Files/write path ^bytes bytes opts))))

(defn write-lines
  "Writes `lines`, a seqable of strings to `path` via `java.nio.file.Files/write`.

  Supported options:
  * `:charset` (default `\"utf-8\"`)

  Supported open options:
  * `:create` (default `true`)
  * `:truncate-existing` (default `true`)
  * `:write` (default `true`)
  * `:append` (default `false`)
  * or any `java.nio.file.StandardOption`."
  ([path lines] (write-lines path lines nil))
  ([path lines {:keys [charset]
                :or {charset "utf-8"}
                :as opts}]
   (java.nio.file.Files/write (as-path path)
                              lines
                              (->charset charset)
                              (->open-options (dissoc opts :charset)))))

(defn update-file
  "Updates the contents of text file `path` using `f` applied to old contents and `xs`.
  Returns the new contents.

  Options:

  * `:charset` - charset of file, default to \"utf-8\""
  {:arglists '([file f & xs] [file opts f & xs])}
  ([file f & xs]
   (let [[opts f xs] (if (map? f)
                       [f (first xs) (rest xs)]
                       [nil f xs])
         {:keys [charset]
          :or {charset "utf-8"}} opts
         opts [:encoding charset]
         old-val (apply slurp file opts)
         new-val (apply f old-val xs)]
     (apply spit file new-val opts)
     new-val)))

(defn unixify
  "Returns path as string with Unix-style file separators (`/`)."
  [f]
  (if win?
    (-> f as-path .toUri .getPath)
    (str f)))

(defn- xdg-path-from-env-var
  "Yields value of environment variable `k` as path if it's an absolute
  path. Else `nil`."
  [k]
  (some-> (get-env k)
          (#(when (absolute? %) %))
          (path)))

(def ^:private xdg-type->env-var&default-path
  (delay {:config ["XDG_CONFIG_HOME" (path (home) ".config")]
          :cache  ["XDG_CACHE_HOME" (path (home) ".cache")]
          :data   ["XDG_DATA_HOME" (path (home) ".local" "share")]
          :state  ["XDG_STATE_HOME" (path (home) ".local" "state")]}))

(defn- xdg-home-for [k]
  (let [[env-var default-path] (@xdg-type->env-var&default-path k)]
    (or
     (xdg-path-from-env-var env-var)
     default-path)))

(defn xdg-config-home
  "Path representing the base directory relative to which user-specific configuration files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_CONFIG_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) \".config\")`.
  When provided, appends `app` to the path."
  ([] (xdg-config-home nil))
  ([app]
   (cond-> (xdg-home-for :config)
     (seq app) (path app))))

(defn xdg-cache-home
  "Path representing the base directory relative to which user-specific non-essential data files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_CACHE_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) \".cache\")`.
  When provided, appends `app` to the path."
  ([] (xdg-cache-home nil))
  ([app]
   (cond-> (xdg-home-for :cache)
     (seq app) (path app))))

(defn xdg-data-home
  "Path representing the base directory relative to which user-specific data files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_DATA_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) \".local\" \"share\")`.
  When provided, appends `app` to the path."
  ([] (xdg-data-home nil))
  ([app]
   (cond-> (xdg-home-for :data)
     (seq app) (path app))))

(defn xdg-state-home
  "Path representing the base directory relative to which user-specific state files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_STATE_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) \".local\" \"state\")`.
  When provided, appends `app` to the path."
  ([] (xdg-state-home nil))
  ([app]
   (cond-> (xdg-home-for :state)
     (seq app) (path app))))

(defn find-up
  "Starting in `start` and traversing up, checks if the folder contains `file`.

  - `start` (default `(fs/cwd)`) - a string, file or path such as \".\", (fs/file \"README.md\").
  - `file` - a string, file or path such as \"README.md\", \".\", (fs/path \"fs\").

  Yields the path of the file found, else `nil`.

  Examples:

  ``` clojure
  (fs/find-up \"README.md\") ;; search for README.md starting from CWD.

  ;; find .gitignore starting from the parent folder
  (fs/find-up \"..\" \".gitignore\")

  ;; `start` may point to a file
  (fs/find-up (fs/path (fs/home) \".gitconfig\") \".gitignore\")
  ```
  "
  ([file] (find-up (cwd) file))
  ([start file]
   (letfn [(above-root? [p]
             ;; false for e.g. /../path, /some/../../path
             ;; true for e.g.  /some/../path, /some/path/../..
             (boolean (reduce (fn [acc fragment]
                                (if (= (str fragment) "..")
                                  (if (zero? acc) (reduced false) (dec acc))
                                  (inc acc))) 0 (seq (absolutize p)))))
           (ancestors [p]
             (->> p
                  normalize
                  (iterate parent)
                  (take-while some?)))]
     (let [start        (absolutize start)
           start-folder (if (directory? start) start (parent start))]
       (when (above-root? (path start-folder file))
         (first (keep #(when (exists? (path % file))
                         (path % file)) (ancestors start-folder))))))))
