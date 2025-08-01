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
    -  [`create-link`](#babashka.fs/create-link) - Create a new <code>link</code> (directory entry) for an <code>existing</code> file.
    -  [`create-sym-link`](#babashka.fs/create-sym-link) - Create a symbolic <code>link</code> to <code>target</code>.
    -  [`create-temp-dir`](#babashka.fs/create-temp-dir) - Creates a directory using [Files#createTempDirectory](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#createTempDirectory-java.nio.file.Path-java.lang.String-java.nio.file.attribute.FileAttribute...-).
    -  [`create-temp-file`](#babashka.fs/create-temp-file) - Creates an empty file using [Files#createTempFile](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#createTempFile-java.nio.file.Path-java.lang.String-java.lang.String-java.nio.file.attribute.FileAttribute...-).
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
    -  [`file`](#babashka.fs/file) - Coerces one arg into a File, or combines multiple paths into one.
    -  [`file-name`](#babashka.fs/file-name) - Returns the name of the file or directory.
    -  [`file-separator`](#babashka.fs/file-separator)
    -  [`file-time->instant`](#babashka.fs/file-time->instant) - Converts a java.nio.file.attribute.FileTime to a java.time.Instant.
    -  [`file-time->millis`](#babashka.fs/file-time->millis) - Converts a java.nio.file.attribute.FileTime to epoch millis (long).
    -  [`get-attribute`](#babashka.fs/get-attribute)
    -  [`glob`](#babashka.fs/glob) - Given a file and glob pattern, returns matches as vector of paths.
    -  [`gunzip`](#babashka.fs/gunzip) - Extracts <code>gz-file</code> to <code>dest</code> directory (default <code>&quot;.&quot;</code>).
    -  [`gzip`](#babashka.fs/gzip) - Gzips <code>source-file</code> and writes the output to <code>dir/out-file</code>.
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
    -  [`owner`](#babashka.fs/owner) - Returns the owner of a file.
    -  [`parent`](#babashka.fs/parent) - Returns parent of f.
    -  [`path`](#babashka.fs/path) - Coerces one arg into a Path, or combines multiple paths into one.
    -  [`path-separator`](#babashka.fs/path-separator)
    -  [`posix->str`](#babashka.fs/posix->str) - Converts a set of PosixFilePermission to a string.
    -  [`posix-file-permissions`](#babashka.fs/posix-file-permissions)
    -  [`read-all-bytes`](#babashka.fs/read-all-bytes) - Returns contents of file as byte array.
    -  [`read-all-lines`](#babashka.fs/read-all-lines) - Read all lines from a file.
    -  [`read-attributes`](#babashka.fs/read-attributes) - Same as <code>read-attributes*</code> but turns attributes into a map and keywordizes keys.
    -  [`read-attributes*`](#babashka.fs/read-attributes*) - Reads attributes via Files/readAttributes.
    -  [`read-link`](#babashka.fs/read-link) - Reads the target of a symbolic link.
    -  [`readable?`](#babashka.fs/readable?) - Returns true if f is readable.
    -  [`real-path`](#babashka.fs/real-path) - Converts f into real path via Path#toRealPath.
    -  [`regular-file?`](#babashka.fs/regular-file?) - Returns true if f is a regular file, using Files/isRegularFile.
    -  [`relative?`](#babashka.fs/relative?) - Returns true if f represents a relative path.
    -  [`relativize`](#babashka.fs/relativize) - Returns relative path by comparing this with other.
    -  [`same-file?`](#babashka.fs/same-file?) - Returns true if this is the same file as other.
    -  [`set-attribute`](#babashka.fs/set-attribute)
    -  [`set-creation-time`](#babashka.fs/set-creation-time) - Sets creation time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).
    -  [`set-last-modified-time`](#babashka.fs/set-last-modified-time) - Sets last modified time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).
    -  [`set-posix-file-permissions`](#babashka.fs/set-posix-file-permissions)
    -  [`size`](#babashka.fs/size) - Returns the size of a file (in bytes).
    -  [`split-ext`](#babashka.fs/split-ext) - Splits path on extension If provided, a specific extension <code>ext</code>, the extension (without dot), will be used for splitting.
    -  [`split-paths`](#babashka.fs/split-paths) - Splits a path list given as a string joined by the OS-specific path-separator into a vec of paths.
    -  [`starts-with?`](#babashka.fs/starts-with?) - Returns true if path this starts with path other.
    -  [`str->posix`](#babashka.fs/str->posix) - Converts a string to a set of PosixFilePermission.
    -  [`strip-ext`](#babashka.fs/strip-ext) - Strips extension via <code>split-ext</code>.
    -  [`sym-link?`](#babashka.fs/sym-link?) - Determines if <code>f</code> is a symbolic link via <code>java.nio.file.Files/isSymbolicLink</code>.
    -  [`temp-dir`](#babashka.fs/temp-dir) - Returns <code>java.io.tmpdir</code> property as path.
    -  [`unixify`](#babashka.fs/unixify) - Returns path as string with Unix-style file separators (<code>/</code>).
    -  [`unzip`](#babashka.fs/unzip) - Unzips <code>zip-file</code> to <code>dest</code> directory (default <code>&quot;.&quot;</code>).
    -  [`update-file`](#babashka.fs/update-file) - Updates the contents of text file <code>path</code> using <code>f</code> applied to old contents and <code>xs</code>.
    -  [`walk-file-tree`](#babashka.fs/walk-file-tree) - Walks f using Files/walkFileTree.
    -  [`which`](#babashka.fs/which) - Returns Path to first executable <code>program</code> found in <code>:paths</code> <code>opt</code>, similar to the which Unix command.
    -  [`which-all`](#babashka.fs/which-all) - Returns every Path to <code>program</code> found in (<code>exec-paths</code>).
    -  [`windows?`](#babashka.fs/windows?) - Returns true if OS is Windows.
    -  [`with-temp-dir`](#babashka.fs/with-temp-dir) - Evaluates body with binding-name bound to the result of <code>(create-temp-dir options)</code>, then cleans up.
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






## <a name="babashka.fs/absolute?">`absolute?`</a><a name="babashka.fs/absolute?"></a>
``` clojure

(absolute? f)
```
Function.

Returns true if f represents an absolute path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L121-L123">Source</a></sub></p>

## <a name="babashka.fs/absolutize">`absolutize`</a><a name="babashka.fs/absolutize"></a>
``` clojure

(absolutize f)
```
Function.

Converts f into an absolute path via Path#toAbsolutePath.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L160-L162">Source</a></sub></p>

## <a name="babashka.fs/canonicalize">`canonicalize`</a><a name="babashka.fs/canonicalize"></a>
``` clojure

(canonicalize f)
(canonicalize f {:keys [:nofollow-links]})
```
Function.

Returns the canonical path via
  java.io.File#getCanonicalPath. If `:nofollow-links` is set, then it
  will fall back on [`absolutize`](#babashka.fs/absolutize) + `normalize.` This function can be used
  as an alternative to [`real-path`](#babashka.fs/real-path) which requires files to exist.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L174-L183">Source</a></sub></p>

## <a name="babashka.fs/components">`components`</a><a name="babashka.fs/components"></a>
``` clojure

(components f)
```
Function.

Returns a seq of all components of f as paths, i.e. split on the file
  separator.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L154-L158">Source</a></sub></p>

## <a name="babashka.fs/copy">`copy`</a><a name="babashka.fs/copy"></a>
``` clojure

(copy src dest)
(copy src dest {:keys [replace-existing copy-attributes nofollow-links]})
```
Function.

Copies src file to dest dir or file.
  Options:
  * `:replace-existing`
  * `:copy-attributes`
  * `:nofollow-links` (used to determine to copy symbolic link itself or not).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L390-L408">Source</a></sub></p>

## <a name="babashka.fs/copy-tree">`copy-tree`</a><a name="babashka.fs/copy-tree"></a>
``` clojure

(copy-tree src dest)
(copy-tree src dest {:keys [:replace-existing :copy-attributes :nofollow-links], :as opts})
```
Function.

Copies entire file tree from src to dest. Creates dest if needed
  using [`create-dirs`](#babashka.fs/create-dirs), passing it the `:posix-file-permissions`
  option. Supports same options as copy.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L479-L526">Source</a></sub></p>

## <a name="babashka.fs/create-dir">`create-dir`</a><a name="babashka.fs/create-dir"></a>
``` clojure

(create-dir path)
(create-dir path {:keys [:posix-file-permissions]})
```
Function.

Creates dir using `Files#createDirectory`. Does not create parents.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L443-L449">Source</a></sub></p>

## <a name="babashka.fs/create-dirs">`create-dirs`</a><a name="babashka.fs/create-dirs"></a>
``` clojure

(create-dirs path)
(create-dirs path {:keys [:posix-file-permissions]})
```
Function.

Creates directories using `Files#createDirectories`. Also creates parents if needed.
  Doesn't throw an exception if the dirs exist already. Similar to `mkdir -p`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L451-L456">Source</a></sub></p>

## <a name="babashka.fs/create-file">`create-file`</a><a name="babashka.fs/create-file"></a>
``` clojure

(create-file path)
(create-file path {:keys [:posix-file-permissions]})
```
Function.

Creates empty file using `Files#createFile`.

  File permissions can be specified with an `:posix-file-permissions` option.
  String format for posix file permissions is described in the [`str->posix`](#babashka.fs/str->posix) docstring.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L680-L689">Source</a></sub></p>

## <a name="babashka.fs/create-link">`create-link`</a><a name="babashka.fs/create-link"></a>
``` clojure

(create-link link existing)
```
Function.

Create a new `link` (directory entry) for an `existing` file.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L627-L632">Source</a></sub></p>

## <a name="babashka.fs/create-sym-link">`create-sym-link`</a><a name="babashka.fs/create-sym-link"></a>
``` clojure

(create-sym-link link target)
```
Function.

Create a symbolic `link` to `target`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L619-L625">Source</a></sub></p>

## <a name="babashka.fs/create-temp-dir">`create-temp-dir`</a><a name="babashka.fs/create-temp-dir"></a>
``` clojure

(create-temp-dir)
(create-temp-dir {:keys [:dir :prefix :posix-file-permissions], :as opts})
```
Function.

Creates a directory using [Files#createTempDirectory](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#createTempDirectory-java.nio.file.Path-java.lang.String-java.nio.file.attribute.FileAttribute...-).

  This function does not set up any automatic deletion of the directories it
  creates. See [`with-temp-dir`](#babashka.fs/with-temp-dir) for that functionality.

  Options:
  - `:dir`: Directory in which to create the new directory. Defaults to default
  system temp dir (e.g. `/tmp`); see [`temp-dir`](#babashka.fs/temp-dir). Must already exist.
  - `:prefix`: Provided as a hint to the process that generates the name of the
  new directory. In most cases, this will be the beginning of the new directory
  name. Defaults to a random (v4) UUID.
  - `:posix-file-permissions`: The new directory will be created with these
  permissions, given as a String as described in [`str->posix`](#babashka.fs/str->posix). If not
  specified, uses the file system default permissions for new directories.
  - :warning: `:path` **[DEPRECATED]** Previous name for `:dir`, kept
  for backwards compatibility. If both `:path` and `:dir` are given (don't do
  that!), `:dir` is used.

  Examples:
  - `(create-temp-dir)`
  - `(create-temp-dir {:posix-file-permissions "rwx------"})`
  - `(create-temp-dir {:dir (path (cwd) "_workdir") :prefix "process-1-"})`
  
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L535-L571">Source</a></sub></p>

## <a name="babashka.fs/create-temp-file">`create-temp-file`</a><a name="babashka.fs/create-temp-file"></a>
``` clojure

(create-temp-file)
(create-temp-file {:keys [:dir :prefix :suffix :posix-file-permissions], :as opts})
```
Function.

Creates an empty file using [Files#createTempFile](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#createTempFile-java.nio.file.Path-java.lang.String-java.lang.String-java.nio.file.attribute.FileAttribute...-).

  This function does not set up any automatic deletion of the files it
  creates. Create the file in a [`with-temp-dir`](#babashka.fs/with-temp-dir) for that functionality.

  Options:
  - `:dir`: Directory in which to create the new file. Defaults to default
  system temp dir (e.g. `/tmp`); see [`temp-dir`](#babashka.fs/temp-dir). Must already exist.
  - `:prefix`: Provided as a hint to the process that generates the name of the
  new file. In most cases, this will be the beginning of the new file name.
  Defaults to a random (v4) UUID.
  - `:suffix`: Provided as a hint to the process that generates the name of the
  new file. In most cases, this will be the end of the new file name.
  Defaults to a random (v4) UUID.
  - `:posix-file-permissions`: The new file will be created with these
  permissions, given as a String as described in [`str->posix`](#babashka.fs/str->posix). If not
  specified, uses the file system default permissions for new files.
  - :warning: `:path` **[DEPRECATED]** Previous name for `:dir`, kept
  for backwards compatibility. If both `:path` and `:dir` are given (don't do
  that!), `:dir` is used.

  Examples:
  - `(create-temp-file)`
  - `(create-temp-file {:posix-file-permissions "rw-------"})`
  - `(create-temp-file {:dir (path (cwd) "_workdir") :prefix "process-1-" :suffix "-queue"})`
  
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L573-L617">Source</a></sub></p>

## <a name="babashka.fs/creation-time">`creation-time`</a><a name="babashka.fs/creation-time"></a>
``` clojure

(creation-time f)
(creation-time f {:keys [nofollow-links], :as opts})
```
Function.

Returns creation time as FileTime.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L838-L843">Source</a></sub></p>

## <a name="babashka.fs/cwd">`cwd`</a><a name="babashka.fs/cwd"></a>
``` clojure

(cwd)
```
Function.

Returns current working directory as path
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1245-L1248">Source</a></sub></p>

## <a name="babashka.fs/delete">`delete`</a><a name="babashka.fs/delete"></a>
``` clojure

(delete f)
```
Function.

Deletes f. Returns nil if the delete was successful,
  throws otherwise. Does not follow symlinks.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L639-L645">Source</a></sub></p>

## <a name="babashka.fs/delete-if-exists">`delete-if-exists`</a><a name="babashka.fs/delete-if-exists"></a>
``` clojure

(delete-if-exists f)
```
Function.

Deletes f if it exists. Returns true if the delete was successful,
  false if f didn't exist. Does not follow symlinks.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L647-L651">Source</a></sub></p>

## <a name="babashka.fs/delete-on-exit">`delete-on-exit`</a><a name="babashka.fs/delete-on-exit"></a>
``` clojure

(delete-on-exit f)
```
Function.

Requests delete on exit via `File#deleteOnExit`. Returns f.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L716-L720">Source</a></sub></p>

## <a name="babashka.fs/delete-tree">`delete-tree`</a><a name="babashka.fs/delete-tree"></a>
``` clojure

(delete-tree root)
(delete-tree root {:keys [force]})
```
Function.

Deletes a file tree using [`walk-file-tree`](#babashka.fs/walk-file-tree). Similar to `rm -rf`. Does not follow symlinks.
   `force` ensures read-only directories/files are deleted. Similar to `chmod -R +wx` + `rm -rf`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L658-L678">Source</a></sub></p>

## <a name="babashka.fs/directory?">`directory?`</a><a name="babashka.fs/directory?"></a>
``` clojure

(directory? f)
(directory? f {:keys [:nofollow-links]})
```
Function.

Returns true if f is a directory, using Files/isDirectory.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L104-L109">Source</a></sub></p>

## <a name="babashka.fs/ends-with?">`ends-with?`</a><a name="babashka.fs/ends-with?"></a>
``` clojure

(ends-with? this other)
```
Function.

Returns true if path this ends with path other.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L980-L983">Source</a></sub></p>

## <a name="babashka.fs/exec-paths">`exec-paths`</a><a name="babashka.fs/exec-paths"></a>
``` clojure

(exec-paths)
```
Function.

Returns executable paths (using the PATH environment variable). Same
  as `(split-paths (System/getenv "PATH"))`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L896-L900">Source</a></sub></p>

## <a name="babashka.fs/executable?">`executable?`</a><a name="babashka.fs/executable?"></a>
``` clojure

(executable? f)
```
Function.

Returns true if f is executable.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L125-L127">Source</a></sub></p>

## <a name="babashka.fs/exists?">`exists?`</a><a name="babashka.fs/exists?"></a>
``` clojure

(exists? f)
(exists? f {:keys [:nofollow-links]})
```
Function.

Returns true if f exists.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L141-L150">Source</a></sub></p>

## <a name="babashka.fs/expand-home">`expand-home`</a><a name="babashka.fs/expand-home"></a>
``` clojure

(expand-home f)
```
Function.

If [`path`](#babashka.fs/path) begins with a tilde (`~`), expand the tilde to the value
  of the `user.home` system property. If the [`path`](#babashka.fs/path) begins with a
  tilde immediately followed by some characters, they are assumed to
  be a username. This is expanded to the path to that user's home
  directory. This is (naively) assumed to be a directory with the same
  name as the user relative to the parent of the current value of
  `user.home`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1222-L1238">Source</a></sub></p>

## <a name="babashka.fs/extension">`extension`</a><a name="babashka.fs/extension"></a>
``` clojure

(extension path)
```
Function.

Returns the extension of a file via [`split-ext`](#babashka.fs/split-ext).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L885-L888">Source</a></sub></p>

## <a name="babashka.fs/file">`file`</a><a name="babashka.fs/file"></a>
``` clojure

(file f)
(file f & fs)
```
Function.

Coerces one arg into a File, or combines multiple paths into one.
  Multiple-arg versions treat the first argument as parent and subsequent args
  as children relative to the parent.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L67-L73">Source</a></sub></p>

## <a name="babashka.fs/file-name">`file-name`</a><a name="babashka.fs/file-name"></a>
``` clojure

(file-name x)
```
Function.

Returns the name of the file or directory. E.g. (file-name "foo/bar/baz") returns "baz".
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L185-L188">Source</a></sub></p>

## <a name="babashka.fs/file-separator">`file-separator`</a><a name="babashka.fs/file-separator"></a>



<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L256-L256">Source</a></sub></p>

## <a name="babashka.fs/file-time->instant">`file-time->instant`</a><a name="babashka.fs/file-time->instant"></a>
``` clojure

(file-time->instant ft)
```
Function.

Converts a java.nio.file.attribute.FileTime to a java.time.Instant.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L799-L802">Source</a></sub></p>

## <a name="babashka.fs/file-time->millis">`file-time->millis`</a><a name="babashka.fs/file-time->millis"></a>
``` clojure

(file-time->millis ft)
```
Function.

Converts a java.nio.file.attribute.FileTime to epoch millis (long).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L809-L812">Source</a></sub></p>

## <a name="babashka.fs/get-attribute">`get-attribute`</a><a name="babashka.fs/get-attribute"></a>
``` clojure

(get-attribute path attribute)
(get-attribute path attribute {:keys [:nofollow-links]})
```
Function.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L749-L755">Source</a></sub></p>

## <a name="babashka.fs/glob">`glob`</a><a name="babashka.fs/glob"></a>
``` clojure

(glob root pattern)
(glob root pattern opts)
```
Function.

Given a file and glob pattern, returns matches as vector of
  paths. Patterns containing `**` or `/` will cause a recursive walk over
  path, unless overriden with :recursive. Similarly: :hidden will be enabled (when not set)
  when `pattern` starts with a dot.
  Glob interpretation is done using the rules described in
  https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String).

  Options:

  * `:hidden` - match hidden paths. Implied when `pattern` starts with a dot;
  otherwise, default to false. Note: on Windows files starting with a dot are
  not hidden, unless their hidden attribute is set.
  * `:follow-links` - follow symlinks. Defaults to false.
  * `:recursive` - force recursive search. Implied when `pattern` contains
  `**` or `/`; otherwise, defaults to false.
  * `:max-depth` - max depth to descend into directory structure, when
  recursing. Defaults to Integer/MAX_VALUE.

  Examples:
  `(fs/glob "." "**.clj")`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L350-L379">Source</a></sub></p>

## <a name="babashka.fs/gunzip">`gunzip`</a><a name="babashka.fs/gunzip"></a>
``` clojure

(gunzip gz-file)
(gunzip gz-file dest)
(gunzip gz-file dest {:keys [replace-existing]})
```
Function.

Extracts `gz-file` to `dest` directory (default `"."`).

   Options:
   * `:replace-existing` - `true` / `false`: overwrite existing files
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1133-L1153">Source</a></sub></p>

## <a name="babashka.fs/gzip">`gzip`</a><a name="babashka.fs/gzip"></a>
``` clojure

(gzip source-file)
(gzip source-file {:keys [dir out-file], :or {dir "."}})
```
Function.

Gzips `source-file` and writes the output to `dir/out-file`.
  If `out-file` is not provided, the `source-file` name with `.gz` appended is used.
  If `dir` is not provided, the current directory is used.
  Returns the created gzip file.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1155-L1176">Source</a></sub></p>

## <a name="babashka.fs/hidden?">`hidden?`</a><a name="babashka.fs/hidden?"></a>
``` clojure

(hidden? f)
```
Function.

Returns true if f is hidden.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L117-L119">Source</a></sub></p>

## <a name="babashka.fs/home">`home`</a><a name="babashka.fs/home"></a>
``` clojure

(home)
(home user)
```
Function.

With no arguments, returns the current value of the `user.home`
  system property. If a `user` is passed, returns that user's home
  directory as found in the parent of home with no args.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1214-L1220">Source</a></sub></p>

## <a name="babashka.fs/instant->file-time">`instant->file-time`</a><a name="babashka.fs/instant->file-time"></a>
``` clojure

(instant->file-time instant)
```
Function.

Converts a java.time.Instant to a java.nio.file.attribute.FileTime.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L804-L807">Source</a></sub></p>

## <a name="babashka.fs/last-modified-time">`last-modified-time`</a><a name="babashka.fs/last-modified-time"></a>
``` clojure

(last-modified-time f)
(last-modified-time f {:keys [nofollow-links], :as opts})
```
Function.

Returns last modified time as a java.nio.file.attribute.FileTime.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L824-L829">Source</a></sub></p>

## <a name="babashka.fs/list-dir">`list-dir`</a><a name="babashka.fs/list-dir"></a>
``` clojure

(list-dir dir)
(list-dir dir glob-or-accept)
```
Function.

Returns all paths in dir as vector. For descending into subdirectories use `glob.`
     - `glob-or-accept` - a glob string such as "*.edn" or a (fn accept [^java.nio.file.Path p]) -> truthy
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L246-L254">Source</a></sub></p>

## <a name="babashka.fs/list-dirs">`list-dirs`</a><a name="babashka.fs/list-dirs"></a>
``` clojure

(list-dirs dirs glob-or-accept)
```
Function.

Similar to list-dir but accepts multiple roots and returns the concatenated results.
  - `glob-or-accept` - a glob string such as "*.edn" or a (fn accept [^java.nio.file.Path p]) -> truthy
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L852-L856">Source</a></sub></p>

## <a name="babashka.fs/match">`match`</a><a name="babashka.fs/match"></a>
``` clojure

(match root pattern)
(match root pattern {:keys [hidden follow-links max-depth recursive]})
```
Function.

Given a file and match pattern, returns matches as vector of
  paths. Pattern interpretation is done using the rules described in
  https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String).

  Options:

  * `:hidden` - match hidden paths - note: on Windows paths starting with
  a dot are not hidden, unless their hidden attribute is set. Defaults to
  false, i.e. skip hidden files and folders.
  * `:follow-links` - follow symlinks. Defaults to false.
  * `:recursive` - match recursively. Defaults to false.
  * `:max-depth` - max depth to descend into directory structure, when
  matching recursively. Defaults to Integer/MAX_VALUE.

  Examples:
  `(fs/match "." "regex:.*\\.clj" {:recursive true})`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L279-L348">Source</a></sub></p>

## <a name="babashka.fs/millis->file-time">`millis->file-time`</a><a name="babashka.fs/millis->file-time"></a>
``` clojure

(millis->file-time millis)
```
Function.

Converts epoch millis (long) to a java.nio.file.attribute.FileTime.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L814-L817">Source</a></sub></p>

## <a name="babashka.fs/modified-since">`modified-since`</a><a name="babashka.fs/modified-since"></a>
``` clojure

(modified-since anchor file-set)
```
Function.

Returns seq of regular files (non-directories, non-symlinks) from file-set that were modified since the anchor path.
  The anchor path can be a regular file or directory, in which case
  the recursive max last modified time stamp is used as the timestamp
  to compare with.  The file-set may be a regular file, directory or
  collection of files (e.g. returned by glob). Directories are
  searched recursively.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1016-L1025">Source</a></sub></p>

## <a name="babashka.fs/move">`move`</a><a name="babashka.fs/move"></a>
``` clojure

(move source target)
(move source target {:keys [:replace-existing :atomic-move :nofollow-links]})
```
Function.

Move or rename a file to a target dir or file via `Files/move`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L691-L704">Source</a></sub></p>

## <a name="babashka.fs/normalize">`normalize`</a><a name="babashka.fs/normalize"></a>
``` clojure

(normalize f)
```
Function.

Normalizes f via Path#normalize.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L169-L172">Source</a></sub></p>

## <a name="babashka.fs/owner">`owner`</a><a name="babashka.fs/owner"></a>
``` clojure

(owner f)
(owner f {:keys [:nofollow-links]})
```
Function.

Returns the owner of a file. Call `str` on it to get the owner name
  as a string.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L88-L93">Source</a></sub></p>

## <a name="babashka.fs/parent">`parent`</a><a name="babashka.fs/parent"></a>
``` clojure

(parent f)
```
Function.

Returns parent of f. Akin to `dirname` in bash.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L706-L709">Source</a></sub></p>

## <a name="babashka.fs/path">`path`</a><a name="babashka.fs/path"></a>
``` clojure

(path f)
(path parent child)
(path parent child & more)
```
Function.

Coerces one arg into a Path, or combines multiple paths into one.
  Multiple-arg versions treat the first argument as parent and subsequent
  args as children relative to the parent.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L52-L65">Source</a></sub></p>

## <a name="babashka.fs/path-separator">`path-separator`</a><a name="babashka.fs/path-separator"></a>



<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L257-L257">Source</a></sub></p>

## <a name="babashka.fs/posix->str">`posix->str`</a><a name="babashka.fs/posix->str"></a>
``` clojure

(posix->str p)
```
Function.

Converts a set of PosixFilePermission to a string.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L410-L413">Source</a></sub></p>

## <a name="babashka.fs/posix-file-permissions">`posix-file-permissions`</a><a name="babashka.fs/posix-file-permissions"></a>



<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L527-L527">Source</a></sub></p>

## <a name="babashka.fs/read-all-bytes">`read-all-bytes`</a><a name="babashka.fs/read-all-bytes"></a>
``` clojure

(read-all-bytes f)
```
Function.

Returns contents of file as byte array.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L727-L730">Source</a></sub></p>

## <a name="babashka.fs/read-all-lines">`read-all-lines`</a><a name="babashka.fs/read-all-lines"></a>
``` clojure

(read-all-lines f)
(read-all-lines f {:keys [charset], :or {charset "utf-8"}})
```
Function.

Read all lines from a file.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L737-L745">Source</a></sub></p>

## <a name="babashka.fs/read-attributes">`read-attributes`</a><a name="babashka.fs/read-attributes"></a>
``` clojure

(read-attributes path attributes)
(read-attributes path attributes {:keys [:nofollow-links :key-fn], :as opts})
```
Function.

Same as [`read-attributes*`](#babashka.fs/read-attributes*) but turns attributes into a map and keywordizes keys.
  Keywordizing can be changed by passing a :key-fn in the options map.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L780-L788">Source</a></sub></p>

## <a name="babashka.fs/read-attributes*">`read-attributes*`</a><a name="babashka.fs/read-attributes*"></a>
``` clojure

(read-attributes* path attributes)
(read-attributes* path attributes {:keys [:nofollow-links]})
```
Function.

Reads attributes via Files/readAttributes.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L762-L778">Source</a></sub></p>

## <a name="babashka.fs/read-link">`read-link`</a><a name="babashka.fs/read-link"></a>
``` clojure

(read-link path)
```
Function.

Reads the target of a symbolic link. The target need not exist.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L634-L637">Source</a></sub></p>

## <a name="babashka.fs/readable?">`readable?`</a><a name="babashka.fs/readable?"></a>
``` clojure

(readable? f)
```
Function.

Returns true if f is readable
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L129-L131">Source</a></sub></p>

## <a name="babashka.fs/real-path">`real-path`</a><a name="babashka.fs/real-path"></a>
``` clojure

(real-path f)
(real-path f {:keys [:nofollow-links]})
```
Function.

Converts f into real path via Path#toRealPath.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L82-L86">Source</a></sub></p>

## <a name="babashka.fs/regular-file?">`regular-file?`</a><a name="babashka.fs/regular-file?"></a>
``` clojure

(regular-file? f)
(regular-file? f {:keys [:nofollow-links]})
```
Function.

Returns true if f is a regular file, using Files/isRegularFile.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L97-L102">Source</a></sub></p>

## <a name="babashka.fs/relative?">`relative?`</a><a name="babashka.fs/relative?"></a>
``` clojure

(relative? f)
```
Function.

Returns true if f represents a relative path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L137-L139">Source</a></sub></p>

## <a name="babashka.fs/relativize">`relativize`</a><a name="babashka.fs/relativize"></a>
``` clojure

(relativize this other)
```
Function.

Returns relative path by comparing this with other.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L164-L167">Source</a></sub></p>

## <a name="babashka.fs/same-file?">`same-file?`</a><a name="babashka.fs/same-file?"></a>
``` clojure

(same-file? this other)
```
Function.

Returns true if this is the same file as other.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L722-L725">Source</a></sub></p>

## <a name="babashka.fs/set-attribute">`set-attribute`</a><a name="babashka.fs/set-attribute"></a>
``` clojure

(set-attribute path attribute value)
(set-attribute path attribute value {:keys [:nofollow-links]})
```
Function.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L790-L797">Source</a></sub></p>

## <a name="babashka.fs/set-creation-time">`set-creation-time`</a><a name="babashka.fs/set-creation-time"></a>
``` clojure

(set-creation-time f time)
(set-creation-time f time {:keys [nofollow-links], :as opts})
```
Function.

Sets creation time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L845-L850">Source</a></sub></p>

## <a name="babashka.fs/set-last-modified-time">`set-last-modified-time`</a><a name="babashka.fs/set-last-modified-time"></a>
``` clojure

(set-last-modified-time f time)
(set-last-modified-time f time {:keys [nofollow-links], :as opts})
```
Function.

Sets last modified time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L831-L836">Source</a></sub></p>

## <a name="babashka.fs/set-posix-file-permissions">`set-posix-file-permissions`</a><a name="babashka.fs/set-posix-file-permissions"></a>



<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L528-L528">Source</a></sub></p>

## <a name="babashka.fs/size">`size`</a><a name="babashka.fs/size"></a>
``` clojure

(size f)
```
Function.

Returns the size of a file (in bytes).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L711-L714">Source</a></sub></p>

## <a name="babashka.fs/split-ext">`split-ext`</a><a name="babashka.fs/split-ext"></a>
``` clojure

(split-ext path)
(split-ext path {:keys [ext]})
```
Function.

Splits path on extension If provided, a specific extension `ext`, the
  extension (without dot), will be used for splitting.  Directories
  are not processed.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L858-L876">Source</a></sub></p>

## <a name="babashka.fs/split-paths">`split-paths`</a><a name="babashka.fs/split-paths"></a>
``` clojure

(split-paths joined-paths)
```
Function.

Splits a path list given as a string joined by the OS-specific path-separator into a vec of paths.
  On UNIX systems, the separator is ':', on Microsoft Windows systems it is ';'.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L890-L894">Source</a></sub></p>

## <a name="babashka.fs/starts-with?">`starts-with?`</a><a name="babashka.fs/starts-with?"></a>
``` clojure

(starts-with? this other)
```
Function.

Returns true if path this starts with path other.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L975-L978">Source</a></sub></p>

## <a name="babashka.fs/str->posix">`str->posix`</a><a name="babashka.fs/str->posix"></a>
``` clojure

(str->posix s)
```
Function.

Converts a string to a set of PosixFilePermission.

  `s` is a string like `"rwx------"`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L415-L420">Source</a></sub></p>

## <a name="babashka.fs/strip-ext">`strip-ext`</a><a name="babashka.fs/strip-ext"></a>
``` clojure

(strip-ext path)
(strip-ext path {:keys [ext], :as opts})
```
Function.

Strips extension via [`split-ext`](#babashka.fs/split-ext).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L878-L883">Source</a></sub></p>

## <a name="babashka.fs/sym-link?">`sym-link?`</a><a name="babashka.fs/sym-link?"></a>
``` clojure

(sym-link? f)
```
Function.

Determines if `f` is a symbolic link via `java.nio.file.Files/isSymbolicLink`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L653-L656">Source</a></sub></p>

## <a name="babashka.fs/temp-dir">`temp-dir`</a><a name="babashka.fs/temp-dir"></a>
``` clojure

(temp-dir)
```
Function.

Returns `java.io.tmpdir` property as path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L530-L533">Source</a></sub></p>

## <a name="babashka.fs/unixify">`unixify`</a><a name="babashka.fs/unixify"></a>
``` clojure

(unixify f)
```
Function.

Returns path as string with Unix-style file separators (`/`).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1337-L1342">Source</a></sub></p>

## <a name="babashka.fs/unzip">`unzip`</a><a name="babashka.fs/unzip"></a>
``` clojure

(unzip zip-file)
(unzip zip-file dest)
(unzip zip-file dest {:keys [replace-existing extract-fn]})
```
Function.

Unzips `zip-file` to `dest` directory (default `"."`).

   Options:
   * `:replace-existing` - `true` / `false`: overwrite existing files
   * `:extract-fn` - function that decides if the current ZipEntry
     should be extracted. The function is only called for the file case
     (not directories) with a map with entries:
     * `:entry` and the current ZipEntry
     * `:name` and the name of the ZipEntry (result of calling `getName`)
     Extraction only occurs if a truthy value is returned (i.e. not
     nil/false).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1031-L1068">Source</a></sub></p>

## <a name="babashka.fs/update-file">`update-file`</a><a name="babashka.fs/update-file"></a>
``` clojure

(update-file path f & xs)
(update-file path opts f & xs)
```
Function.

Updates the contents of text file [`path`](#babashka.fs/path) using `f` applied to old contents and `xs`.
  Returns the new contents.

  Options:

  * `:charset` - charset of file, default to "utf-8"
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1317-L1335">Source</a></sub></p>

## <a name="babashka.fs/walk-file-tree">`walk-file-tree`</a><a name="babashka.fs/walk-file-tree"></a>
``` clojure

(walk-file-tree f {:keys [:pre-visit-dir :post-visit-dir :visit-file :visit-file-failed :follow-links :max-depth]})
```
Function.

Walks f using Files/walkFileTree. Visitor functions: :pre-visit-dir,
  :post-visit-dir, :visit-file, :visit-file-failed. All visitor functions
  default to (constantly :continue). Supported return
  values: :continue, :skip-subtree, :skip-siblings, :terminate. A
  different return value will throw.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L192-L226">Source</a></sub></p>

## <a name="babashka.fs/which">`which`</a><a name="babashka.fs/which"></a>
``` clojure

(which program)
(which program opts)
```
Function.

Returns Path to first executable `program` found in `:paths` `opt`, similar to the which Unix command.
  Default for `:paths` is `(exec-paths)`.

  On Windows, searches for `program` with filename extensions specified in `:win-exts` `opt`.
  Default is `["com" "exe" "bat" "cmd"]`.
  If `program` already includes an extension from `:win-exts`, it will be searched as-is first.

  When `program` is a relative or absolute path, `:paths` is not consulted. On Windows, the `:win-exts`
  variants are still searched. On other OSes, the path for `program` will be returned if executable,
  else nil.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L909-L961">Source</a></sub></p>

## <a name="babashka.fs/which-all">`which-all`</a><a name="babashka.fs/which-all"></a>
``` clojure

(which-all program)
(which-all program opts)
```
Function.

Returns every Path to `program` found in ([`exec-paths`](#babashka.fs/exec-paths)). See [`which`](#babashka.fs/which).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L963-L967">Source</a></sub></p>

## <a name="babashka.fs/windows?">`windows?`</a><a name="babashka.fs/windows?"></a>
``` clojure

(windows?)
```
Function.

Returns true if OS is Windows.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1240-L1243">Source</a></sub></p>

## <a name="babashka.fs/with-temp-dir">`with-temp-dir`</a><a name="babashka.fs/with-temp-dir"></a>
``` clojure

(with-temp-dir [binding-name] & body)
(with-temp-dir [binding-name options] & body)
```
Macro.

Evaluates body with binding-name bound to the result of `(create-temp-dir
  options)`, then cleans up. See [`create-temp-dir`](#babashka.fs/create-temp-dir)
  for valid `options`.

  The directory will be removed with [`delete-tree`](#babashka.fs/delete-tree) on exit from the scope.

  Example:

  ```
  (with-temp-dir [d]
    (let [t (path d "extract")
      (create-dir t)
      (gunzip path-to-zip t)
      (copy (path t "the-one-file-I-wanted.txt") (path permanent-dir "file-I-extracted.txt"))))
  ;; d no longer exists here
  ```
  
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1180-L1206">Source</a></sub></p>

## <a name="babashka.fs/writable?">`writable?`</a><a name="babashka.fs/writable?"></a>
``` clojure

(writable? f)
```
Function.

Returns true if f is writable
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L133-L135">Source</a></sub></p>

## <a name="babashka.fs/write-bytes">`write-bytes`</a><a name="babashka.fs/write-bytes"></a>
``` clojure

(write-bytes path bytes)
(write-bytes path bytes {:keys [append create truncate-existing write], :as opts})
```
Function.

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
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1271-L1294">Source</a></sub></p>

## <a name="babashka.fs/write-lines">`write-lines`</a><a name="babashka.fs/write-lines"></a>
``` clojure

(write-lines path lines)
(write-lines path lines {:keys [charset], :or {charset "utf-8"}, :as opts})
```
Function.

Writes `lines`, a seqable of strings to [`path`](#babashka.fs/path) via `java.nio.file.Files/write`.

  Supported options:
  * `:charset` (default `"utf-8"`)

  Supported open options:
  * `:create` (default `true`)
  * `:truncate-existing` (default `true`)
  * `:write` (default `true`)
  * `:append` (default `false`)
  * or any `java.nio.file.StandardOption`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1296-L1315">Source</a></sub></p>

## <a name="babashka.fs/xdg-cache-home">`xdg-cache-home`</a><a name="babashka.fs/xdg-cache-home"></a>
``` clojure

(xdg-cache-home)
(xdg-cache-home app)
```
Function.

Path representing the base directory relative to which user-specific non-essential data files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_CACHE_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) ".cache")`.
  When provided, appends `app` to the path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1374-L1382">Source</a></sub></p>

## <a name="babashka.fs/xdg-config-home">`xdg-config-home`</a><a name="babashka.fs/xdg-config-home"></a>
``` clojure

(xdg-config-home)
(xdg-config-home app)
```
Function.

Path representing the base directory relative to which user-specific configuration files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_CONFIG_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) ".config")`.
  When provided, appends `app` to the path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1364-L1372">Source</a></sub></p>

## <a name="babashka.fs/xdg-data-home">`xdg-data-home`</a><a name="babashka.fs/xdg-data-home"></a>
``` clojure

(xdg-data-home)
(xdg-data-home app)
```
Function.

Path representing the base directory relative to which user-specific data files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_DATA_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) ".local" "share")`.
  When provided, appends `app` to the path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1384-L1392">Source</a></sub></p>

## <a name="babashka.fs/xdg-state-home">`xdg-state-home`</a><a name="babashka.fs/xdg-state-home"></a>
``` clojure

(xdg-state-home)
(xdg-state-home app)
```
Function.

Path representing the base directory relative to which user-specific state files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_STATE_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) ".local" "state")`.
  When provided, appends `app` to the path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1394-L1402">Source</a></sub></p>

## <a name="babashka.fs/zip">`zip`</a><a name="babashka.fs/zip"></a>
``` clojure

(zip zip-file entries)
(zip zip-file entries opts)
```
Function.

Zips entry or entries into zip-file. An entry may be a file or
  directory. Directories are included recursively and their names are
  preserved in the zip file. Currently only accepts relative entries.

  Options:
  * `:root`: directory which will be elided in zip. E.g.: `(fs/zip ["src"] {:root "src"})`
  * `:path-fn`: a single-arg function from file system path to zip entry path.
  
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1101-L1127">Source</a></sub></p>
