# Table of contents
-  [`babashka.fs`](#babashka.fs) 
    -  [`absolute?`](#babashka.fs/absolute?) - Returns true if f represents an absolute path.
    -  [`absolutize`](#babashka.fs/absolutize) - Converts f into an absolute path via Path#toAbsolutePath.
    -  [`canonicalize`](#babashka.fs/canonicalize) - Returns the canonical path via java.io.File#getCanonicalPath.
    -  [`components`](#babashka.fs/components) - Returns a seq of all components of f.
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
    -  [`extension`](#babashka.fs/extension) - Returns the extension of a file.
    -  [`file`](#babashka.fs/file) - Coerces f into a File.
    -  [`file-name`](#babashka.fs/file-name) - Returns the name of the file or directory.
    -  [`file-separator`](#babashka.fs/file-separator)
    -  [`file-time->instant`](#babashka.fs/file-time->instant) - Converts a java.nio.file.attribute.FileTime to a java.time.Instant.
    -  [`file-time->millis`](#babashka.fs/file-time->millis) - Converts a java.nio.file.attribute.FileTime to epoch millis (long).
    -  [`get-attribute`](#babashka.fs/get-attribute)
    -  [`glob`](#babashka.fs/glob) - Given a file and glob pattern, returns matches as vector of files.
    -  [`hidden?`](#babashka.fs/hidden?) - Returns true if f is hidden.
    -  [`home`](#babashka.fs/home) - With no arguments, returns the current value of the <code>user.home</code> system property.
    -  [`instant->file-time`](#babashka.fs/instant->file-time) - Converts a java.time.Instant to a java.nio.file.attribute.FileTime.
    -  [`last-modified-time`](#babashka.fs/last-modified-time) - Returns last modified time as a java.nio.file.attribute.FileTime.
    -  [`list-dir`](#babashka.fs/list-dir) - Returns all paths in dir as vector.
    -  [`list-dirs`](#babashka.fs/list-dirs) - Similar to list-dir but accepts multiple roots and returns the concatenated results.
    -  [`match`](#babashka.fs/match) - Given a file and match pattern, returns matches as vector of files.
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
    -  [`split-ext`](#babashka.fs/split-ext) - Splits a path into a vec of [path-without-ext ext].
    -  [`split-paths`](#babashka.fs/split-paths) - Splits a string joined by the OS-specific path-seperator into a vec of paths.
    -  [`starts-with?`](#babashka.fs/starts-with?) - Returns true if path this starts with path other.
    -  [`str->posix`](#babashka.fs/str->posix) - Converts a string to a set of PosixFilePermission.
    -  [`strip-ext`](#babashka.fs/strip-ext) - Returns the path with the extension removed.
    -  [`sym-link?`](#babashka.fs/sym-link?)
    -  [`temp-dir`](#babashka.fs/temp-dir) - Returns <code>java.io.tmpdir</code> property as path.
    -  [`unzip`](#babashka.fs/unzip) - Unzips <code>zip-file</code> to <code>dest</code> directory (default <code>"."</code>).
    -  [`walk-file-tree`](#babashka.fs/walk-file-tree) - Walks f using Files/walkFileTree.
    -  [`which`](#babashka.fs/which) - Returns Path to first <code>program</code> found in (<code>exec-paths</code>), similar to the which Unix command.
    -  [`which-all`](#babashka.fs/which-all) - Returns every Path to <code>program</code> found in (<code>exec-paths</code>).
    -  [`windows?`](#babashka.fs/windows?) - Returns true if OS is Windows.
    -  [`with-temp-dir`](#babashka.fs/with-temp-dir) - Evaluate body with binding-name bound to a temporary directory.
    -  [`writable?`](#babashka.fs/writable?) - Returns true if f is writable.
    -  [`write-bytes`](#babashka.fs/write-bytes) - Writes <code>bytes</code> to <code>path</code> via <code>java.nio.file.Files/write</code>.
    -  [`write-lines`](#babashka.fs/write-lines) - Writes <code>lines</code>, a seqable of strings to <code>path</code> via <code>java.nio.file.Files/write</code>.
    -  [`zip`](#babashka.fs/zip) - Zips entry or entries into zip-file.

-----
# <a name="babashka.fs">babashka.fs</a>






## <a name="babashka.fs/absolute?">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L104-L106) `absolute?`</a>
``` clojure

(absolute? f)
```


Returns true if f represents an absolute path.

## <a name="babashka.fs/absolutize">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L142-L144) `absolutize`</a>
``` clojure

(absolutize f)
```


Converts f into an absolute path via Path#toAbsolutePath.

## <a name="babashka.fs/canonicalize">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L156-L165) `canonicalize`</a>
``` clojure

(canonicalize f)
(canonicalize f {:keys [:nofollow-links]})
```


Returns the canonical path via
  java.io.File#getCanonicalPath. If `:nofollow-links` is set, then it
  will fall back on [`absolutize`](#babashka.fs/absolutize) + `normalize.` This function can be used
  as an alternative to [`real-path`](#babashka.fs/real-path) which requires files to exist.

## <a name="babashka.fs/components">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L137-L140) `components`</a>
``` clojure

(components f)
```


Returns a seq of all components of f.

## <a name="babashka.fs/copy">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L346-L362) `copy`</a>
``` clojure

(copy src dest)
(copy src dest {:keys [:replace-existing :copy-attributes :nofollow-links]})
```


Copies src file to dest dir or file.
  Options:
  * `:replace-existing`
  * `:copy-attributes`
  * `:nofollow-links` (used to determine to copy symbolic link itself or not).

## <a name="babashka.fs/copy-tree">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L409-L447) `copy-tree`</a>
``` clojure

(copy-tree src dest)
(copy-tree src dest {:keys [:replace-existing :copy-attributes :nofollow-links], :as opts})
```


Copies entire file tree from src to dest. Creates dest if needed
  using [`create-dirs`](#babashka.fs/create-dirs), passing it the `:posix-file-permissions`
  option. Supports same options as copy.

## <a name="babashka.fs/create-dir">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L395-L401) `create-dir`</a>
``` clojure

(create-dir path)
(create-dir path {:keys [:posix-file-permissions]})
```


Creates dir using `Files#createDirectory`. Does not create parents.

## <a name="babashka.fs/create-dirs">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L403-L407) `create-dirs`</a>
``` clojure

(create-dirs path)
(create-dirs path {:keys [:posix-file-permissions]})
```


Creates directories using `Files#createDirectories`. Also creates parents if needed.

## <a name="babashka.fs/create-file">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L550-L556) `create-file`</a>
``` clojure

(create-file path)
(create-file path {:keys [:posix-file-permissions]})
```


Creates empty file using `Files#createFile`.

## <a name="babashka.fs/create-link">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L514-L519) `create-link`</a>
``` clojure

(create-link path target)
```


Create a hard link from path to target.

## <a name="babashka.fs/create-sym-link">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L506-L512) `create-sym-link`</a>
``` clojure

(create-sym-link path target)
```


Create a soft link from path to target.

## <a name="babashka.fs/create-temp-dir">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L454-L476) `create-temp-dir`</a>
``` clojure

(create-temp-dir)
(create-temp-dir {:keys [:prefix :path :posix-file-permissions]})
```


Creates a temporary directory using Files#createDirectories.

  `(create-temp-dir)`: creates temp dir with random prefix.
  `(create-temp-dir {:keys [:prefix :path :posix-file-permissions]})`:

  create temp dir in path with prefix. If prefix is not provided, a random one
  is generated. If path is not provided, the directory is created as if called with `(create-temp-dir)`. The `:posix-file-permissions` option is a string like `"rwx------"`.

## <a name="babashka.fs/create-temp-file">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L478-L504) `create-temp-file`</a>
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

## <a name="babashka.fs/creation-time">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L716-L721) `creation-time`</a>
``` clojure

(creation-time f)
(creation-time f {:keys [nofollow-links], :as opts})
```


Returns creation time as FileTime.

## <a name="babashka.fs/cwd">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1025-L1028) `cwd`</a>
``` clojure

(cwd)
```


Returns current working directory as path

## <a name="babashka.fs/delete">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L521-L527) `delete`</a>
``` clojure

(delete f)
```


Deletes f. Returns nil if the delete was successful,
  throws otherwise. Does not follow symlinks.

## <a name="babashka.fs/delete-if-exists">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L529-L533) `delete-if-exists`</a>
``` clojure

(delete-if-exists f)
```


Deletes f if it exists. Returns true if the delete was successful,
  false if f didn't exist. Does not follow symlinks.

## <a name="babashka.fs/delete-on-exit">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L583-L587) `delete-on-exit`</a>
``` clojure

(delete-on-exit f)
```


Requests delete on exit via `File#deleteOnExit`. Returns f.

## <a name="babashka.fs/delete-tree">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L538-L548) `delete-tree`</a>
``` clojure

(delete-tree root)
```


Deletes a file tree using [`walk-file-tree`](#babashka.fs/walk-file-tree). Similar to `rm -rf`. Does not follow symlinks.

## <a name="babashka.fs/directory?">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L87-L92) `directory?`</a>
``` clojure

(directory? f)
(directory? f {:keys [:nofollow-links]})
```


Returns true if f is a directory, using Files/isDirectory.

## <a name="babashka.fs/ends-with?">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L848-L851) `ends-with?`</a>
``` clojure

(ends-with? this other)
```


Returns true if path this ends with path other.

## <a name="babashka.fs/exec-paths">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L773-L777) `exec-paths`</a>
``` clojure

(exec-paths)
```


Returns executable paths (using the PATH environment variable). Same
  as `(split-paths (System/getenv "PATH"))`.

## <a name="babashka.fs/executable?">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L108-L110) `executable?`</a>
``` clojure

(executable? f)
```


Returns true if f is executable.

## <a name="babashka.fs/exists?">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L124-L133) `exists?`</a>
``` clojure

(exists? f)
(exists? f {:keys [:nofollow-links]})
```


Returns true if f exists.

## <a name="babashka.fs/expand-home">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1002-L1018) `expand-home`</a>
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

## <a name="babashka.fs/extension">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L763-L766) `extension`</a>
``` clojure

(extension path)
```


Returns the extension of a file

## <a name="babashka.fs/file">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L58-L63) `file`</a>
``` clojure

(file f)
(file f & fs)
```


Coerces f into a File. Multiple-arg versions treat the first argument
  as parent and subsequent args as children relative to the parent.

## <a name="babashka.fs/file-name">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L167-L170) `file-name`</a>
``` clojure

(file-name x)
```


Returns the name of the file or directory. E.g. (file-name "foo/bar/baz") returns "baz".

## <a name="babashka.fs/file-separator">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L238-L238) `file-separator`</a>

## <a name="babashka.fs/file-time->instant">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L677-L680) `file-time->instant`</a>
``` clojure

(file-time->instant ft)
```


Converts a java.nio.file.attribute.FileTime to a java.time.Instant.

## <a name="babashka.fs/file-time->millis">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L687-L690) `file-time->millis`</a>
``` clojure

(file-time->millis ft)
```


Converts a java.nio.file.attribute.FileTime to epoch millis (long).

## <a name="babashka.fs/get-attribute">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L627-L633) `get-attribute`</a>
``` clojure

(get-attribute path attribute)
(get-attribute path attribute {:keys [:nofollow-links]})
```


## <a name="babashka.fs/glob">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L312-L335) `glob`</a>
``` clojure

(glob root pattern)
(glob root pattern opts)
```


Given a file and glob pattern, returns matches as vector of
  files. Patterns containing `**` or `/` will cause a recursive walk over
  path, unless overriden with :recursive. Glob interpretation is done
  using the rules described in
  https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String).

  Options:

  * `:hidden:` match hidden files. Note: on Windows files starting with
  a dot are not hidden, unless their hidden attribute is set.
  * `:follow-links:` follow symlinks.
  * `:recursive:` force recursive search.

  Examples:
  `(fs/glob "." "**.clj")`

## <a name="babashka.fs/hidden?">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L100-L102) `hidden?`</a>
``` clojure

(hidden? f)
```


Returns true if f is hidden.

## <a name="babashka.fs/home">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L994-L1000) `home`</a>
``` clojure

(home)
(home user)
```


With no arguments, returns the current value of the `user.home`
  system property. If a `user` is passed, returns that user's home
  directory as found in the parent of home with no args.

## <a name="babashka.fs/instant->file-time">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L682-L685) `instant->file-time`</a>
``` clojure

(instant->file-time instant)
```


Converts a java.time.Instant to a java.nio.file.attribute.FileTime.

## <a name="babashka.fs/last-modified-time">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L702-L707) `last-modified-time`</a>
``` clojure

(last-modified-time f)
(last-modified-time f {:keys [nofollow-links], :as opts})
```


Returns last modified time as a java.nio.file.attribute.FileTime.

## <a name="babashka.fs/list-dir">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L228-L236) `list-dir`</a>
``` clojure

(list-dir dir)
(list-dir dir glob-or-accept)
```


Returns all paths in dir as vector. For descending into subdirectories use `glob.`
     - `glob-or-accept` - a glob string such as "*.edn" or a (fn accept [^java.nio.file.Path p]) -> truthy

## <a name="babashka.fs/list-dirs">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L730-L734) `list-dirs`</a>
``` clojure

(list-dirs dirs glob-or-accept)
```


Similar to list-dir but accepts multiple roots and returns the concatenated results.
  - `glob-or-accept` - a glob string such as "*.edn" or a (fn accept [^java.nio.file.Path p]) -> truthy

## <a name="babashka.fs/match">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L246-L310) `match`</a>
``` clojure

(match root pattern)
(match root pattern {:keys [hidden follow-links max-depth recursive]})
```


Given a file and match pattern, returns matches as vector of
  files. Pattern interpretation is done using the rules described in
  https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String).

  Options:

  * `:hidden:` match hidden files - note: on Windows files starting with
  a dot are not hidden, unless their hidden attribute is set.
  * `:follow-links:` - follow symlinks
  * `:recursive:` - match recursively.
  * `:max-depth:` - max depth to descend into directory structure.

  Examples:
  `(fs/match "." "regex:.*\\.clj" {:recursive true})`

## <a name="babashka.fs/millis->file-time">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L692-L695) `millis->file-time`</a>
``` clojure

(millis->file-time millis)
```


Converts epoch millis (long) to a java.nio.file.attribute.FileTime.

## <a name="babashka.fs/modified-since">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L880-L889) `modified-since`</a>
``` clojure

(modified-since anchor file-set)
```


Returns seq of regular files (non-directories, non-symlinks) from file-set that were modified since the anchor path.
  The anchor path can be a regular file or directory, in which case
  the recursive max last modified time stamp is used as the timestamp
  to compare with.  The file-set may be a regular file, directory or
  collection of files (e.g. returned by glob). Directories are
  searched recursively.

## <a name="babashka.fs/move">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L558-L571) `move`</a>
``` clojure

(move source target)
(move source target {:keys [:replace-existing :atomic-move :nofollow-links]})
```


Move or rename a file to a target dir or file via `Files/move`.

## <a name="babashka.fs/normalize">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L151-L154) `normalize`</a>
``` clojure

(normalize f)
```


Normalizes f via Path#normalize.

## <a name="babashka.fs/parent">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L573-L576) `parent`</a>
``` clojure

(parent f)
```


Returns parent of f, is it exists.

## <a name="babashka.fs/path">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L48-L56) `path`</a>
``` clojure

(path f)
(path parent child)
(path parent child & more)
```


Coerces f into a Path. Multiple-arg versions treat the first argument as
  parent and subsequent args as children relative to the parent.

## <a name="babashka.fs/path-separator">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L239-L239) `path-separator`</a>

## <a name="babashka.fs/posix->str">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L364-L367) `posix->str`</a>
``` clojure

(posix->str p)
```


Converts a set of PosixFilePermission to a string.

## <a name="babashka.fs/posix-file-permissions">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L594-L598) `posix-file-permissions`</a>
``` clojure

(posix-file-permissions f)
(posix-file-permissions f {:keys [:nofollow-links]})
```


Gets f's posix file permissions. Use posix->str to view as a string.

## <a name="babashka.fs/read-all-bytes">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L605-L608) `read-all-bytes`</a>
``` clojure

(read-all-bytes f)
```


Returns contents of file as byte array.

## <a name="babashka.fs/read-all-lines">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L615-L623) `read-all-lines`</a>
``` clojure

(read-all-lines f)
(read-all-lines f {:keys [charset], :or {charset "utf-8"}})
```


Read all lines from a file.

## <a name="babashka.fs/read-attributes">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L658-L666) `read-attributes`</a>
``` clojure

(read-attributes path attributes)
(read-attributes path attributes {:keys [:nofollow-links :key-fn], :as opts})
```


Same as [`read-attributes*`](#babashka.fs/read-attributes*) but turns attributes into a map and keywordizes keys.
  Keywordizing can be changed by passing a :key-fn in the options map.

## <a name="babashka.fs/read-attributes*">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L640-L656) `read-attributes*`</a>
``` clojure

(read-attributes* path attributes)
(read-attributes* path attributes {:keys [:nofollow-links]})
```


Reads attributes via Files/readAttributes.

## <a name="babashka.fs/readable?">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L112-L114) `readable?`</a>
``` clojure

(readable? f)
```


Returns true if f is readable

## <a name="babashka.fs/real-path">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L72-L76) `real-path`</a>
``` clojure

(real-path f)
(real-path f {:keys [:nofollow-links]})
```


Converts f into real path via Path#toRealPath.

## <a name="babashka.fs/regular-file?">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L80-L85) `regular-file?`</a>
``` clojure

(regular-file? f)
(regular-file? f {:keys [:nofollow-links]})
```


Returns true if f is a regular file, using Files/isRegularFile.

## <a name="babashka.fs/relative?">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L120-L122) `relative?`</a>
``` clojure

(relative? f)
```


Returns true if f represents a relative path.

## <a name="babashka.fs/relativize">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L146-L149) `relativize`</a>
``` clojure

(relativize this other)
```


Returns relative path by comparing this with other.

## <a name="babashka.fs/same-file?">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L600-L603) `same-file?`</a>
``` clojure

(same-file? this other)
```


Returns true if this is the same file as other.

## <a name="babashka.fs/set-attribute">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L668-L675) `set-attribute`</a>
``` clojure

(set-attribute path attribute value)
(set-attribute path attribute value {:keys [:nofollow-links]})
```


## <a name="babashka.fs/set-creation-time">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L723-L728) `set-creation-time`</a>
``` clojure

(set-creation-time f time)
(set-creation-time f time {:keys [nofollow-links], :as opts})
```


Sets creation time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).

## <a name="babashka.fs/set-last-modified-time">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L709-L714) `set-last-modified-time`</a>
``` clojure

(set-last-modified-time f time)
(set-last-modified-time f time {:keys [nofollow-links], :as opts})
```


Sets last modified time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).

## <a name="babashka.fs/set-posix-file-permissions">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L589-L592) `set-posix-file-permissions`</a>
``` clojure

(set-posix-file-permissions f posix-file-permissions)
```


Sets posix file permissions on f. Accepts a string like `"rwx------"` or a set of PosixFilePermission.

## <a name="babashka.fs/size">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L578-L581) `size`</a>
``` clojure

(size f)
```


Returns the size of a file (in bytes).

## <a name="babashka.fs/split-ext">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L736-L745) `split-ext`</a>
``` clojure

(split-ext path)
```


Splits a path into a vec of [path-without-ext ext]. Works with strings, files, or paths.

## <a name="babashka.fs/split-paths">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L768-L771) `split-paths`</a>
``` clojure

(split-paths joined-paths)
```


Splits a string joined by the OS-specific path-seperator into a vec of paths.

## <a name="babashka.fs/starts-with?">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L843-L846) `starts-with?`</a>
``` clojure

(starts-with? this other)
```


Returns true if path this starts with path other.

## <a name="babashka.fs/str->posix">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L369-L372) `str->posix`</a>
``` clojure

(str->posix s)
```


Converts a string to a set of PosixFilePermission.

## <a name="babashka.fs/strip-ext">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L747-L761) `strip-ext`</a>
``` clojure

(strip-ext path)
(strip-ext path {:keys [ext]})
```


Returns the path with the extension removed. If provided, a specific extension will be removed.

## <a name="babashka.fs/sym-link?">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L535-L536) `sym-link?`</a>
``` clojure

(sym-link? f)
```


## <a name="babashka.fs/temp-dir">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L449-L452) `temp-dir`</a>
``` clojure

(temp-dir)
```


Returns `java.io.tmpdir` property as path.

## <a name="babashka.fs/unzip">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L895-L922) `unzip`</a>
``` clojure

(unzip zip-file)
(unzip zip-file dest)
(unzip zip-file dest {:keys [replace-existing]})
```


Unzips `zip-file` to `dest` directory (default `"."`).

   Options:
   * `:replace-existing` - `true` / `false`: overwrite existing files

## <a name="babashka.fs/walk-file-tree">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L174-L208) `walk-file-tree`</a>
``` clojure

(walk-file-tree f {:keys [:pre-visit-dir :post-visit-dir :visit-file :visit-file-failed :follow-links :max-depth]})
```


Walks f using Files/walkFileTree. Visitor functions: :pre-visit-dir,
  :post-visit-dir, :visit-file, :visit-file-failed. All visitor functions
  default to (constantly :continue). Supported return
  values: :continue, :skip-subtree, :skip-siblings, :terminate. A
  different return value will throw.

## <a name="babashka.fs/which">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L786-L829) `which`</a>
``` clojure

(which program)
(which program opts)
```


Returns Path to first `program` found in ([`exec-paths`](#babashka.fs/exec-paths)), similar to the which Unix command.

  On Windows, also searches for `program` with filename extensions specified in `:win-exts` `opt`.
  Default is `["com" "exe" "bat" "cmd"]`.
  If `program` already includes an extension from `:win-exts`, it will be searched as-is first.

## <a name="babashka.fs/which-all">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L831-L835) `which-all`</a>
``` clojure

(which-all program)
(which-all program opts)
```


Returns every Path to `program` found in ([`exec-paths`](#babashka.fs/exec-paths)). See [`which`](#babashka.fs/which).

## <a name="babashka.fs/windows?">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1020-L1023) `windows?`</a>
``` clojure

(windows?)
```


Returns true if OS is Windows.

## <a name="babashka.fs/with-temp-dir">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L972-L986) `with-temp-dir`</a>
``` clojure

(with-temp-dir [binding-name options] & body)
```


Macro.


Evaluate body with binding-name bound to a temporary directory.

  The directory is created by passing `options` to [`create-temp-dir`](#babashka.fs/create-temp-dir),
  and will be removed with [`delete-tree`](#babashka.fs/delete-tree) on exit from the scope.

  `options` is a map with the keys as for create-temp-dir.

## <a name="babashka.fs/writable?">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L116-L118) `writable?`</a>
``` clojure

(writable? f)
```


Returns true if f is writable

## <a name="babashka.fs/write-bytes">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1052-L1075) `write-bytes`</a>
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

## <a name="babashka.fs/write-lines">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1077-L1096) `write-lines`</a>
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

## <a name="babashka.fs/zip">[:page_facing_up:](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L953-L968) `zip`</a>
``` clojure

(zip zip-file entries)
(zip zip-file entries _opts)
```


Zips entry or entries into zip-file. An entry may be a file or
  directory. Directories are included recursively and their names are
  preserved in the zip file. Currently only accepts relative entries.
