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
  "Returns `path`(s) coerced to a `Path`, combining multiple paths into one.
  Multiple-arg versions treat the first argument as parent and subsequent
  args as children relative to the parent."
  (^Path [path]
   (as-path path))
  (^Path [parent child]
   (if parent
     (if (string? child)
       (.resolve ^Path (as-path parent) ^String child)
       (.resolve ^Path (as-path parent) (as-path child)))
     (as-path child)))
  (^Path [parent child & more]
   (reduce path (path parent child) more)))

(def ^:private path* "synonym to avoid path arg-name to path fn shadowing conflicts" path)

(defn file
  "Returns `path`(s) coerced to a `File`, combining multiple paths into one.
  Multiple-arg versions treat the first argument as parent and subsequent args
  as children relative to the parent."
  (^File [path] (as-file path))
  (^File [path & paths]
   (apply io/file (map as-file (cons path paths)))))

(defn- ->link-opts ^"[Ljava.nio.file.LinkOption;"
  [nofollow-links]
  (into-array LinkOption
              (cond-> []
                nofollow-links
                (conj LinkOption/NOFOLLOW_LINKS))))

(defn real-path
  "Returns real path for `path` via [Path#toRealPath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#toRealPath(java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
  (^Path [path] (real-path path nil))
  (^Path [path {:keys [:nofollow-links]}]
   (.toRealPath (as-path path) (->link-opts nofollow-links))))

(defn owner
  "Returns the owner of `path` via [Files/getOwner](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getOwner(java.nio.file.Path,java.nio.file.LinkOption...)).
  Call `str` on return value to get the owner name as a string.

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
  ([path] (owner path nil))
  ([path {:keys [:nofollow-links]}]
   (Files/getOwner (as-path path) (->link-opts nofollow-links))))

;;;; Predicates

(defn regular-file?
  "Returns `true` if `path` is a regular file via [Files/isRegularFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isRegularFile(java.nio.file.Path,java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
  ([path] (regular-file? path nil))
  ([path {:keys [:nofollow-links]}]
   (Files/isRegularFile (as-path path)
                        (->link-opts nofollow-links))))

(defn directory?
  "Returns `true` if `path` is a directory via [Files/isDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isDirectory(java.nio.file.Path,java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
  ([path] (directory? path nil))
  ([path {:keys [:nofollow-links]}]
   (Files/isDirectory (as-path path)
                      (->link-opts nofollow-links))))

(def ^:private simple-link-opts
  (into-array LinkOption []))

(defn- directory-simple?
  [^Path path] (Files/isDirectory path simple-link-opts))

(defn hidden?
  "Returns `true` if `path` is hidden via [Files/isHidden](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isHidden(java.nio.file.Path)).

  TIP: some older JDKs can throw on empty-string path `(hidden \"\")`.
  Consider instead checking cwd via `(hidden \".\")`."
  [path] (Files/isHidden (as-path path)))

(defn absolute?
  "Returns `true` if `path` is absolute via [Path#isAbsolute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#isAbsolute())."
  [path] (.isAbsolute (as-path path)))

(defn executable?
  "Returns `true` if `path` is executable via [Files/isExecutable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isExecutable(java.nio.file.Path))."
  [path] (Files/isExecutable (as-path path)))

(defn readable?
  "Returns `true` if `path` is readable via [Files/isReadable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isReadable(java.nio.file.Path))"
  [path] (Files/isReadable (as-path path)))

(defn writable?
  "Returns `true` if `path` is writable via [Files/isWritable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isWritable(java.nio.file.Path))"
  [path] (Files/isWritable (as-path path)))

(defn relative?
  "Returns `true` if `path` is relative (in other words, is not [[absolute?]])."
  [path] (not (absolute? path)))

(defn exists?
  "Returns `true` if `path` exists via [Files/exists](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#exists(java.nio.file.Path,java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
  ([path] (exists? path nil))
  ([path {:keys [:nofollow-links]}]
   (try
     (Files/exists
      (as-path path)
      (->link-opts nofollow-links))
     (catch Exception _e
       false))))

;;;; End predicates

(defn components
  "Returns a seq of paths for all components of `path`.
  i.e.: split on the [[file-separator]]."
  [path]
  (seq (as-path path)))

(defn absolutize
  "Returns absolute path for `path` via [Path#toAbsolutePath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#toAbsolutePath())."
  [path] (.toAbsolutePath (as-path path)))

(defn relativize
  "Returns `other-path` relative to `base-path` via [Path#relativize](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#relativize(java.nio.file.Path)).

  Examples:
  - `(fs/relativize \"a/b\" \"a/b/c/d\")` => `c/d`
  - `(fs/relativize \"a/b/c/d\" \"a/b\")` => `../..`"
  ^Path [base-path other-path]
  (.relativize (as-path base-path) (as-path other-path)))

(defn normalize
  "Returns normalized path for `path` via [Path#normalize](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#normalize())."
  [path]
  (.normalize (as-path path)))

(defn canonicalize
  "Returns canonical path for `path` via [File#getCanonicalPath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#getCanonicalPath()).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links) - when set, falls back on [[absolutize]] + [[normalize]].

  This function can be used as an alternative to [[real-path]] which requires files to exist."
  (^Path [path] (canonicalize path nil))
  (^Path [path {:keys [:nofollow-links]}]
   (if nofollow-links
     (-> path absolutize normalize)
     (as-path (.getCanonicalPath (as-file path))))))

(defn root
  "Returns root path for `path`, or `nil`, via [Path#getRoot](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#getRoot()).

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
  "Returns the name of the file or directory for `path` via [File#getName](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#getName()).
  E.g. `(file-name \"foo/bar/baz\")` returns `\"baz\"`."
  [path]
  (.getName (as-file path)))

(def ^:private continue (constantly :continue))

(defn walk-file-tree
  "Walks `path` via [Files/walkFileTree](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#walkFileTree(java.nio.file.Path,java.util.Set,int,java.nio.file.FileVisitor)).

  Options:
  * [`:follow-links`](/README.md#follow-links)
  * `:max-depth` - maximum directory depth to walk, defaults is unlimited
  * Override default visitor functions via:
    * `:pre-visit-dir` - args `[dir attrs]`
    * `:post-visit-dir` - args `[dir ex]`
    * `:visit-file` - args `[file attrs]`
    * `:visit-file-failed` - args `[file ex]`

  All visitor functions must return one of `:continue`, `:skip-subtree`, `:skip-siblings` or `:terminate`.
  A different return value will throw. When not supplied, visitor functions default
  to `(constantly :continue)`.

  Returns `path`."
  [path
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
    (Files/walkFileTree (as-path path)
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
  can be consumed as a seq by calling seq on it. Accepts optional [[glob]] string or
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
     "Returns a vector of all paths in `dir`. For descending into subdirectories use [[glob]].

     - `glob-or-accept` - a [[glob]] string such as \"*.edn\" or a `(fn accept [^java.nio.file.Path p]) -> truthy`"
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
  "The system-dependent default path component separator character (as string)."
  File/separator)

(def path-separator
  "The system-dependent path-separator character (as string)."
  File/pathSeparator)

(def ^:private win?
  "`true` if running on Windows Operating System"
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
  "Returns a vector of paths matching `pattern` (on path and filename) relative to `root-dir`.
  Pattern interpretation is done using the rules described in
  [FileSystem#getPathMatcher](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String))

  Options:
  * `:hidden` - match hidden paths - note: on Windows paths starting with
  a dot are not hidden, unless their hidden attribute is set. Defaults to
  `false`, i.e. skip hidden files and folders.
  * [`:follow-links`](/README.md#follow-links) - follow symlinks. Defaults to false.
  * `:recursive`
    * `true` - `pattern` is matched against all descendant files and directories under `root`
    * `false` (default) - `pattern` is matched only against immediate children under `root`
  * `:max-depth` - max depth to descend into directory structure, when
  matching recursively. Defaults to `Integer/MAX_VALUE`.

  Examples: 
  - `(fs/match \".\" \"regex:.*\\\\.clj\" {:recursive true})`

  See also: [[glob]]"
  ([root-dir pattern] (match root-dir pattern nil))
  ([root-dir pattern {:keys [hidden follow-links max-depth recursive]}]
   (let [[prefix pattern] (str/split pattern #":")
         base-path (-> root-dir absolutize normalize str)
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
       (if (relative? root-dir)
         (mapv #(relativize absolute-cwd %)
               results)
         results)))))

(defn glob
  "Returns a vector of paths matching glob `pattern` (on path and filename) relative to `root-dir`.
  Patterns containing `**` or `/` will cause a recursive walk under
  `root-dir`, unless overriden with `:recursive false`. Similarly, `:hidden` will be automaticaly enabled 
  when `pattern` starts with a dot.
  Glob interpretation is done using the rules described in
  [FileSystem#getPathMatcher](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String))

  Options:
  * `:hidden` - match hidden paths. Implied `true` when `pattern` starts with a dot;
  otherwise, defaults to `false`. Note: on Windows files starting with a dot are
  not hidden, unless their hidden attribute is set.
  * [`:follow-links`](/README.md#follow-links) - follow symlinks. Defaults to `false`.
  * `:recursive` - implied `true` when `pattern` contains `**` or `/`; otherwise, defaults to `false`.
    * `true` - `pattern` is matched against all descendant files and directories under `root`
    * `false` - `pattern` is matched only against immediate children under `root`
  * `:max-depth` - max depth to descend into directory structure, when
  recursing. Defaults to `Integer/MAX_VALUE`.

  Examples:
  - `(fs/glob \".\" \"**.clj\")` - finds `.clj` files and dirs under `.` dir and its subdirs
  - `(fs/glob \".\" \"**.clj\" {:recursive false})` - finds `.clj` files and dirs immediately under `.` dir only
  - `(fs/glob \".\" \"*.clj\" {:recursive true})` - finds `.clj` files and dirs immediately under `.` only (`pattern` lacks directory wildcards)

  If on macOS, see [note on glob](/README.md#glob)

  See also: [[match]]"
  ([root-dir pattern] (glob root-dir pattern nil))
  ([root-dir pattern opts]
   (let [recursive (:recursive opts
                               (or (str/includes? pattern "**")
                                   (str/includes? pattern file-separator)
                                   (when win?
                                     (str/includes? pattern "/"))))
         hidden    (:hidden opts (str/starts-with? pattern "."))]
     (match root-dir (str "glob:" pattern) (assoc opts :recursive recursive :hidden hidden)))))

(defn- ->copy-opts ^"[Ljava.nio.file.CopyOption;"
  [replace-existing copy-attributes atomic-move nofollow-links]
  (into-array CopyOption
              (cond-> []
                replace-existing (conj StandardCopyOption/REPLACE_EXISTING)
                copy-attributes  (conj StandardCopyOption/COPY_ATTRIBUTES)
                atomic-move      (conj StandardCopyOption/ATOMIC_MOVE)
                nofollow-links   (conj LinkOption/NOFOLLOW_LINKS))))

(defn copy
  "Copies `source-file` to `target-path` dir or file via [Files/copy](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#copy(java.nio.file.Path,java.nio.file.Path,java.nio.file.CopyOption...)).

  Options:
  * `:replace-existing`
  * `:copy-attributes`
  * [`:nofollow-links`](/README.md#nofollow-links) - used to determine to copy symbolic link itself or not."
  ([source-file target-path] (copy source-file target-path nil))
  ([source-file target-path {:keys [replace-existing
                                    copy-attributes
                                    nofollow-links]}]
   (let [copy-options (->copy-opts replace-existing copy-attributes false nofollow-links)
         dest (as-path target-path)
         dest (if (directory-simple? dest)
                (path dest (file-name source-file))
                dest)
         input-stream? (instance? java.io.InputStream source-file)]
     (if input-stream?
       (Files/copy ^java.io.InputStream source-file dest copy-options)
       (Files/copy (as-path source-file) dest copy-options)))))

(defn posix->str
  "Returns permissions string, like `\"rwx------\"`, for a set of `PosixFilePermission` `p`.

  See also [[str->posix]]"
  [p]
  (PosixFilePermissions/toString p))

(defn str->posix
  "Returns a set of `PosixFilePermission` for permissions string `s`.

  `s` is a string like `\"rwx------\"`.

  See also [[posix->str]]"
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
  "Returns new `dir` created via [Files/createDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createDirectory(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
  Does not create parents.

  Options:
  * `:posix-file-permissions` - string format for unix-like system permissions for `dir`, as described in [[str->posix]].
  Affected by [umask](/README.md#umask)."
  ([dir]
   (create-dir dir nil))
  ([dir {:keys [:posix-file-permissions]}]
   (let [attrs (posix->attrs posix-file-permissions)]
     (Files/createDirectory (as-path dir) attrs))))

(defn create-dirs
  "Returns `dir` after creating directories for `dir` via [Files/createDirectories](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createDirectories(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
  Also creates parents if needed.
  Does not throw an exception if the dirs exist already. Similar to `mkdir -p` shell command.
  
  Options:
  * `:posix-file-permissions` - string format for unix-like system permissions for `dir`, as described in [[str->posix]].
  Affected by [umask](/README.md#umask)."
  ([dir] (create-dirs dir nil))
  ([dir {:keys [:posix-file-permissions]}]
   (let [p (as-path dir)]
     (if (directory? p) ;; compensate for JDK11 which does not follow symlinks in its createDirectories
       p
       (Files/createDirectories (as-path dir) (posix->attrs posix-file-permissions))))))

(defn set-posix-file-permissions
  "Sets `posix-file-permissions` on `path` via [Files/setPosixFilePermissions](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#setPosixFilePermissions(java.nio.file.Path,java.util.Set)).
  Accepts a string like `\"rwx------\"` or a set of `PosixFilePermission`.

  See also: [[posix-file-permissions]]"
  [path posix-file-permissions]
  (Files/setPosixFilePermissions (as-path path) (->posix-file-permissions posix-file-permissions)))

(defn posix-file-permissions
  "Returns a set of `PosixFilePermissions` for `path` via [Files/getPosixFilePermissions](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getPosixFilePermissions(java.nio.file.Path,java.nio.file.LinkOption...)).
  Use [[posix->str]] to convert to a string.

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)

  See also: [[set-posix-file-permissions]]"
  ([path] (posix-file-permissions path nil))
  ([path {:keys [:nofollow-links]}]
   (Files/getPosixFilePermissions (as-path path) (->link-opts nofollow-links))))

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
  "Returns `true` if `this-path` starts with `other-path` via [Path#startsWith](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#startsWith(java.nio.file.Path)).

  See also: [[ends-with?]]"
  [this-path other-path]
  (.startsWith (as-path this-path) (as-path other-path)))

(defn ends-with?
  "Returns `true` if `this-path` ends with `other-path` via [Path#endsWith](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#endsWith(java.nio.file.Path)).

  See also: [[starts-with?]]"
  [this-path other-path]
  (.endsWith (as-path this-path) (as-path other-path)))

(defn copy-tree
  "Copies entire file tree from `source-dir` to `target-dir`. Creates `target-dir` if needed.
 
  Options:
  * same as [[copy]]
  * `:posix-file-permissions` - string format unix-like system permissions passed to [[create-dirs]] when creating `target-dir`."
  ([source-dir target-dir] (copy-tree source-dir target-dir nil))
  ([source-dir target-dir {:keys [:replace-existing
                                  :copy-attributes
                                  :nofollow-links]
                           :as opts}]
   ;; cf. Python
   (when-not (directory? source-dir opts)
     (throw (IllegalArgumentException. (str "Not a directory: " source-dir))))
   ;; cf. Python
   (when (and (exists? target-dir opts)
              (not (directory? target-dir opts)))
     (throw (IllegalArgumentException. (str "Not a directory: " target-dir))))
   ;; cf. Python
   (let [csrc (canonicalize source-dir)
         cdest (canonicalize target-dir)]
     (when (and (not= csrc cdest)
                (starts-with? cdest csrc))
       (throw (Exception. (format "Cannot copy src directory: %s, under itself to dest: %s"
                                  (str source-dir) (str target-dir))))))
   (create-dirs target-dir opts)
   (let [copy-options (->copy-opts replace-existing copy-attributes false nofollow-links)
         link-options (->link-opts nofollow-links)
         from (real-path source-dir {:nofollow-links nofollow-links})
         ;; using canonicalize here because real-path requires the path to exist
         to (canonicalize target-dir {:nofollow-links nofollow-links})]
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
  "Returns path to directory created via [Files/createTempDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createTempDirectory(java.nio.file.Path,java.lang.String,java.nio.file.attribute.FileAttribute...)).

  This function does not set up any automatic deletion of the directories it
  creates. See [[with-temp-dir]] for that functionality.

  Options:
  * `:dir` - directory in which to create the new directory. Defaults to default
  system temp dir (e.g. `/tmp`); see [[temp-dir]]. Must already exist.
  * `:prefix` - provided as a hint to the process that generates the name of the
  new directory. In most cases, this will be the beginning of the new directory
  name. Defaults to a random (v4) UUID.
  * `:posix-file-permissions` - string format unix-like system permissions as described in [[str->posix]] for new directory.
  If not specified, uses the file system default permissions for new directories.
  Affected by [umask](/README.md#umask).
  * :warning: `:path` - **[DEPRECATED]** previous name for `:dir`, kept
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
  "Returns path to empty file created via [Files/createTempFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createTempFile(java.nio.file.Path,java.lang.String,java.lang.String,java.nio.file.attribute.FileAttribute...)).

  This function does not set up any automatic deletion of the files it
  creates. Create the file in a [[with-temp-dir]] for that functionality.

  Options:
  * `:dir` - directory in which to create the new file. Defaults to default
  system temp dir (e.g. `/tmp`); see [[temp-dir]]. Must already exist.
  * `:prefix` - provided as a hint to the process that generates the name of the
  new file. In most cases, this will be the beginning of the new file name.
  Defaults to a random (v4) UUID.
  * `:suffix` - provided as a hint to the process that generates the name of the
  new file. In most cases, this will be the end of the new file name.
  Defaults to a random (v4) UUID.
  * `:posix-file-permissions` - string format unix-like system permissions for new file, as described in [[str->posix]].
  If not specified, uses the file system default permissions for new files.
  Affected by [umask](/README.md#umask).
  * :warning: `:path` - **[DEPRECATED]** Previous name for `:dir`, kept
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
  "Returns new symbolic `link` to `target-path` created via [Files/createSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createSymbolicLink(java.nio.file.Path,java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).

  As of this writing, JDKs do not recognize empty-string `target-path` `\"\"` as the cwd.
  Consider instead using a `target-path` of `\".\"` to link to the cwd."
  [link target-path]
  (Files/createSymbolicLink
   (as-path link)
   (as-path target-path)
   (make-array FileAttribute 0)))

(defn create-link
  "Returns a new hard `link` (directory entry) for an `existing-file` created via [Files/createLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createLink(java.nio.file.Path,java.nio.file.Path))."
  [link existing-file]
  (Files/createLink
   (as-path link)
   (as-path existing-file)))

(defn read-link
  "Returns the immediate target of `sym-link-path` via [Files/readSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readSymbolicLink(java.nio.file.Path)).
  The target need not exist."
  [sym-link-path]
  (java.nio.file.Files/readSymbolicLink (as-path sym-link-path)))

(defn delete
  "Deletes `path` via [Files/delete](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#delete(java.nio.file.Path)).
  Returns `nil` if the delete was successful,
  throws otherwise. Does not follow symlinks."
  ;; We don't follow symlinks, since the link can target a dir and you should be
  ;; using delete-tree to delete that.
  [path]
  (Files/delete (as-path path)))

(defn delete-if-exists
  "Deletes `path` if it exists via [Files/deleteIfExists](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#deleteIfExists(java.nio.file.Path)).
  Returns `true` if the delete was successful,
  `false` if `path` didn't exist. Does not follow symlinks."
  [path]
  (Files/deleteIfExists (as-path path)))

(defn sym-link?
  "Returns `true` if `path` is a symbolic link via [Files/isSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isSymbolicLink(java.nio.file.Path))."
  [path]
  (Files/isSymbolicLink (as-path path)))

(defn delete-tree
  "Deletes the file tree at `root-path` using [[walk-file-tree]]. Similar to `rm -rf` shell command. Does not follow symlinks.

  Options:
  * `:force` - if `true` forces deletion of read-only files/directories. Similar to `chmod -R +wx` + `rm -rf` shell commands."
  ;; See: delete-permissions-* tests
  ;; Implementation with the force flag is based on assumptions in those tests
  ([root-path] (delete-tree root-path nil))
  ([root-path {:keys [force]}]
   (when (exists? root-path {:nofollow-links true})
     (walk-file-tree root-path
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
  "Returns new empty `file` created via [Files/createFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createFile(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).

  Options:
  * `:posix-file-permissions` - string format for unix-like system permissions for `file`, as described in [[str->posix]].
  Affected by [umask](/README.md#umask)."
  ([file]
   (create-file file nil))
  ([file {:keys [:posix-file-permissions]}]
   (let [attrs (posix->attrs posix-file-permissions)]
     (Files/createFile (as-path file) attrs))))

(defn move
  "Moves or renames dir or file at `source-path` to `target-path` dir or file via [Files/move](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#move(java.nio.file.Path,java.nio.file.Path,java.nio.file.CopyOption...)).
  If `target-path` is a directory, moves `source-path` under `target-path`.
  Never follows symbolic links.

  Returns `target-path` path.

  Options:
  * `replace-existing` - overwrite existing `target-path`, default `false`
  * `atomic-move` - watchers will only see complete `target-path` file, default `false`"
  ([source-path target-path] (move source-path target-path nil))
  ([source-path target-path {:keys [:replace-existing
                                    :atomic-move]}]
   (let [target (as-path target-path)
         nofollow-links true
         link-opts (->link-opts nofollow-links)]
     (if (Files/isDirectory target link-opts)
       (Files/move (as-path source-path)
                   (path target (file-name source-path))
                   (->copy-opts replace-existing false atomic-move nofollow-links))
       (Files/move (as-path source-path)
                   target
                   (->copy-opts replace-existing false atomic-move nofollow-links))))))

(defn parent
  "Returns parent path of `path` via [Paths/getParent](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#getParent()).
  Akin to `dirname` in bash."
  [path]
  (.getParent (as-path path)))

(defn size
  "Returns the size of `path` in bytes."
  [path]
  (Files/size (as-path path)))

(defn delete-on-exit
  "Requests delete of `path` on exit via [File#deleteOnExit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#deleteOnExit()).
  Returns `path`."
  [path]
  (.deleteOnExit (as-file path))
  path)

(defn same-file?
  "Returns `true` if `this-path` is the same file as `other-path` via [Files/isSamefile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isSameFile(java.nio.file.Path,java.nio.file.Path))."
  [this-path other-path]
  (Files/isSameFile (as-path this-path) (as-path other-path)))

(defn read-all-bytes
  "Returns contents of `file` as byte array via [Files/readAllBytes](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAllBytes(java.nio.file.Path))."
  ^bytes
  [file]
  (Files/readAllBytes (as-path file)))

(defn- ->charset ^Charset [charset]
  (if (string? charset)
    (Charset/forName charset)
    charset))

(defn read-all-lines
  "Return contents of `file` as a vector of lines via [Files/readAllLines](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAllLines(java.nio.file.Path,java.nio.charset.Charset)).

  Options:
  - `:charset` - defaults to `\"utf-8\"`"
  ([file]
   (vec (Files/readAllLines (as-path file))))
  ([file {:keys [charset]
          :or {charset "utf-8"}}]
   (vec (Files/readAllLines
         (as-path file)
         (->charset charset)))))

;;;; Attributes, from github.com/corasaurus-hex/fs

(defn get-attribute
  "Returns `attribute` for `path` via [Files/getAttribute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getAttribute(java.nio.file.Path,java.lang.String,java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
  ([path attribute]
   (get-attribute path attribute nil))
  ([path attribute {:keys [:nofollow-links]}]
   (Files/getAttribute (as-path path)
                       attribute
                       (->link-opts nofollow-links))))

(defn- keyize
  [key-fn m]
  (let [f (fn [[k v]] (if (string? k) [(key-fn k) v] [k v]))]
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn read-attributes*
  "Returns requested `attributes` for `path` via [Files/readAttributes](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAttributes(java.nio.file.Path,java.lang.Class,java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)"
  ([path attributes]
   (read-attributes* path attributes nil))
  ([path attributes {:keys [:nofollow-links]}]
   (let [p (as-path path)
         link-opts (->link-opts nofollow-links)
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
  "Same as [[read-attributes*]] but returns requested `attributes` for `path` as a map with keywordized attribute keys.

  Options:
  * `:key-fn` - optionally override keywordizing function with your own.
  * [`:nofollow-links`](/README.md#nofollow-links)"
  ([path attributes]
   (read-attributes path attributes nil))
  ([path attributes {:keys [:nofollow-links :key-fn] :as opts}]
   (->> (read-attributes* path attributes opts)
        (into {})
        (keyize (or key-fn keyword)))))

(defn set-attribute
  "Sets `attribute` for `path` to `value` via [Files/setAttribute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#setAttribute(java.nio.file.Path,java.lang.String,java.lang.Object,java.nio.file.LinkOption...))"
  ([path attribute value]
   (set-attribute path attribute value nil))
  ([path attribute value {:keys [:nofollow-links]}]
   (Files/setAttribute (as-path path)
                       attribute
                       value
                       (->link-opts nofollow-links))))

(defn file-time->instant
  "Returns [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html) `ft`
  as [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html)."
  [^FileTime ft]
  (.toInstant ft))

(defn instant->file-time
  "Returns [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html) `instant`
  as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)."
  [instant]
  (FileTime/from instant))

(defn file-time->millis
  "Returns [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html) `ft`
  as epoch milliseconds (long)."
  [^FileTime ft]
  (.toMillis ft))

(defn millis->file-time
  "Returns epoch milliseconds (long)
  as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)"
  [millis]
  (FileTime/fromMillis millis))

(defn- ->file-time [x]
  (cond (int? x) (millis->file-time x)
        (instance? java.time.Instant x) (instant->file-time x)
        (instance? FileTime x) x
        :else (throw (ex-info "Unrecognized time type" {}))))

(defn last-modified-time
  "Returns last modified time of `path` as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).

  See also: [[set-last-modified-time]], [[creation-time]], [[file-time->instant]], [[file-time->millis]]"
  ([path]
   (last-modified-time path nil))
  ([path {:keys [nofollow-links] :as opts}]
   (get-attribute path "basic:lastModifiedTime" opts)))

(defn set-last-modified-time
  "Sets last modified `time` of `path`. 
  `time` can be `epoch milliseconds`,
  [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html),
  or [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html).

  See also: [[last-modified-time]]"
  ([path time]
   (set-last-modified-time path time nil))
  ([path time {:keys [nofollow-links] :as opts}]
   (set-attribute path "basic:lastModifiedTime" (->file-time time) opts)))

(defn creation-time
  "Returns creation time of `path` as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).

  See [README notes](/README.md#creation-time) for some details on behaviour.

  See also: [[set-creation-time]], [[last-modified-time]], [[file-time->instant]], [[file-time->millis]]"
  ([path]
   (creation-time path nil))
  ([path {:keys [nofollow-links] :as opts}]
   (get-attribute path "basic:creationTime" opts)))

(defn set-creation-time
  "Sets creation `time` of `path`.
  `time` can be `epoch milliseconds`,
  [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html),
  or [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html) .

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)

  See [README notes](/README.md#set-creation-time) for some details on behaviour.

  See also: [[creation-time]]"
  ([path time]
   (set-creation-time path time nil))
  ([path time {:keys [nofollow-links] :as opts}]
   (set-attribute path "basic:creationTime" (->file-time time) opts)))

(defn touch
  "Update last modified time of `path` to `:time`, creating `path` as a file if it does not exist.

  If `path` is deleted by some other process/thread before `:time` is set,
  a `NoSuchFileException` will be thrown. Callers can, if their use case requires it,
  implement their own retry loop.

  Options:
  * `:time` - last modified time (epoch milliseconds, `Instant`, or `FileTime`), defaults to current time
  * [`:nofollow-links`](/README.md#nofollow-links)"
  ([path]
   (touch path nil))
  ([path {:keys [time nofollow-links] :as opts}]
   (let [time (when time (->file-time time)) ;; convert early to fail fast on invalid time value
         path (as-path path)
         time (or time (java.time.Instant/now))]
     (try
       ;; attempt touch on existing path
       (set-last-modified-time path time opts)
       (catch java.nio.file.NoSuchFileException _
         ;; file/dir does not exist, attempt to create file
         ;; create via FileChannel/open to allow for case where some other process/thread
         ;; might have created path since exception was thrown and now
         (with-open [_chan (-> (java.nio.channels.FileChannel/open
                                path
                                (into-array [java.nio.file.StandardOpenOption/CREATE
                                             java.nio.file.StandardOpenOption/WRITE])))])
         (set-last-modified-time path time opts))))))

(defn list-dirs
  "Similar to [[list-dir]] but accepts multiple roots in `dirs` and returns the concatenated results.
  - `glob-or-accept` - a [[glob]] string such as `\"*.edn\"` or a `(fn accept [^java.nio.file.Path p]) -> truthy`"
  [dirs glob-or-accept]
  (mapcat #(list-dir % glob-or-accept) dirs))

(defn split-ext
  "Returns `path` split on extension.
  Leading directories in `path` are not processed.

  Options:
  * `:ext` - split on specified extension (do not include a leading dot) 
  
  Examples:
  - `(fs/split-ext \"foo.bar.baz\")` => `[\"foo.bar\" \"baz\"]`
  - `(fs/split-ext \"foo.bar.baz\" {:ext \"bar.baz\"})`  => `[\"foo\" \"bar.baz\"]`
  - `(fs/split-ext \"foo.bar.baz\" {:ext \"png\"})`  => `[\"foo.bar.baz\" nil]`"
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
  "Returns `path` with extension stripped via [[split-ext]]."
  ([path]
   (strip-ext path nil))
  ([path {:keys [ext] :as opts}]
   (first (split-ext path opts))))

(defn extension
  "Returns the extension of `path` via [[split-ext]]."
  [path]
  (-> path split-ext last))

(defn split-paths
  "Returns a vector of paths from paths in `joined-paths` string.
  `joined-paths` is split on OS-specific [[path-separator]].
  On UNIX systems, the separator is `:`, on Microsoft Windows systems it is `;`."
  [^String joined-paths]
  (mapv path (.split joined-paths path-separator)))

(defn exec-paths
  "Returns a vector of command search paths (from the `PATH` environment variable). Same
  as `(split-paths (System/getenv \"PATH\"))`."
  []
  (split-paths (System/getenv "PATH")))

(defn- filename-only?
  "Returns `true` if `path` is exactly a file name (i.e. with no absolute or
  relative path information)."
  [path]
  (let [f-as-path (as-path path)]
    (= f-as-path (.getFileName f-as-path))))

(defn which
  "Returns path to first executable `program` found in `:paths`, similar to the `which` Unix command.

  When `program` is a relative or absolute path, `:paths` option is not consulted.
  On Windows, the `:win-exts` variants are still searched.
  On other OSes, the path for `program` will be returned if executable, else `nil`.

  Options:
  * `:paths` - paths to search, default is return of ([[exec-paths]])
  * `:win-exts` - active on Windows only. Searches for `program` with filename extensions specified in `:win-exts` option.
  If `program` already includes an extension from `:win-exts`, it will be searched as-is first.
  Default is `[\"com\" \"exe\" \"bat\" \"cmd\"]`."
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
         paths (or (:paths opts) (exec-paths))
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
                            f (if (relative? program)
                                (path p program)
                                (path program))]
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
  "Returns a vector of every path to `program` found in ([[exec-paths]]). See [[which]]."
  ([program] (which-all program nil))
  ([program opts]
   (which program (assoc opts :all true))))

;; the above can be implemented using:

;; user=> (first (filter fs/executable? (fs/list-dirs (filter fs/exists? (fs/exec-path)) "java")))
;; #object[sun.nio.fs.UnixPath 0x1dd74143 "/Users/borkdude/.jenv/versions/11.0/bin/java"]
;; although the which impl is faster

;;;; Modified since

(defn- last-modified-1
  "Returns max last-modified of regular `file`. Returns 0 if file does not exist."
  ^FileTime [file]
  (if (exists? file)
    (last-modified-time file)
    (FileTime/fromMillis 0)))

(defn- max-filetime [filetimes]
  (reduce #(if (pos? (.compareTo ^FileTime %1 ^FileTime %2))
             %1 %2)
          (FileTime/fromMillis 0) filetimes))

(defn- last-modified
  "Returns max last-modified of path or of all files within `path`"
  [path]
  (if (exists? path)
    (if (regular-file? path)
      (last-modified-1 path)
      (max-filetime
       (map last-modified-1
            (filter regular-file? (path-seq path)))))
    (FileTime/fromMillis 0)))

(defn- expand-file-set
  [file-set]
  (if (coll? file-set)
    (mapcat expand-file-set file-set)
    (filter regular-file? (path-seq file-set))))

(defn modified-since
  "Returns seq of regular files (non-directories, non-symlinks) from `path-set` that were modified since the `anchor-path`.
  The `anchor-path` can be a regular file or directory, in which case
  the recursive max last modified time stamp is used as the timestamp
  to compare with.  The `path-set` may be a regular file, directory or
  collection of paths (e.g. as returned by [[glob]]). Directories are
  searched recursively."
  [anchor-path path-set]
  (let [lm (last-modified anchor-path)]
    (map path (filter #(pos? (.compareTo (last-modified-1 %) lm)) (expand-file-set path-set)))))

;;;; End modified since

;;;; Zip

(defn unzip
  "Unzips `zip-file` to `target-dir` (default `\".\"`).

   Options:
   * `:replace-existing` - `true` / `false`: overwrite existing files
   * `:extract-fn` - function that decides if the current `ZipEntry`
     should be extracted. Extraction only occurs if a truthy value is returned (i.e. not nil/false).
     The function is only called for for files (not directories) with a single map arg:
     * `:entry` - the current `ZipEntry`
     * `:name` - the name of the `ZipEntry` (result of calling `getName`)

  See also: [[zip]]."
  ([zip-file] (unzip zip-file "."))
  ([zip-file target-dir] (unzip zip-file target-dir nil))
  ([zip-file target-dir {:keys [replace-existing extract-fn]}]
   (let [output-path (as-path target-dir)
         _ (create-dirs target-dir)
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
  [^ZipOutputStream output-stream ^Path path fpath]
  (let [dir (directory? path)
        attrs (Files/readAttributes path BasicFileAttributes
                                    (->link-opts []))
        entry (doto (ZipEntry. (str fpath))
                (.setLastModifiedTime (.lastModifiedTime attrs)))]
    (.putNextEntry output-stream entry)
    (when-not dir
      (with-open [fis (BufferedInputStream. (FileInputStream. (file path)))]
        (io/copy fis output-stream)))

    (.closeEntry output-stream)))

;; partially borrowed from tools.build
(defn- copy-to-zip
  [^ZipOutputStream jos path path-fn]
  (let [files (path-seq path)]
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
  "Zips `path-or-paths` into `zip-file`. A path may be a file or
  directory. Directories are included recursively and their names are
  preserved in the zip file. Currently only accepts relative paths.

  Options:
  * `:root` - optional directory to be elided in `zip-file` entries. E.g.: `(fs/zip [\"src\"] {:root \"src\"})`
  * `:path-fn` - an optional custom path conversion function.
  A single-arg function called for each file sytem path returning the path to be used for the corresponding zip entry.

  See also: [[unzip]]."
  ([zip-file path-or-paths]
   (zip zip-file path-or-paths nil))
  ([zip-file path-or-paths opts]
   (let [entries (if (or (string? path-or-paths)
                         (instance? File path-or-paths)
                         (instance? Path path-or-paths))
                   [path-or-paths]
                   path-or-paths)
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
  "Extracts `gz-file` to `target-dir`.

   If `target-dir` not specified (or `nil`) defaults to `gz-file` dir.

   File is extracted to `target-dir` with `gz-file` [[file-name]] without `.gz` extension.

   Creates `target-dir` dir(s) if necessary.
   The `gz-file` is not deleted.

   Options:
   * `:replace-existing` - when `true` overwrites existing file

   See also: [[gzip]]"
  ([gz-file] (gunzip gz-file nil))
  ([gz-file target-dir] (gunzip gz-file target-dir {}))
  ([gz-file target-dir {:keys [replace-existing]}]
   (let [dest-dir (or target-dir (parent gz-file) "")
         dest-filename (str/replace-first (file-name gz-file) #"\.gz$" "")
         gz-file (as-path gz-file)
         cp-opts (->copy-opts replace-existing nil nil nil)
         output-file (path dest-dir dest-filename)]
     (with-open
      [fis (Files/newInputStream gz-file (into-array java.nio.file.OpenOption []))
       gzis (GZIPInputStream. fis)]
       (when (parent output-file)
         (create-dirs (parent output-file)))
       (Files/copy ^java.io.InputStream gzis
                   output-file
                   cp-opts)))))

(defn gzip
  "Gzips `source-file` to `:dir`/`:out-file`.

  Does not store the `source-file` name in the `.gz` file.
  The `source-file` is not deleted.

  Options:
  * `:dir`(s) - created if necessary. If not specified, defaults to `source-file` dir.
  * `:out-file` - if not specified, defaults to `source-file` [[file-name]] with `.gz` extension.

  Returns the created gzip file.

  See also: [[gunzip]]"
  ([source-file]
   (gzip source-file {}))
  ([source-file {:keys [dir out-file]}]
   (assert source-file "source-file must be specified")
   (assert (exists? source-file) "source-file does not exist")
   (let [dest-dir (or dir (parent source-file) "")
         dest-filename (if (not out-file)
                         (str (file-name source-file) ".gz")
                         (str out-file))
         output-file (path dest-dir dest-filename)]
     (when (parent output-file)
       (create-dirs (parent output-file)))
     (with-open [source-input-stream (io/input-stream (file source-file))
                 gzos                (GZIPOutputStream.
                                      (FileOutputStream. (file output-file)))]
       (io/copy source-input-stream
                gzos))
     (str output-file))))

;;;; End gzip

(defmacro with-temp-dir
  "Evaluates body with `temp-dir` bound to the result of `(create-temp-dir
  opts)`.

  By default, the `temp-dir` will be removed with [[delete-tree]] on exit from the scope.

  Options:
  * see [[delete-tree]]
  * `:keep` - if `true` does not delete the directory on exit from macro scope. 
  
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
  {:arglists '[[[temp-dir] & body]
               [[temp-dir opts] & body]]}
  [[temp-dir opts & more] & body]
  {:pre [(empty? more) (symbol? temp-dir)]}
  `(let [opts# ~opts
         ~temp-dir (create-temp-dir opts#)]
     (try
       ~@body
       (finally
         (when-not (:keep opts#)
           (delete-tree ~temp-dir {:force true}))))))

(def ^:private cached-home-dir
  (delay (path (System/getProperty "user.home"))))

(def ^:private cached-users-dir
  (delay (parent @cached-home-dir)))

(defn home
  "Returns home dir path.

  With no arguments, returns the current value of the `user.home`
  system property as a path. If a `user` is passed, returns that user's home
  directory as found in the parent of home with no args."
  (^Path [] @cached-home-dir)
  (^Path [user] (if (empty? user) @cached-home-dir
                    (path @cached-users-dir user))))

(defn expand-home
  "Returns `path` replacing `~` (tilde) with home dir.

  If `path`:
  - does not start with `~`, returns `path`.
  - starts with `~` then [[file-separator]], `~` is replaced with `(home)`.
  e.g., `~/foo` -> `/home/myuser/foo` 
  - starts with `~` then some other chars, those other chars are
  assumed to be a username, then naively expanded to `(home username)`.
  e.g., `~someuser/foo` -> `/home/someuser/foo`  

  See also: [[home]]"
  ^Path [path]
  (let [p (as-path path)
        path-str (str p)]
    (if (.startsWith path-str "~")
      (let [sep (.indexOf path-str File/separator)]
        (if (neg? sep)
          (home (subs path-str 1))
          (path* (home (subs path-str 1 sep)) (subs path-str (inc sep)))))
      p)))

(defn windows?
  "Returns `true` if OS is Windows."
  []
  win?)

(defn cwd
  "Returns current working directory path."
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
  "Writes `bytes` to `file` via [Files/write](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#write(java.nio.file.Path,byte%5B%5D,java.nio.file.OpenOption...)).

  Options:
  * `:create` - (default `true`)
  * `:truncate-existing` - (default `true`)
  * `:write` - (default `true`)
  * `:append` - (default `false`)
  * or any `java.nio.file.StandardOption`.

  Examples:

  ``` clojure
  (fs/write-bytes f (.getBytes (String. \"foo\"))) ;; overwrites + truncates or creates new file
  (fs/write-bytes f (.getBytes (String. \"foo\")) {:append true})
  ```"
  ([file bytes] (write-bytes file bytes nil))
  ([file bytes {:keys [append
                       ;; default when no options are given:
                       create
                       truncate-existing
                       write] :as opts}]
   (let [path (as-path file)
         opts (->open-options opts)]
     (java.nio.file.Files/write path ^bytes bytes opts))))

(defn write-lines
  "Writes `lines`, a seqable of strings, to `file` via [Files/write](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#write(java.nio.file.Path,java.lang.Iterable,java.nio.charset.Charset,java.nio.file.OpenOption...)).

  Options:
  * `:charset` - (default `\"utf-8\"`)

  Open options:
  * `:create` (default `true`)
  * `:truncate-existing` (default `true`)
  * `:write` (default `true`)
  * `:append` (default `false`)
  * or any `java.nio.file.StandardOption`."
  ([file lines] (write-lines file lines nil))
  ([file lines {:keys [charset]
                :or {charset "utf-8"}
                :as opts}]
   (java.nio.file.Files/write (as-path file)
                              lines
                              (->charset charset)
                              (->open-options (dissoc opts :charset)))))

(defn update-file
  "Updates the contents of text `file` with result of applying function `f` with old contents and args `xs`.
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
         old-val (apply slurp (as-file file) opts)
         new-val (apply f old-val xs)]
     (apply spit (as-file file) new-val opts)
     new-val)))

(defn unixify
  "Returns `path` as string with Unix-style file separators (`/`)."
  [path]
  (let [s (str path)]
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
  "Returns path to user-specific configuration files as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Uses env-var `XDG_CONFIG_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) \".config\")`.
  When provided, appends `app` to the returned path."
  ([] (xdg-config-home nil))
  ([app]
   (cond-> (xdg-home-for :config)
     (seq app) (path app))))

(defn xdg-cache-home
  "Returns path to user-specific non-essential data as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Uses env-var `XDG_CACHE_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) \".cache\")`.
  When provided, appends `app` to the returned path."
  ([] (xdg-cache-home nil))
  ([app]
   (cond-> (xdg-home-for :cache)
     (seq app) (path app))))

(defn xdg-data-home
  "Returns path to user-specific data files as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Uses env-var `XDG_DATA_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) \".local\" \"share\")`.
  When provided, appends `app` to the returned path."
  ([] (xdg-data-home nil))
  ([app]
   (cond-> (xdg-home-for :data)
     (seq app) (path app))))

(defn xdg-state-home
  "Returns path to user-specific state files as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Uses env-var `XDG_STATE_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) \".local\" \"state\")`.
  When provided, appends `app` to the returned path."
  ([] (xdg-state-home nil))
  ([app]
   (cond-> (xdg-home-for :state)
     (seq app) (path app))))
