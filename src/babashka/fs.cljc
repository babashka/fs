(ns babashka.fs
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :as walk])
  (:import [java.io File InputStream]
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
           [java.nio.file.attribute BasicFileAttributes FileAttribute FileTime PosixFilePermissions PosixFilePermission]
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
  "Coerces arg(s) into a `Path`, combining multiple paths into one.
  Multiple-arg versions treat the first argument as parent and subsequent
  args as children relative to the parent."
  (^Path [f]
   (as-path f))
  (^Path [parent child]
   (if parent
     (if (string? child)
       (.resolve ^Path (as-path parent) ^String child)
       (.resolve ^Path (as-path parent) (as-path child)))
     (as-path child)))
  (^Path [parent child & more]
   (reduce path (path parent child) more)))

(defn file
  "Coerces arg(s) into a `File`, combining multiple paths into one.
  Multiple-arg versions treat the first argument as parent and subsequent args
  as children relative to the parent."
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
  "Converts `f` into real `Path` via [Path#toRealPath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#toRealPath(java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
  (^Path [f] (real-path f nil))
  (^Path [f {:keys [:nofollow-links]}]
   (.toRealPath (as-path f) (->link-opts nofollow-links))))

(defn owner
  "Returns the owner of file `f` via [Files/getOwner](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getOwner(java.nio.file.Path,java.nio.file.LinkOption...)).
  Call `str` on return value to get the owner name as a string.

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
  ([f] (owner f nil))
  ([f {:keys [:nofollow-links]}]
   (Files/getOwner (as-path f) (->link-opts nofollow-links))))

;;;; Predicates

(defn regular-file?
  "Returns true if `f` is a regular file, using [Files/isRegularFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isRegularFile(java.nio.file.Path,java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
  ([f] (regular-file? f nil))
  ([f {:keys [:nofollow-links]}]
   (Files/isRegularFile (as-path f)
                        (->link-opts nofollow-links))))

(defn directory?
  "Returns true if `f` is a directory, using [Files/isDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isDirectory(java.nio.file.Path,java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
  ([f] (directory? f nil))
  ([f {:keys [:nofollow-links]}]
   (Files/isDirectory (as-path f)
                      (->link-opts nofollow-links))))

(def ^:private simple-link-opts
  (into-array LinkOption []))

(defn- directory-simple?
  [^Path f] (Files/isDirectory f simple-link-opts))

(defn hidden?
  "Returns true if `f` is hidden via [Files/isHidden](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isHidden(java.nio.file.Path)).

  TIP: some older JDKs can throw on empty-string path `(hidden \"\")`.
  Consider instead checking cwd via `(hidden \".\")`."
  [f] (Files/isHidden (as-path f)))

(defn absolute?
  "Returns true if `f` represents an absolute path via [Path#isAbsolute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#isAbsolute())."
  [f] (.isAbsolute (as-path f)))

(defn executable?
  "Returns true if `f` has is executable via [Files/isExecutable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isExecutable(java.nio.file.Path))."
  [f] (Files/isExecutable (as-path f)))

(defn readable?
  "Returns true if `f` is readable via [Files/isReadable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isReadable(java.nio.file.Path))"
  [f] (Files/isReadable (as-path f)))

(defn writable?
  "Returns true if `f` is writable via [Files/isWritable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isWritable(java.nio.file.Path))"
  [f] (Files/isWritable (as-path f)))

(defn relative?
  "Returns true if `f` represents a relative path (in other words, is not [[absolute?]])."
  [f] (not (absolute? f)))

(defn exists?
  "Returns true if `f` exists via [Files/exists](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#exists(java.nio.file.Path,java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
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
  "Returns a seq of all components of `f` as paths.
  i.e.: split on the [[file-separator]]."
  [f]
  (seq (as-path f)))

(defn absolutize
  "Converts `f` into an absolute `Path` via [Path#toAbsolutePath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#toAbsolutePath())."
  [f] (.toAbsolutePath (as-path f)))

(defn relativize
  "Returns relative `Path` by comparing `this` with `other` via [Path#relativize](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#relativize(java.nio.file.Path))."
  ^Path [this other]
  (.relativize (as-path this) (as-path other)))

(defn normalize
  "Returns normalize `Path` for `f` via [Path#normalize](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#normalize())."
  [f]
  (.normalize (as-path f)))

(defn canonicalize
  "Returns the canonical `Path` for `f` via [File#getCanonicalPath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#getCanonicalPath()).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links), when set, falls back on [[absolutize]] + [[normalize]].

  This function can be used as an alternative to [[real-path]] which requires files to exist."
  (^Path [f] (canonicalize f nil))
  (^Path [f {:keys [:nofollow-links]}]
   (if nofollow-links
     (-> f absolutize normalize)
     (as-path (.getCanonicalPath (as-file f))))))

(defn root
  "Returns `root` for `path` as `Path`, or `nil` via [Path#getRoot](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#getRoot()).

  The return value depends upon the runtime platform.

  On Windows, returns Windows specific roots, ex:
  (replace forward slash with backslash):
  * `C:/` for `C:/foo/bar`
  * `C:`  for `C:foo/bar` 
  * `//server/share` for `//server/share/foo/bar`

  On Linux and macOS, returns the leading `/` for anything that looks like an absolute path."
  [path]
  (.getRoot (as-path path)))

(defn file-name
  "Returns the name of the file or directory via [File#getName](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#getName()).
  E.g. (file-name \"foo/bar/baz\") returns \"baz\"."
  [x]
  (.getName (as-file x)))

(def ^:private continue (constantly :continue))

(defn walk-file-tree
  "Walks `f` using [Files/walkFileTree](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#walkFileTree(java.nio.file.Path,java.util.Set,int,java.nio.file.FileVisitor)).

  Options:
  * [`:follow-links`](/README.md#follow-links)
  * `:max-depth` maximum directory depth to walk, defaults is unlimited 
  * Override default visitor functions via:
    * `:pre-visit-dir` args `[dir attrs]`
    * `:post-visit-dir` args `[dir ex]`
    * `:visit-file` args `[file attrs]`
    * `:visit-file-failed` args `[file ex]`

  All visitor functions must return one of `:continue`, `:skip-subtree`, `:skip-siblings` or `:terminate`.
  A different return value will throw. When not supplied, visitor functions default
  to `(constantly :continue)`.

  Returns `f` as `Path`."
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
                          (postVisitDirectory [_ dir ex]
                            (-> (post-visit-dir dir ex)
                                file-visit-result))
                          (visitFile [_ path attrs]
                            (-> (visit-file path attrs)
                                file-visit-result))
                          (visitFileFailed [_ path ex]
                            (-> (visit-file-failed path ex)
                                file-visit-result))))))

#?(:bb nil :clj
   (defn- directory-stream
     "Returns a stream of all files in `dir`. The caller of this function is
  responsible for closing the stream, e.g. using `with-open`. The stream
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
     "Returns all paths in `dir` as vector. For descending into subdirectories use `glob.`
     - `glob-or-accept` - a glob string such as \"*.edn\" or a `(fn accept [^java.nio.file.Path p]) -> truthy`"
     ([dir]
      (with-open [stream (directory-stream dir)]
        (vec stream)))
     ([dir glob-or-accept]
      (with-open [stream (directory-stream dir glob-or-accept)]
        (vec stream)))))

(defn- path-seq
  [path]
  (tree-seq
   (fn [^Path path] (directory? path))
   (fn [^Path path] (seq (list-dir path)))
   (as-path path)))

(def file-separator
  "The system-dependent default name-separator character (as string)"
  File/separator)
(def path-separator
  "The system-dependent path-separator character (as string)."
  File/pathSeparator)

(def ^:private win?
  (-> (System/getProperty "os.name")
      (str/lower-case)
      (str/includes? "win")))

(defn- escape-glob-chars
  "Escapes special glob characters in the input string."
  [s]
  (let [special-chars #{\\ \* \? \[ \] \{ \}}
        escape-char (fn [c]
                      (if (contains? special-chars c)
                        (str "\\" c)
                        (str c)))]
    (apply str (map escape-char s))))

(defn- escape-regex-chars
  "Escapes a string so it can be used literally in a regular expression."
  [s]
  (java.util.regex.Pattern/quote s))

(defn match
  "Given a file `root` and match `pattern`, returns matches as vector of
  paths. Pattern interpretation is done using the rules described in
  [FileSystem#getPathMatcher](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String))

  Options:

  * `:hidden` - match hidden paths - note: on Windows paths starting with
  a dot are not hidden, unless their hidden attribute is set. Defaults to
  false, i.e. skip hidden files and folders.
  * [`:follow-links`](/README.md#follow-links) - follow symlinks. Defaults to false.
  * `:recursive` - match recursively. Defaults to false.
  * `:max-depth` - max depth to descend into directory structure, when
  matching recursively. Defaults to Integer/MAX_VALUE.

  Examples:
  `(fs/match \".\" \"regex:.*\\\\.clj\" {:recursive true})`"
  ([root pattern] (match root pattern nil))
  ([root pattern {:keys [hidden follow-links max-depth recursive]}]
   (let [[prefix pattern] (str/split pattern #":")
         base-path (-> root absolutize normalize str)
         escaped-base-path (case prefix
                             "glob" (escape-glob-chars base-path)
                             "regex" (escape-regex-chars base-path)
                             base-path)
         skip-hidden? (not hidden)
         results (atom (transient []))
         past-root? (volatile! nil)
         pattern (let [separator (when-not (str/ends-with? base-path file-separator)
                                   ;; we need to escape the file separator on Windows
                                   (str (when win? "\\")
                                        file-separator))]
                   (str escaped-base-path
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
  "Given a file `root` and glob `pattern`, returns matches as vector of
  paths. Patterns containing `**` or `/` will cause a recursive walk over
  path, unless overriden with :recursive. Similarly: :hidden will be enabled (when not set)
  when `pattern` starts with a dot.
  Glob interpretation is done using the rules described in
  [FileSystem#getPathMatcher](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String))

  Options:

  * `:hidden` - match hidden paths. Implied when `pattern` starts with a dot;
  otherwise, default to false. Note: on Windows files starting with a dot are
  not hidden, unless their hidden attribute is set.
  * [`:follow-links`](/README.md#follow-links) - follow symlinks. Defaults to false.
  * `:recursive` - force recursive search. Implied when `pattern` contains
  `**` or `/`; otherwise, defaults to false.
  * `:max-depth` - max depth to descend into directory structure, when
  recursing. Defaults to Integer/MAX_VALUE.

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
  "Copies `src` file to `dest` dir or file using [Files/copy](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#copy(java.nio.file.Path,java.nio.file.Path,java.nio.file.CopyOption...)).

  Options:
  * `:replace-existing`
  * `:copy-attributes`
  * [`:nofollow-links`](/README.md#nofollow-links) (used to determine to copy symbolic link itself or not).
  Returns `dest` as path."
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
  "Converts a set of `PosixFilePermission` `p` to a string."
  [p]
  (PosixFilePermissions/toString p))

(defn str->posix
  "Converts a string `s` to a set of `PosixFilePermission`.

  `s` is a string like `\"rwx------\"`."
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
  "Creates dir using [Files/createDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createDirectory(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
  Does not create parents.

  Options:
  * `:posix-file-permissions` permission for unix-like systems"
  ([path]
   (create-dir path nil))
  ([path {:keys [:posix-file-permissions]}]
   (let [attrs (posix->attrs posix-file-permissions)]
     (Files/createDirectory (as-path path) attrs))))

(defn create-dirs
  "Creates directories for `path` using [Files/createDirectories](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createDirectories(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
  Also creates parents if needed.
  Doesn't throw an exception if the dirs exist already. Similar to `mkdir -p`

  Options:
  * `:posix-file-permissions` permission for unix-like systems"
  ([path] (create-dirs path nil))
  ([path {:keys [:posix-file-permissions]}]
   (Files/createDirectories (as-path path) (posix->attrs posix-file-permissions))))

(defn set-posix-file-permissions
  "Sets `posix-file-permissions` on `f`. Accepts a string like `\"rwx------\"` or a set of `PosixFilePermission`."
  [f posix-file-permissions]
  (Files/setPosixFilePermissions (as-path f) (->posix-file-permissions posix-file-permissions)))

(defn posix-file-permissions
  "Returns posix file permissions for `f`. Use [[posix->str]] to view as a string.

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
  ([f] (posix-file-permissions f nil))
  ([f {:keys [:nofollow-links]}]
   (Files/getPosixFilePermissions (as-path f) (->link-opts nofollow-links))))

(defn- u+wx
  [f]
  (if win?
    (.setWritable (file f) true)
    (let [^java.util.Set perms (posix-file-permissions f)
          p1 (.add perms PosixFilePermission/OWNER_WRITE)
          p2 (.add perms PosixFilePermission/OWNER_EXECUTE)]
      (when (or p1 p2)
        (set-posix-file-permissions f perms)))))

(defn starts-with?
  "Returns `true` if path `this` starts with path `other`."
  [this other]
  (.startsWith (as-path this) (as-path other)))

(defn ends-with?
  "Returns `true` if path `this` ends with path `other`."
  [this other]
  (.endsWith (as-path this) (as-path other)))

(defn copy-tree
  "Copies entire file tree from `src` to `dest`. Creates `dest` if needed
  using [[create-dirs]], passing it the `:posix-file-permissions`
  option. Supports same options as [[copy]].
  Returns `dest` as `Path`"
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
   (let [csrc (canonicalize src)
         cdest (canonicalize dest)]
     (when (and (not= csrc cdest)
                (starts-with? cdest csrc))
       (throw (Exception. (format "Cannot copy src directory: %s, under itself to dest: %s"
                                  (str src) (str dest))))))
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
                                                            copy-options)
                                                (when-not win?
                                                  (u+wx to-dir))))
                                            :continue)
                           :visit-file (fn [from-path _attrs]
                                         (let [rel (relativize from from-path)
                                               to-file (path to rel)]
                                           (Files/copy ^Path from-path to-file
                                                       ^"[Ljava.nio.file.CopyOption;"
                                                       copy-options)
                                           :continue)
                                         :continue)
                           :post-visit-dir (fn [dir _ex]
                                             (let [rel (relativize from dir)
                                                   to-dir (path to rel)]
                                               (when-not win?
                                                 (let [perms (posix-file-permissions (file dir))]
                                                   (Files/setPosixFilePermissions to-dir perms)))
                                               :continue))}))))

(defn temp-dir
  "Returns `java.io.tmpdir` property as path."
  []
  (as-path (System/getProperty "java.io.tmpdir")))

(defn create-temp-dir
  "Creates a directory using [Files/createTempDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createTempDirectory(java.nio.file.Path,java.lang.String,java.nio.file.attribute.FileAttribute...)).

  This function does not set up any automatic deletion of the directories it
  creates. See [[with-temp-dir]] for that functionality.

  Options:
  * `:dir`: Directory in which to create the new directory. Defaults to default
  system temp dir (e.g. `/tmp`); see [[temp-dir]]. Must already exist.
  * `:prefix`: Provided as a hint to the process that generates the name of the
  new directory. In most cases, this will be the beginning of the new directory
  name. Defaults to a random (v4) UUID.
  * `:posix-file-permissions`: The new directory will be created with these
  permissions, given as a String as described in [[str->posix]]. If not
  specified, uses the file system default permissions for new directories.
  * :warning: `:path` **[DEPRECATED]** Previous name for `:dir`, kept
  for backwards compatibility. If both `:path` and `:dir` are given (don't do
  that!), `:dir` is used.

  Examples:
  * `(create-temp-dir)`
  * `(create-temp-dir {:posix-file-permissions \"rwx------\"})`
  * `(create-temp-dir {:dir (path (cwd) \"_workdir\") :prefix \"process-1-\"})`
  "
  ([] (create-temp-dir {}))
  ([{:keys [:dir :prefix :posix-file-permissions] :as opts}]
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
  "Creates an empty file using [Files/createTempFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createTempFile(java.nio.file.Path,java.lang.String,java.lang.String,java.nio.file.attribute.FileAttribute...)).

  This function does not set up any automatic deletion of the files it
  creates. Create the file in a [[with-temp-dir]] for that functionality.

  Options:
  * `:dir`: Directory in which to create the new file. Defaults to default
  system temp dir (e.g. `/tmp`); see [[temp-dir]]. Must already exist.
  * `:prefix`: Provided as a hint to the process that generates the name of the
  new file. In most cases, this will be the beginning of the new file name.
  Defaults to a random (v4) UUID.
  * `:suffix`: Provided as a hint to the process that generates the name of the
  new file. In most cases, this will be the end of the new file name.
  Defaults to a random (v4) UUID.
  * `:posix-file-permissions`: The new file will be created with these
  permissions, given as a String as described in [[str->posix]]. If not
  specified, uses the file system default permissions for new files.
  * :warning: `:path` **[DEPRECATED]** Previous name for `:dir`, kept
  for backwards compatibility. If both `:path` and `:dir` are given (don't do
  that!), `:dir` is used.

  Examples:
  * `(create-temp-file)`
  * `(create-temp-file {:posix-file-permissions \"rw-------\"})`
  * `(create-temp-file {:dir (path (cwd) \"_workdir\") :prefix \"process-1-\" :suffix \"-queue\"})`
  "
  ([] (create-temp-file {}))
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
  "Create a symbolic `link` to `target` via [Files/createSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createSymbolicLink(java.nio.file.Path,java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).

  As of this writing, JDKs do not recognize empty-string `target` `\"\"` as the cwd.
  Consider instead using a `target` of `\".\"` to link to the cwd."
  [link target]
  (Files/createSymbolicLink
   (as-path link)
   (as-path target)
   (make-array FileAttribute 0)))

(defn create-link
  "Create a new `link` (directory entry) for an `existing` file via [Files/createLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createLink(java.nio.file.Path,java.nio.file.Path))."
  [link existing]
  (Files/createLink
   (as-path link)
   (as-path existing)))

(defn read-link
  "Reads the target of a symbolic link `path` via [Files/readSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readSymbolicLink(java.nio.file.Path)).
  The target need not exist."
  [path]
  (java.nio.file.Files/readSymbolicLink (as-path path)))

(defn delete
  "Deletes `f` using [Files/delete](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#delete(java.nio.file.Path)).
  Returns `nil` if the delete was successful,
  throws otherwise. Does not follow symlinks."
  ;; We don't follow symlinks, since the link can target a dir and you should be
  ;; using delete-tree to delete that.
  [f]
  (Files/delete (as-path f)))

(defn delete-if-exists
  "Deletes `f` if it exists via [Files/deleteIfExists](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#deleteIfExists(java.nio.file.Path)).
  Returns `true` if the delete was successful,
  `false` if `f` didn't exist. Does not follow symlinks."
  [f]
  (Files/deleteIfExists (as-path f)))

(defn sym-link?
  "Determines if `f` is a symbolic link via [Files/isSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isSymbolicLink(java.nio.file.Path))."
  [f]
  (Files/isSymbolicLink (as-path f)))

(defn delete-tree
  "Deletes a file tree `root` using [[walk-file-tree]]. Similar to `rm -rf`. Does not follow symlinks.
   `force` ensures read-only directories/files are deleted. Similar to `chmod -R +wx` + `rm -rf`"
  ;; See delete-permission-assumptions-test
  ;; Implementation with the force flag is based on those assumptions
  ([root] (delete-tree root nil))
  ([root {:keys [force]}]
   (when (exists? root {:nofollow-links true})
     (walk-file-tree root
                     {:visit-file (fn [path _]
                                    (when (and win? force)
                                      (.setWritable (file path) true))
                                    (delete path)
                                    :continue)
                      :pre-visit-dir (fn [path _]
                                       (when force
                                         (u+wx path))
                                       :continue)
                      :post-visit-dir (fn [path _]
                                        (delete path)
                                        :continue)}))))

(defn create-file
  "Creates empty file at `path` using [Files/createFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createFile(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).

  Options:
  * `:posix-file-permissions` string format for posix file permissions is described in the [[str->posix]] docstring."
  ([path]
   (create-file path nil))
  ([path {:keys [:posix-file-permissions]}]
   (let [attrs (posix->attrs posix-file-permissions)]
     (Files/createFile (as-path path) attrs))))

(defn move
  "Move or rename a file `source` to a `target` dir or file via [Files/move](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#move(java.nio.file.Path,java.nio.file.Path,java.nio.file.CopyOption...)).
  Returns `target` as `Path`.

  Options:
  * `replace-existing` - overwrite existing `target`, default `false`
  * `atomic-move` - watchers will only see complete `target` file, default `false`
  * [`:nofollow-links`](/README.md#nofollow-links)" 
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
  "Returns parent of `f`. Akin to `dirname` in bash."
  [f]
  (.getParent (as-path f)))

(defn size
  "Returns the size of a file (in bytes)."
  [f]
  (Files/size (as-path f)))

(defn delete-on-exit
  "Requests delete of file `f` on exit via [File#deleteOnExit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#deleteOnExit()).
  Returns `f` unaltered."
  [f]
  (.deleteOnExit (as-file f))
  f)

(defn same-file?
  "Returns `true` if `this` is the same file as `other` via [Files/isSamefile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isSameFile(java.nio.file.Path,java.nio.file.Path))."
  [this other]
  (Files/isSameFile (as-path this) (as-path other)))

(defn read-all-bytes
  "Returns contents of file `f` as byte array via [Files/readAllBytes](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAllBytes(java.nio.file.Path))."
  ^bytes
  [f]
  (Files/readAllBytes (as-path f)))

(defn- ->charset ^Charset [charset]
  (if (string? charset)
    (Charset/forName charset)
    charset))

(defn read-all-lines
  "Read all lines from a file `f` via [Files/readAllLines](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAllLines(java.nio.file.Path,java.nio.charset.Charset))."
  ([f]
   (vec (Files/readAllLines (as-path f))))
  ([f {:keys [charset]
       :or {charset "utf-8"}}]
   (vec (Files/readAllLines
         (as-path f)
         (->charset charset)))))

;;;; Attributes, from github.com/corasaurus-hex/fs

(defn get-attribute
  "Return `attribute` for `path` via [Files/getAttribute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getAttribute(java.nio.file.Path,java.lang.String,java.nio.file.LinkOption...))

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
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
  "Reads `attributes` for `path` via [Files/readAttributes](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAttributes(java.nio.file.Path,java.lang.Class,java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
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
  "Same as [[read-attributes*]] but turns `attributes` for `path` into a map and keywordizes keys.
  Keywordizing can be changed by passing a `:key-fn` in the `opts` map."
  ([path attributes]
   (read-attributes path attributes nil))
  ([path attributes {:keys [:nofollow-links :key-fn] :as opts}]
   (->> (read-attributes* path attributes opts)
        (into {})
        (keyize (or key-fn keyword)))))

(defn set-attribute
  "Set `attribute` for `path` to `value` via [Files/setAttribute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#setAttribute(java.nio.file.Path,java.lang.String,java.lang.Object,java.nio.file.LinkOption...))"
  ([path attribute value]
   (set-attribute path attribute value nil))
  ([path attribute value {:keys [:nofollow-links]}]
   (Files/setAttribute (as-path path)
                       attribute
                       value
                       (->link-opts {:nofollow-links nofollow-links}))))

(defn file-time->instant
  "Converts `ft` [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)
  to an [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html)."
  [^FileTime ft]
  (.toInstant ft))

(defn instant->file-time
  "Converts `instant` [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html)
  to a [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)."
  [instant]
  (FileTime/from instant))

(defn file-time->millis
  "Converts `ft` [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)
   to epoch millis (long)."
  [^FileTime ft]
  (.toMillis ft))

(defn millis->file-time
  "Converts epoch millis (long) to a [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)."
  [millis]
  (FileTime/fromMillis millis))

(defn- ->file-time [x]
  (cond (int? x) (millis->file-time x)
        (instance? java.time.Instant x) (instant->file-time x)
        :else x))

(defn last-modified-time
  "Returns last modified time of `f` as a [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)."
  ([f]
   (last-modified-time f nil))
  ([f {:keys [nofollow-links] :as opts}]
   (get-attribute f "basic:lastModifiedTime" opts)))

(defn set-last-modified-time
  "Sets last modified time of `f` to `time` (`epoch millis` or [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html))."
  ([f time]
   (set-last-modified-time f time nil))
  ([f time {:keys [nofollow-links] :as opts}]
   (set-attribute f "basic:lastModifiedTime" (->file-time time) opts)))

(defn creation-time
  "Returns creation time of `f` as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).

  See [README notes](/README.md#creation-time) for some details on behaviour."
  ([f]
   (creation-time f nil))
  ([f {:keys [nofollow-links] :as opts}]
   (get-attribute f "basic:creationTime" opts)))

(defn set-creation-time
  "Sets creation time of `f` to time (`epoch millis` or [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)

  See [README notes](/README.md#set-creation-time) for some details on behaviour."
  ([f time]
   (set-creation-time f time nil))
  ([f time {:keys [nofollow-links] :as opts}]
   (set-attribute f "basic:creationTime" (->file-time time) opts)))

(defn list-dirs
  "Similar to list-dir but accepts multiple roots in `dirs` and returns the concatenated results.
  - `glob-or-accept` - a glob string such as `\"*.edn\"` or a `(fn accept [^java.nio.file.Path p]) -> truthy`"
  [dirs glob-or-accept]
  (mapcat #(list-dir % glob-or-accept) dirs))

(defn split-ext
  "Splits `path` on extension. If provided, a specific extension `ext`, the
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
  "Strips extension for `path` via [[split-ext]]."
  ([path]
   (strip-ext path nil))
  ([path {:keys [ext] :as opts}]
   (first (split-ext path opts))))

(defn extension
  "Returns the extension of `path` via [[split-ext]]."
  [path]
  (-> path split-ext last))

(defn split-paths
  "Splits a `joined-paths` list given as a string joined by the OS-specific [[path-separator]] into a vec of paths.
  On UNIX systems, the separator is ':', on Microsoft Windows systems it is ';'."
  [^String joined-paths]
  (mapv path (.split joined-paths path-separator)))

(defn exec-paths
  "Returns executable paths (using the `PATH` environment variable). Same
  as `(split-paths (System/getenv \"PATH\"))`."
  []
  (split-paths (System/getenv "PATH")))

(defn- filename-only?
  "Returns `true` if `f` is exactly a file name (i.e. with no absolute or
  relative path information)."
  [f]
  (let [f-as-path (as-path f)]
    (= f-as-path (.getFileName f-as-path))))

(defn which
  "Returns `Path` to first executable `program` found in `:paths` `opt`, similar to the `which` Unix command.
  Default for `:paths` is ([[exec-paths]]).

  On Windows, searches for `program` with filename extensions specified in `:win-exts` option.
  Default is `[\"com\" \"exe\" \"bat\" \"cmd\"]`.
  If `program` already includes an extension from `:win-exts`, it will be searched as-is first.

  When `program` is a relative or absolute path, `:paths` option is not consulted. On Windows, the `:win-exts`
  variants are still searched. On other OSes, the path for `program` will be returned if executable,
  else `nil`."
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
  "Returns every `Path` to `program` found in ([[exec-paths]]). See [[which]]."
  ([program] (which-all program nil))
  ([program opts]
   (which program (assoc opts :all true))))

;; the above can be implemented using:

;; user=> (first (filter fs/executable? (fs/list-dirs (filter fs/exists? (fs/exec-path)) "java")))
;; #object[sun.nio.fs.UnixPath 0x1dd74143 "/Users/borkdude/.jenv/versions/11.0/bin/java"]
;; although the which impl is faster

;;;; Modified since

(defn- last-modified-1
  "Returns max last-modified of regular file `f`. Returns 0 if file does not exist."
  ^FileTime [f]
  (if (exists? f)
    (last-modified-time f)
    (FileTime/fromMillis 0)))

(defn- max-filetime [filetimes]
  (reduce #(if (pos? (.compareTo ^FileTime %1 ^FileTime %2))
             %1 %2)
          (FileTime/fromMillis 0) filetimes))

(defn- last-modified
  "Returns max last-modified of f or of all files within f"
  [f]
  (if (exists? f)
    (if (regular-file? f)
      (last-modified-1 f)
      (max-filetime
             (map last-modified-1
                  (filter regular-file? (path-seq f)))))
    (FileTime/fromMillis 0)))

(defn- expand-file-set
  [file-set]
  (if (coll? file-set)
    (mapcat expand-file-set file-set)
    (filter regular-file? (path-seq file-set))))

(defn modified-since
  "Returns seq of regular files (non-directories, non-symlinks) from `file-set` that were modified since the `anchor` path.
  The `anchor` path can be a regular file or directory, in which case
  the recursive max last modified time stamp is used as the timestamp
  to compare with.  The `file-set` may be a regular file, directory or
  collection of files (e.g. returned by glob). Directories are
  searched recursively."
  [anchor file-set]
  (let [lm (last-modified anchor)]
    (map path (filter #(pos? (.compareTo (last-modified-1 %) lm)) (expand-file-set file-set)))))

;;;; End modified since

;;;; Zip

(defn unzip
  "Unzips `zip-file` to `dest` directory (default `\".\"`).

   Options:
   * `:replace-existing` - `true` / `false`: overwrite existing files
   * `:extract-fn` - function that decides if the current ZipEntry
     should be extracted. The function is only called for the file case
     (not directories) with a map with entries:
     * `:entry` and the current ZipEntry
     * `:name` and the name of the ZipEntry (result of calling `getName`)
     Extraction only occurs if a truthy value is returned (i.e. not
     nil/false)."
  ([zip-file] (unzip zip-file "."))
  ([zip-file dest] (unzip zip-file dest nil))
  ([zip-file dest {:keys [replace-existing extract-fn]}]
   (let [output-path (as-path dest)
         _ (create-dirs dest)
         cp-opts (->copy-opts replace-existing nil nil nil)]
     (with-open
       [^InputStream fis
        (if (instance? InputStream zip-file) zip-file
            (Files/newInputStream (as-path zip-file) (into-array java.nio.file.OpenOption [])))
       zis (ZipInputStream. fis)]
       (loop []
         (let [entry (.getNextEntry zis)]
           (when entry
             (let [entry-name (.getName entry)
                   new-path (.resolve output-path entry-name)]
               (if (.isDirectory entry)
                 (create-dirs new-path)
                 (when (or (nil? extract-fn)
                           (and (fn? extract-fn)
                                (extract-fn {:entry entry :name entry-name})))
                   (when-let [p (parent new-path)]
                     (create-dirs p))
                   (Files/copy ^java.io.InputStream zis
                               new-path
                               cp-opts))))
             (recur))))))))

;; partially borrowed from tools.build
(defn- add-zip-entry
  [^ZipOutputStream output-stream ^Path f fpath]
  (let [dir (directory? f)
        attrs (Files/readAttributes f BasicFileAttributes
                                    (->link-opts []))
        entry (doto (ZipEntry. (str fpath))
                (.setLastModifiedTime (.lastModifiedTime attrs)))]
    (.putNextEntry output-stream entry)
    (when-not dir
      (with-open [fis (BufferedInputStream. (FileInputStream. (file f)))]
        (io/copy fis output-stream)))

    (.closeEntry output-stream)))

;; partially borrowed from tools.build
(defn- copy-to-zip
  [^ZipOutputStream jos f path-fn]
  (let [files (path-seq f)]
    (run! (fn [^Path f]
            (let [dir (directory? f)
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
  "Zips entry or `entries` into `zip-file`. An entry may be a file or
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
         (copy-to-zip zos zpath #(when-not (same-file? % zip-file)
                                   (path-fn %))))))))

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

  Options:
  * `out-file` if not provided, the `source-file` name with `.gz` appended is used.
  * `dir` if not provided, the current directory is used.

  Returns the created gzip file."
  ([source-file]
   (gzip source-file {:dir "."}))
  ([source-file {:keys [dir out-file] :or {dir "."}}]
   (assert source-file "source-file must be specified")
   (assert (exists? source-file) "source-file does not exist")
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
  "Evaluates body with binding-name bound to the result of `(create-temp-dir
  options)`, then cleans up. See [[create-temp-dir]]
  for valid `options`.

  The directory will be removed with [[delete-tree]] on exit from the scope.

  Example:

  ```
  (with-temp-dir [d]
    (let [t (path d \"extract\")
      (create-dir t)
      (gunzip path-to-zip t)
      (copy (path t \"the-one-file-I-wanted.txt\") (path permanent-dir \"file-I-extracted.txt\"))))
  ;; d no longer exists here
  ```
  "
  {:arglists '[[[binding-name] & body]
               [[binding-name options] & body]]}
  [[binding-name options & more] & body]
  {:pre [(empty? more) (symbol? binding-name)]}
  `(let [~binding-name (create-temp-dir ~options)]
     (try
       ~@body
       (finally
         (delete-tree ~binding-name {:force true})))))

(def ^:private cached-home-dir
  (delay (path (System/getProperty "user.home"))))

(def ^:private cached-users-dir
  (delay (parent @cached-home-dir)))

(defn home
  "With no arguments, returns the current value of the `user.home`
  system property as a `Path`. If a `user` is passed, returns that user's home
  directory as found in the parent of home with no args."
  (^Path [] @cached-home-dir)
  (^Path [user] (if (empty? user) @cached-home-dir
                    (path @cached-users-dir user))))

(defn expand-home
  "If `f` begins with a tilde (`~`), expand the tilde to the value
  of the `user.home` system property. If the `f` begins with a
  tilde immediately followed by some characters, they are assumed to
  be a username. This is expanded to the path to that user's home
  directory. This is (naively) assumed to be a directory with the same
  name as the user relative to the parent of the current value of
  `user.home`. Returns a `Path`"
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
  "Returns current working directory as `Path`"
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
  "Writes `bytes` to `path` via [Files/write](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#write(java.nio.file.Path,byte%5B%5D,java.nio.file.OpenOption...)).

  Options:
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
  "Writes `lines`, a seqable of strings to `path` via [Files/write](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#write(java.nio.file.Path,java.lang.Iterable,java.nio.charset.Charset,java.nio.file.OpenOption...)).

  Options:
  * `:charset` (default `\"utf-8\"`)

  Open options:
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
  {:arglists '([path f & xs] [path opts f & xs])}
  ([path f & xs]
   (let [[opts f xs] (if (map? f)
                       [f (first xs) (rest xs)]
                       [nil f xs])
         {:keys [charset]
          :or {charset "utf-8"}} opts
         opts [:encoding charset]
         old-val (apply slurp (as-file path) opts)
         new-val (apply f old-val xs)]
     (apply spit (as-file path) new-val opts)
     new-val)))

(defn unixify
  "Returns path as string with Unix-style file separators (`/`)."
  [f]
  (let [s (str f)]
    (if win?
      (.replace s "\\" "/")
      s)))

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
