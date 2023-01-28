# Table of contents
-  [`babashka.fs`](#babashka.fs) 
    -  [`absolute?`](#babashka.fs/absolute?) - Returns true if f represents an absolute path.
    -  [`absolutize`](#babashka.fs/absolutize) - Converts f into an absolute path via Path#toAbsolutePath.
    -  [`canonicalize`](#babashka.fs/canonicalize) - Returns the canonical path via java.io.File#getCanonicalPath.
    -  [`components`](#babashka.fs/components) - Returns a seq of all components of f as paths, i.e.
    -  [`copy`](#babashka.fs/copy) - Copies src file to dest dir or file.
    -  [`copy-tree`](#babashka.fs/copy-tree) - Copies entire file tree from src to dest.
    -  [`create-dir`](#babashka.fs/create-dir) - Creates dir using <code>Files#createDirectory</code>.
    -  [`create-dirs`](#babashka.fs/create-dirs) - Creates directories using <code>Files#createDirectories</code>.
    -  [`create-file`](#babashka.fs/create-file) - Creates empty file using <code>Files#createFile</code>.
    -  [`create-link`](#babashka.fs/create-link) - Create a hard link from path to target.
    -  [`create-sym-link`](#babashka.fs/create-sym-link) - Create a soft link from path to target.
    -  [`create-temp-dir`](#babashka.fs/create-temp-dir) - Creates a temporary directory using Files#createDirectories.
    -  [`create-temp-file`](#babashka.fs/create-temp-file) - Creates an empty temporary file using Files#createTempFile.
    -  [`creation-time`](#babashka.fs/creation-time) - Returns creation time as FileTime.
    -  [`cwd`](#babashka.fs/cwd) - Returns current working directory as path.
    -  [`delete`](#babashka.fs/delete) - Deletes f.
    -  [`delete-if-exists`](#babashka.fs/delete-if-exists) - Deletes f if it exists.
    -  [`delete-on-exit`](#babashka.fs/delete-on-exit) - Requests delete on exit via <code>File#deleteOnExit</code>.
    -  [`delete-tree`](#babashka.fs/delete-tree) - Deletes a file tree using <code>walk-file-tree</code>.
    -  [`directory?`](#babashka.fs/directory?) - Returns true if f is a directory, using Files/isDirectory.
    -  [`ends-with?`](#babashka.fs/ends-with?) - Returns true if path this ends with path other.
    -  [`exec-paths`](#babashka.fs/exec-paths) - Returns executable paths (using the PATH environment variable).
    -  [`executable?`](#babashka.fs/executable?) - Returns true if f is executable.
    -  [`exists?`](#babashka.fs/exists?) - Returns true if f exists.
    -  [`expand-home`](#babashka.fs/expand-home) - If <code>path</code> begins with a tilde (<code>~</code>), expand the tilde to the value of the <code>user.home</code> system property.
    -  [`extension`](#babashka.fs/extension) - Returns the extension of a file via <code>split-ext</code>.
    -  [`file`](#babashka.fs/file) - Coerces f into a File.
    -  [`file-name`](#babashka.fs/file-name) - Returns the name of the file or directory.
    -  [`file-separator`](#babashka.fs/file-separator)
    -  [`file-time->instant`](#babashka.fs/file-time->instant) - Converts a java.nio.file.attribute.FileTime to a java.time.Instant.
    -  [`file-time->millis`](#babashka.fs/file-time->millis) - Converts a java.nio.file.attribute.FileTime to epoch millis (long).
    -  [`get-attribute`](#babashka.fs/get-attribute)
    -  [`glob`](#babashka.fs/glob) - Given a file and glob pattern, returns matches as vector of paths.
    -  [`hidden?`](#babashka.fs/hidden?) - Returns true if f is hidden.
    -  [`home`](#babashka.fs/home) - With no arguments, returns the current value of the <code>user.home</code> system property.
    -  [`instant->file-time`](#babashka.fs/instant->file-time) - Converts a java.time.Instant to a java.nio.file.attribute.FileTime.
    -  [`last-modified-time`](#babashka.fs/last-modified-time) - Returns last modified time as a java.nio.file.attribute.FileTime.
    -  [`list-dir`](#babashka.fs/list-dir) - Returns all paths in dir as vector.
    -  [`list-dirs`](#babashka.fs/list-dirs) - Similar to list-dir but accepts multiple roots and returns the concatenated results.
    -  [`match`](#babashka.fs/match) - Given a file and match pattern, returns matches as vector of paths.
    -  [`millis->file-time`](#babashka.fs/millis->file-time) - Converts epoch millis (long) to a java.nio.file.attribute.FileTime.
    -  [`modified-since`](#babashka.fs/modified-since) - Returns seq of regular files (non-directories, non-symlinks) from file-set that were modified since the anchor path.
    -  [`move`](#babashka.fs/move) - Move or rename a file to a target dir or file via <code>Files/move</code>.
    -  [`normalize`](#babashka.fs/normalize) - Normalizes f via Path#normalize.
    -  [`parent`](#babashka.fs/parent) - Returns parent of f, is it exists.
    -  [`path`](#babashka.fs/path) - Coerces f into a Path.
    -  [`path-separator`](#babashka.fs/path-separator)
    -  [`posix->str`](#babashka.fs/posix->str) - Converts a set of PosixFilePermission to a string.
    -  [`posix-file-permissions`](#babashka.fs/posix-file-permissions) - Gets f's posix file permissions.
    -  [`read-all-bytes`](#babashka.fs/read-all-bytes) - Returns contents of file as byte array.
    -  [`read-all-lines`](#babashka.fs/read-all-lines) - Read all lines from a file.
    -  [`read-attributes`](#babashka.fs/read-attributes) - Same as <code>read-attributes*</code> but turns attributes into a map and keywordizes keys.
    -  [`read-attributes*`](#babashka.fs/read-attributes*) - Reads attributes via Files/readAttributes.
    -  [`readable?`](#babashka.fs/readable?) - Returns true if f is readable.
    -  [`real-path`](#babashka.fs/real-path) - Converts f into real path via Path#toRealPath.
    -  [`regular-file?`](#babashka.fs/regular-file?) - Returns true if f is a regular file, using Files/isRegularFile.
    -  [`relative?`](#babashka.fs/relative?) - Returns true if f represents a relative path.
    -  [`relativize`](#babashka.fs/relativize) - Returns relative path by comparing this with other.
    -  [`same-file?`](#babashka.fs/same-file?) - Returns true if this is the same file as other.
    -  [`set-attribute`](#babashka.fs/set-attribute)
    -  [`set-creation-time`](#babashka.fs/set-creation-time) - Sets creation time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).
    -  [`set-last-modified-time`](#babashka.fs/set-last-modified-time) - Sets last modified time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).
    -  [`set-posix-file-permissions`](#babashka.fs/set-posix-file-permissions) - Sets posix file permissions on f.
    -  [`size`](#babashka.fs/size) - Returns the size of a file (in bytes).
    -  [`split-ext`](#babashka.fs/split-ext) - Splits path on extension If provided, a specific extension <code>ext</code>, the extension (without dot), will be used for splitting.
    -  [`split-paths`](#babashka.fs/split-paths) - Splits a string joined by the OS-specific path-seperator into a vec of paths.
    -  [`starts-with?`](#babashka.fs/starts-with?) - Returns true if path this starts with path other.
    -  [`str->posix`](#babashka.fs/str->posix) - Converts a string to a set of PosixFilePermission.
    -  [`strip-ext`](#babashka.fs/strip-ext) - Strips extension via <code>split-ext</code>.
    -  [`sym-link?`](#babashka.fs/sym-link?) - Determines if <code>f</code> is a symbolic link via <code>java.nio.file.Files/isSymbolicLink</code>.
    -  [`temp-dir`](#babashka.fs/temp-dir) - Returns <code>java.io.tmpdir</code> property as path.
    -  [`unixify`](#babashka.fs/unixify) - Returns path as string with Unix-style file separators (<code>/</code>).
    -  [`unzip`](#babashka.fs/unzip) - Unzips <code>zip-file</code> to <code>dest</code> directory (default <code>"."</code>).
    -  [`update-file`](#babashka.fs/update-file) - Updates the contents of text file <code>path</code> using <code>f</code> applied to old contents and <code>xs</code>.
    -  [`walk-file-tree`](#babashka.fs/walk-file-tree) - Walks f using Files/walkFileTree.
    -  [`which`](#babashka.fs/which) - Returns Path to first <code>program</code> found in (<code>exec-paths</code>), similar to the which Unix command.
    -  [`which-all`](#babashka.fs/which-all) - Returns every Path to <code>program</code> found in (<code>exec-paths</code>).
    -  [`windows?`](#babashka.fs/windows?) - Returns true if OS is Windows.
    -  [`with-temp-dir`](#babashka.fs/with-temp-dir) - Evaluate body with binding-name bound to a temporary directory.
    -  [`writable?`](#babashka.fs/writable?) - Returns true if f is writable.
    -  [`write-bytes`](#babashka.fs/write-bytes) - Writes <code>bytes</code> to <code>path</code> via <code>java.nio.file.Files/write</code>.
    -  [`write-lines`](#babashka.fs/write-lines) - Writes <code>lines</code>, a seqable of strings to <code>path</code> via <code>java.nio.file.Files/write</code>.
    -  [`xdg-cache-home`](#babashka.fs/xdg-cache-home) - Path representing the base directory relative to which user-specific non-essential data files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).
    -  [`xdg-config-home`](#babashka.fs/xdg-config-home) - Path representing the base directory relative to which user-specific configuration files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).
    -  [`xdg-data-home`](#babashka.fs/xdg-data-home) - Path representing the base directory relative to which user-specific data files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).
    -  [`xdg-state-home`](#babashka.fs/xdg-state-home) - Path representing the base directory relative to which user-specific state files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).
    -  [`zip`](#babashka.fs/zip) - Zips entry or entries into zip-file.

-----
# <a name="babashka.fs">babashka.fs</a>






## <a name="babashka.fs/absolute?">`absolute?`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L107-L109)
<a name="babashka.fs/absolute?"></a>
``` clojure

(absolute? f)
```


Returns true if f represents an absolute path.

## <a name="babashka.fs/absolutize">`absolutize`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L146-L148)
<a name="babashka.fs/absolutize"></a>
``` clojure

(absolutize f)
```


Converts f into an absolute path via Path#toAbsolutePath.

## <a name="babashka.fs/canonicalize">`canonicalize`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L160-L169)
<a name="babashka.fs/canonicalize"></a>
``` clojure

(canonicalize f)
(canonicalize f {:keys [:nofollow-links]})
```


Returns the canonical path via
  java.io.File#getCanonicalPath. If `:nofollow-links` is set, then it
  will fall back on [`absolutize`](#babashka.fs/absolutize) + `normalize.` This function can be used
  as an alternative to [`real-path`](#babashka.fs/real-path) which requires files to exist.

## <a name="babashka.fs/components">`components`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L140-L144)
<a name="babashka.fs/components"></a>
``` clojure

(components f)
```


Returns a seq of all components of f as paths, i.e. split on the file
  separator.

## <a name="babashka.fs/copy">`copy`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L350-L366)
<a name="babashka.fs/copy"></a>
``` clojure

(copy src dest)
(copy src dest {:keys [:replace-existing :copy-attributes :nofollow-links]})
```


Copies src file to dest dir or file.
  Options:
  * `:replace-existing`
  * `:copy-attributes`
  * `:nofollow-links` (used to determine to copy symbolic link itself or not).

## <a name="babashka.fs/copy-tree">`copy-tree`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L414-L452)
<a name="babashka.fs/copy-tree"></a>
``` clojure

(copy-tree src dest)
(copy-tree src dest {:keys [:replace-existing :copy-attributes :nofollow-links], :as opts})
```


Copies entire file tree from src to dest. Creates dest if needed
  using [`create-dirs`](#babashka.fs/create-dirs), passing it the `:posix-file-permissions`
  option. Supports same options as copy.

## <a name="babashka.fs/create-dir">`create-dir`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L399-L405)
<a name="babashka.fs/create-dir"></a>
``` clojure

(create-dir path)
(create-dir path {:keys [:posix-file-permissions]})
```


Creates dir using `Files#createDirectory`. Does not create parents.

## <a name="babashka.fs/create-dirs">`create-dirs`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L407-L412)
<a name="babashka.fs/create-dirs"></a>
``` clojure

(create-dirs path)
(create-dirs path {:keys [:posix-file-permissions]})
```


Creates directories using `Files#createDirectories`. Also creates parents if needed.
  Doesn't throw an exception if the the dirs exist already. Similar to mkdir -p

## <a name="babashka.fs/create-file">`create-file`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L557-L563)
<a name="babashka.fs/create-file"></a>
``` clojure

(create-file path)
(create-file path {:keys [:posix-file-permissions]})
```


Creates empty file using `Files#createFile`.

## <a name="babashka.fs/create-link">`create-link`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L519-L524)
<a name="babashka.fs/create-link"></a>
``` clojure

(create-link path target)
```


Create a hard link from path to target.

## <a name="babashka.fs/create-sym-link">`create-sym-link`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L511-L517)
<a name="babashka.fs/create-sym-link"></a>
``` clojure

(create-sym-link path target)
```


Create a soft link from path to target.

## <a name="babashka.fs/create-temp-dir">`create-temp-dir`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L459-L481)
<a name="babashka.fs/create-temp-dir"></a>
``` clojure

(create-temp-dir)
(create-temp-dir {:keys [:prefix :path :posix-file-permissions]})
```


Creates a temporary directory using Files#createDirectories.

  `(create-temp-dir)`: creates temp dir with random prefix.
  `(create-temp-dir {:keys [:prefix :path :posix-file-permissions]})`:

  create temp dir in path with prefix. If prefix is not provided, a random one
  is generated. If path is not provided, the directory is created as if called with `(create-temp-dir)`. The `:posix-file-permissions` option is a string like `"rwx------"`.

## <a name="babashka.fs/create-temp-file">`create-temp-file`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L483-L509)
<a name="babashka.fs/create-temp-file"></a>
``` clojure

(create-temp-file)
(create-temp-file {:keys [:path :prefix :suffix :posix-file-permissions]})
```


Creates an empty temporary file using Files#createTempFile.

  - `(create-temp-file)`: creates temp file with random prefix and suffix.
  - `(create-temp-dir {:keys [:prefix :suffix :path :posix-file-permissions]})`: create
  temp file in path with prefix. If prefix and suffix are not
  provided, random ones are generated. The `:posix-file-permissions`
  option is a string like `"rwx------"`.

## <a name="babashka.fs/creation-time">`creation-time`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L723-L728)
<a name="babashka.fs/creation-time"></a>
``` clojure

(creation-time f)
(creation-time f {:keys [nofollow-links], :as opts})
```


Returns creation time as FileTime.

## <a name="babashka.fs/cwd">`cwd`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1032-L1035)
<a name="babashka.fs/cwd"></a>
``` clojure

(cwd)
```


Returns current working directory as path

## <a name="babashka.fs/delete">`delete`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L526-L532)
<a name="babashka.fs/delete"></a>
``` clojure

(delete f)
```


Deletes f. Returns nil if the delete was successful,
  throws otherwise. Does not follow symlinks.

## <a name="babashka.fs/delete-if-exists">`delete-if-exists`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L534-L538)
<a name="babashka.fs/delete-if-exists"></a>
``` clojure

(delete-if-exists f)
```


Deletes f if it exists. Returns true if the delete was successful,
  false if f didn't exist. Does not follow symlinks.

## <a name="babashka.fs/delete-on-exit">`delete-on-exit`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L590-L594)
<a name="babashka.fs/delete-on-exit"></a>
``` clojure

(delete-on-exit f)
```


Requests delete on exit via `File#deleteOnExit`. Returns f.

## <a name="babashka.fs/delete-tree">`delete-tree`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L545-L555)
<a name="babashka.fs/delete-tree"></a>
``` clojure

(delete-tree root)
```


Deletes a file tree using [`walk-file-tree`](#babashka.fs/walk-file-tree). Similar to `rm -rf`. Does not follow symlinks.

## <a name="babashka.fs/directory?">`directory?`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L90-L95)
<a name="babashka.fs/directory?"></a>
``` clojure

(directory? f)
(directory? f {:keys [:nofollow-links]})
```


Returns true if f is a directory, using Files/isDirectory.

## <a name="babashka.fs/ends-with?">`ends-with?`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L855-L858)
<a name="babashka.fs/ends-with?"></a>
``` clojure

(ends-with? this other)
```


Returns true if path this ends with path other.

## <a name="babashka.fs/exec-paths">`exec-paths`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L780-L784)
<a name="babashka.fs/exec-paths"></a>
``` clojure

(exec-paths)
```


Returns executable paths (using the PATH environment variable). Same
  as `(split-paths (System/getenv "PATH"))`.

## <a name="babashka.fs/executable?">`executable?`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L111-L113)
<a name="babashka.fs/executable?"></a>
``` clojure

(executable? f)
```


Returns true if f is executable.

## <a name="babashka.fs/exists?">`exists?`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L127-L136)
<a name="babashka.fs/exists?"></a>
``` clojure

(exists? f)
(exists? f {:keys [:nofollow-links]})
```


Returns true if f exists.

## <a name="babashka.fs/expand-home">`expand-home`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1009-L1025)
<a name="babashka.fs/expand-home"></a>
``` clojure

(expand-home f)
```


If [[`path`](#babashka.fs/path)](#babashka.fs/path) begins with a tilde (`~`), expand the tilde to the value
  of the `user.home` system property. If the [[`path`](#babashka.fs/path)](#babashka.fs/path) begins with a
  tilde immediately followed by some characters, they are assumed to
  be a username. This is expanded to the path to that user's home
  directory. This is (naively) assumed to be a directory with the same
  name as the user relative to the parent of the current value of
  `user.home`.

## <a name="babashka.fs/extension">`extension`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L770-L773)
<a name="babashka.fs/extension"></a>
``` clojure

(extension path)
```


Returns the extension of a file via [`split-ext`](#babashka.fs/split-ext).

## <a name="babashka.fs/file">`file`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L61-L66)
<a name="babashka.fs/file"></a>
``` clojure

(file f)
(file f & fs)
```


Coerces f into a File. Multiple-arg versions treat the first argument
  as parent and subsequent args as children relative to the parent.

## <a name="babashka.fs/file-name">`file-name`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L171-L174)
<a name="babashka.fs/file-name"></a>
``` clojure

(file-name x)
```


Returns the name of the file or directory. E.g. (file-name "foo/bar/baz") returns "baz".

## <a name="babashka.fs/file-separator">`file-separator`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L242-L242)
<a name="babashka.fs/file-separator"></a>

## <a name="babashka.fs/file-time->instant">`file-time->instant`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L684-L687)
<a name="babashka.fs/file-time->instant"></a>
``` clojure

(file-time->instant ft)
```


Converts a java.nio.file.attribute.FileTime to a java.time.Instant.

## <a name="babashka.fs/file-time->millis">`file-time->millis`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L694-L697)
<a name="babashka.fs/file-time->millis"></a>
``` clojure

(file-time->millis ft)
```


Converts a java.nio.file.attribute.FileTime to epoch millis (long).

## <a name="babashka.fs/get-attribute">`get-attribute`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L634-L640)
<a name="babashka.fs/get-attribute"></a>
``` clojure

(get-attribute path attribute)
(get-attribute path attribute {:keys [:nofollow-links]})
```


## <a name="babashka.fs/glob">`glob`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L316-L339)
<a name="babashka.fs/glob"></a>
``` clojure

(glob root pattern)
(glob root pattern opts)
```


Given a file and glob pattern, returns matches as vector of
  paths. Patterns containing `**` or `/` will cause a recursive walk over
  path, unless overriden with :recursive. Glob interpretation is done
  using the rules described in
  https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String).

  Options:

  * `:hidden:` match hidden paths. Note: on Windows files starting with
  a dot are not hidden, unless their hidden attribute is set.
  * `:follow-links:` follow symlinks.
  * `:recursive:` force recursive search.

  Examples:
  `(fs/glob "." "**.clj")`

## <a name="babashka.fs/hidden?">`hidden?`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L103-L105)
<a name="babashka.fs/hidden?"></a>
``` clojure

(hidden? f)
```


Returns true if f is hidden.

## <a name="babashka.fs/home">`home`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1001-L1007)
<a name="babashka.fs/home"></a>
``` clojure

(home)
(home user)
```


With no arguments, returns the current value of the `user.home`
  system property. If a `user` is passed, returns that user's home
  directory as found in the parent of home with no args.

## <a name="babashka.fs/instant->file-time">`instant->file-time`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L689-L692)
<a name="babashka.fs/instant->file-time"></a>
``` clojure

(instant->file-time instant)
```


Converts a java.time.Instant to a java.nio.file.attribute.FileTime.

## <a name="babashka.fs/last-modified-time">`last-modified-time`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L709-L714)
<a name="babashka.fs/last-modified-time"></a>
``` clojure

(last-modified-time f)
(last-modified-time f {:keys [nofollow-links], :as opts})
```


Returns last modified time as a java.nio.file.attribute.FileTime.

## <a name="babashka.fs/list-dir">`list-dir`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L232-L240)
<a name="babashka.fs/list-dir"></a>
``` clojure

(list-dir dir)
(list-dir dir glob-or-accept)
```


Returns all paths in dir as vector. For descending into subdirectories use `glob.`
     - `glob-or-accept` - a glob string such as "*.edn" or a (fn accept [^java.nio.file.Path p]) -> truthy

## <a name="babashka.fs/list-dirs">`list-dirs`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L737-L741)
<a name="babashka.fs/list-dirs"></a>
``` clojure

(list-dirs dirs glob-or-accept)
```


Similar to list-dir but accepts multiple roots and returns the concatenated results.
  - `glob-or-accept` - a glob string such as "*.edn" or a (fn accept [^java.nio.file.Path p]) -> truthy

## <a name="babashka.fs/match">`match`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L250-L314)
<a name="babashka.fs/match"></a>
``` clojure

(match root pattern)
(match root pattern {:keys [hidden follow-links max-depth recursive]})
```


Given a file and match pattern, returns matches as vector of
  paths. Pattern interpretation is done using the rules described in
  https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String).

  Options:

  * `:hidden:` match hidden paths - note: on Windows paths starting with
  a dot are not hidden, unless their hidden attribute is set.
  * `:follow-links:` - follow symlinks
  * `:recursive:` - match recursively.
  * `:max-depth:` - max depth to descend into directory structure.

  Examples:
  `(fs/match "." "regex:.*\\.clj" {:recursive true})`

## <a name="babashka.fs/millis->file-time">`millis->file-time`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L699-L702)
<a name="babashka.fs/millis->file-time"></a>
``` clojure

(millis->file-time millis)
```


Converts epoch millis (long) to a java.nio.file.attribute.FileTime.

## <a name="babashka.fs/modified-since">`modified-since`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L887-L896)
<a name="babashka.fs/modified-since"></a>
``` clojure

(modified-since anchor file-set)
```


Returns seq of regular files (non-directories, non-symlinks) from file-set that were modified since the anchor path.
  The anchor path can be a regular file or directory, in which case
  the recursive max last modified time stamp is used as the timestamp
  to compare with.  The file-set may be a regular file, directory or
  collection of files (e.g. returned by glob). Directories are
  searched recursively.

## <a name="babashka.fs/move">`move`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L565-L578)
<a name="babashka.fs/move"></a>
``` clojure

(move source target)
(move source target {:keys [:replace-existing :atomic-move :nofollow-links]})
```


Move or rename a file to a target dir or file via `Files/move`.

## <a name="babashka.fs/normalize">`normalize`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L155-L158)
<a name="babashka.fs/normalize"></a>
``` clojure

(normalize f)
```


Normalizes f via Path#normalize.

## <a name="babashka.fs/parent">`parent`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L580-L583)
<a name="babashka.fs/parent"></a>
``` clojure

(parent f)
```


Returns parent of f, is it exists. Akin to `dirname` in bash.

## <a name="babashka.fs/path">`path`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L51-L59)
<a name="babashka.fs/path"></a>
``` clojure

(path f)
(path parent child)
(path parent child & more)
```


Coerces f into a Path. Multiple-arg versions treat the first argument as
  parent and subsequent args as children relative to the parent.

## <a name="babashka.fs/path-separator">`path-separator`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L243-L243)
<a name="babashka.fs/path-separator"></a>

## <a name="babashka.fs/posix->str">`posix->str`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L368-L371)
<a name="babashka.fs/posix->str"></a>
``` clojure

(posix->str p)
```


Converts a set of PosixFilePermission to a string.

## <a name="babashka.fs/posix-file-permissions">`posix-file-permissions`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L601-L605)
<a name="babashka.fs/posix-file-permissions"></a>
``` clojure

(posix-file-permissions f)
(posix-file-permissions f {:keys [:nofollow-links]})
```


Gets f's posix file permissions. Use posix->str to view as a string.

## <a name="babashka.fs/read-all-bytes">`read-all-bytes`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L612-L615)
<a name="babashka.fs/read-all-bytes"></a>
``` clojure

(read-all-bytes f)
```


Returns contents of file as byte array.

## <a name="babashka.fs/read-all-lines">`read-all-lines`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L622-L630)
<a name="babashka.fs/read-all-lines"></a>
``` clojure

(read-all-lines f)
(read-all-lines f {:keys [charset], :or {charset "utf-8"}})
```


Read all lines from a file.

## <a name="babashka.fs/read-attributes">`read-attributes`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L665-L673)
<a name="babashka.fs/read-attributes"></a>
``` clojure

(read-attributes path attributes)
(read-attributes path attributes {:keys [:nofollow-links :key-fn], :as opts})
```


Same as [`read-attributes*`](#babashka.fs/read-attributes*) but turns attributes into a map and keywordizes keys.
  Keywordizing can be changed by passing a :key-fn in the options map.

## <a name="babashka.fs/read-attributes*">`read-attributes*`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L647-L663)
<a name="babashka.fs/read-attributes*"></a>
``` clojure

(read-attributes* path attributes)
(read-attributes* path attributes {:keys [:nofollow-links]})
```


Reads attributes via Files/readAttributes.

## <a name="babashka.fs/readable?">`readable?`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L115-L117)
<a name="babashka.fs/readable?"></a>
``` clojure

(readable? f)
```


Returns true if f is readable

## <a name="babashka.fs/real-path">`real-path`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L75-L79)
<a name="babashka.fs/real-path"></a>
``` clojure

(real-path f)
(real-path f {:keys [:nofollow-links]})
```


Converts f into real path via Path#toRealPath.

## <a name="babashka.fs/regular-file?">`regular-file?`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L83-L88)
<a name="babashka.fs/regular-file?"></a>
``` clojure

(regular-file? f)
(regular-file? f {:keys [:nofollow-links]})
```


Returns true if f is a regular file, using Files/isRegularFile.

## <a name="babashka.fs/relative?">`relative?`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L123-L125)
<a name="babashka.fs/relative?"></a>
``` clojure

(relative? f)
```


Returns true if f represents a relative path.

## <a name="babashka.fs/relativize">`relativize`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L150-L153)
<a name="babashka.fs/relativize"></a>
``` clojure

(relativize this other)
```


Returns relative path by comparing this with other.

## <a name="babashka.fs/same-file?">`same-file?`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L607-L610)
<a name="babashka.fs/same-file?"></a>
``` clojure

(same-file? this other)
```


Returns true if this is the same file as other.

## <a name="babashka.fs/set-attribute">`set-attribute`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L675-L682)
<a name="babashka.fs/set-attribute"></a>
``` clojure

(set-attribute path attribute value)
(set-attribute path attribute value {:keys [:nofollow-links]})
```


## <a name="babashka.fs/set-creation-time">`set-creation-time`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L730-L735)
<a name="babashka.fs/set-creation-time"></a>
``` clojure

(set-creation-time f time)
(set-creation-time f time {:keys [nofollow-links], :as opts})
```


Sets creation time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).

## <a name="babashka.fs/set-last-modified-time">`set-last-modified-time`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L716-L721)
<a name="babashka.fs/set-last-modified-time"></a>
``` clojure

(set-last-modified-time f time)
(set-last-modified-time f time {:keys [nofollow-links], :as opts})
```


Sets last modified time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).

## <a name="babashka.fs/set-posix-file-permissions">`set-posix-file-permissions`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L596-L599)
<a name="babashka.fs/set-posix-file-permissions"></a>
``` clojure

(set-posix-file-permissions f posix-file-permissions)
```


Sets posix file permissions on f. Accepts a string like `"rwx------"` or a set of PosixFilePermission.

## <a name="babashka.fs/size">`size`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L585-L588)
<a name="babashka.fs/size"></a>
``` clojure

(size f)
```


Returns the size of a file (in bytes).

## <a name="babashka.fs/split-ext">`split-ext`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L743-L761)
<a name="babashka.fs/split-ext"></a>
``` clojure

(split-ext path)
(split-ext path {:keys [ext]})
```


Splits path on extension If provided, a specific extension `ext`, the
  extension (without dot), will be used for splitting.  Directories
  are not processed.

## <a name="babashka.fs/split-paths">`split-paths`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L775-L778)
<a name="babashka.fs/split-paths"></a>
``` clojure

(split-paths joined-paths)
```


Splits a string joined by the OS-specific path-seperator into a vec of paths.

## <a name="babashka.fs/starts-with?">`starts-with?`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L850-L853)
<a name="babashka.fs/starts-with?"></a>
``` clojure

(starts-with? this other)
```


Returns true if path this starts with path other.

## <a name="babashka.fs/str->posix">`str->posix`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L373-L376)
<a name="babashka.fs/str->posix"></a>
``` clojure

(str->posix s)
```


Converts a string to a set of PosixFilePermission.

## <a name="babashka.fs/strip-ext">`strip-ext`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L763-L768)
<a name="babashka.fs/strip-ext"></a>
``` clojure

(strip-ext path)
(strip-ext path {:keys [ext], :as opts})
```


Strips extension via [`split-ext`](#babashka.fs/split-ext).

## <a name="babashka.fs/sym-link?">`sym-link?`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L540-L543)
<a name="babashka.fs/sym-link?"></a>
``` clojure

(sym-link? f)
```


Determines if `f` is a symbolic link via `java.nio.file.Files/isSymbolicLink`.

## <a name="babashka.fs/temp-dir">`temp-dir`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L454-L457)
<a name="babashka.fs/temp-dir"></a>
``` clojure

(temp-dir)
```


Returns `java.io.tmpdir` property as path.

## <a name="babashka.fs/unixify">`unixify`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1125-L1130)
<a name="babashka.fs/unixify"></a>
``` clojure

(unixify f)
```


Returns path as string with Unix-style file separators (`/`).

## <a name="babashka.fs/unzip">`unzip`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L902-L929)
<a name="babashka.fs/unzip"></a>
``` clojure

(unzip zip-file)
(unzip zip-file dest)
(unzip zip-file dest {:keys [replace-existing]})
```


Unzips `zip-file` to `dest` directory (default `"."`).

   Options:
   * `:replace-existing` - `true` / `false`: overwrite existing files

## <a name="babashka.fs/update-file">`update-file`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1105-L1123)
<a name="babashka.fs/update-file"></a>
``` clojure

(update-file file f & xs)
(update-file file opts f & xs)
```


Updates the contents of text file [`path`](#babashka.fs/path) using `f` applied to old contents and `xs`.
  Returns the new contents.

  Options:

  * `:charset` - charset of file, default to "utf-8"

## <a name="babashka.fs/walk-file-tree">`walk-file-tree`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L178-L212)
<a name="babashka.fs/walk-file-tree"></a>
``` clojure

(walk-file-tree f {:keys [:pre-visit-dir :post-visit-dir :visit-file :visit-file-failed :follow-links :max-depth]})
```


Walks f using Files/walkFileTree. Visitor functions: :pre-visit-dir,
  :post-visit-dir, :visit-file, :visit-file-failed. All visitor functions
  default to (constantly :continue). Supported return
  values: :continue, :skip-subtree, :skip-siblings, :terminate. A
  different return value will throw.

## <a name="babashka.fs/which">`which`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L793-L836)
<a name="babashka.fs/which"></a>
``` clojure

(which program)
(which program opts)
```


Returns Path to first `program` found in ([`exec-paths`](#babashka.fs/exec-paths)), similar to the which Unix command.

  On Windows, also searches for `program` with filename extensions specified in `:win-exts` `opt`.
  Default is `["com" "exe" "bat" "cmd"]`.
  If `program` already includes an extension from `:win-exts`, it will be searched as-is first.

## <a name="babashka.fs/which-all">`which-all`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L838-L842)
<a name="babashka.fs/which-all"></a>
``` clojure

(which-all program)
(which-all program opts)
```


Returns every Path to `program` found in ([`exec-paths`](#babashka.fs/exec-paths)). See [`which`](#babashka.fs/which).

## <a name="babashka.fs/windows?">`windows?`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1027-L1030)
<a name="babashka.fs/windows?"></a>
``` clojure

(windows?)
```


Returns true if OS is Windows.

## <a name="babashka.fs/with-temp-dir">`with-temp-dir`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L979-L993)
<a name="babashka.fs/with-temp-dir"></a>
``` clojure

(with-temp-dir [binding-name options] & body)
```


Macro.


Evaluate body with binding-name bound to a temporary directory.

  The directory is created by passing `options` to [`create-temp-dir`](#babashka.fs/create-temp-dir),
  and will be removed with [`delete-tree`](#babashka.fs/delete-tree) on exit from the scope.

  `options` is a map with the keys as for create-temp-dir.

## <a name="babashka.fs/writable?">`writable?`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L119-L121)
<a name="babashka.fs/writable?"></a>
``` clojure

(writable? f)
```


Returns true if f is writable

## <a name="babashka.fs/write-bytes">`write-bytes`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1059-L1082)
<a name="babashka.fs/write-bytes"></a>
``` clojure

(write-bytes path bytes)
(write-bytes path bytes {:keys [append create truncate-existing write], :as opts})
```


Writes `bytes` to [`path`](#babashka.fs/path) via `java.nio.file.Files/write`.
  Supported options:
  * `:create` (default `true`)
  * `:truncate-existing` (default `true`)
  * `:write` (default `true`)
  * `:append` (default `false`)
  * or any `java.nio.file.StandardOption`.

  Examples:

  ``` clojure
  (fs/write-bytes f (.getBytes (String. "foo"))) ;; overwrites + truncates or creates new file
  (fs/write-bytes f (.getBytes (String. "foo")) {:append true})
  ```

## <a name="babashka.fs/write-lines">`write-lines`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1084-L1103)
<a name="babashka.fs/write-lines"></a>
``` clojure

(write-lines path lines)
(write-lines path lines {:keys [charset], :or {charset "utf-8"}, :as opts})
```


Writes `lines`, a seqable of strings to [`path`](#babashka.fs/path) via `java.nio.file.Files/write`.

  Supported options:
  * `:charset` (default `"utf-8"`)

  Supported open options:
  * `:create` (default `true`)
  * `:truncate-existing` (default `true`)
  * `:write` (default `true`)
  * `:append` (default `false`)
  * or any `java.nio.file.StandardOption`.

## <a name="babashka.fs/xdg-cache-home">`xdg-cache-home`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1159-L1164)
<a name="babashka.fs/xdg-cache-home"></a>
``` clojure

(xdg-cache-home)
```


Path representing the base directory relative to which user-specific non-essential data files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_CACHE_HOME` (if set), else `(fs/path (fs/home) ".cache")`. 

## <a name="babashka.fs/xdg-config-home">`xdg-config-home`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1152-L1157)
<a name="babashka.fs/xdg-config-home"></a>
``` clojure

(xdg-config-home)
```


Path representing the base directory relative to which user-specific configuration files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_CONFIG_HOME` (if set), else `(fs/path (fs/home) ".config")`. 

## <a name="babashka.fs/xdg-data-home">`xdg-data-home`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1166-L1171)
<a name="babashka.fs/xdg-data-home"></a>
``` clojure

(xdg-data-home)
```


Path representing the base directory relative to which user-specific data files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_DATA_HOME` (if set), else `(fs/path (fs/home) ".local" "share")`.

## <a name="babashka.fs/xdg-state-home">`xdg-state-home`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1173-L1178)
<a name="babashka.fs/xdg-state-home"></a>
``` clojure

(xdg-state-home)
```


Path representing the base directory relative to which user-specific state files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_STATE_HOME` (if set), else `(fs/path (fs/home) ".local" "state")`.

## <a name="babashka.fs/zip">`zip`</a> [:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L960-L975)
<a name="babashka.fs/zip"></a>
``` clojure

(zip zip-file entries)
(zip zip-file entries _opts)
```


Zips entry or entries into zip-file. An entry may be a file or
  directory. Directories are included recursively and their names are
  preserved in the zip file. Currently only accepts relative entries.
