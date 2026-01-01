# Table of contents
-  [`babashka.fs`](#babashka.fs) 
    -  [`absolute?`](#babashka.fs/absolute?) - Returns true if <code>f</code> represents an absolute path via [Path#isAbsolute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#isAbsolute()).
    -  [`absolutize`](#babashka.fs/absolutize) - Converts <code>f</code> into an absolute <code>Path</code> via [Path#toAbsolutePath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#toAbsolutePath()).
    -  [`canonicalize`](#babashka.fs/canonicalize) - Returns the canonical <code>Path</code> for <code>f</code> via [File#getCanonicalPath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#getCanonicalPath()).
    -  [`components`](#babashka.fs/components) - Returns a seq of all components of <code>f</code> as paths.
    -  [`copy`](#babashka.fs/copy) - Copies <code>src</code> file to <code>dest</code> dir or file using [Files/copy](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#copy(java.nio.file.Path,java.nio.file.Path,java.nio.file.CopyOption...)).
    -  [`copy-tree`](#babashka.fs/copy-tree) - Copies entire file tree from <code>src</code> to <code>dest</code>.
    -  [`create-dir`](#babashka.fs/create-dir) - Creates dir using [Files/createDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createDirectory(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
    -  [`create-dirs`](#babashka.fs/create-dirs) - Creates directories for <code>path</code> using [Files/createDirectories](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createDirectories(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
    -  [`create-file`](#babashka.fs/create-file) - Creates empty file at <code>path</code> using [Files/createFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createFile(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
    -  [`create-link`](#babashka.fs/create-link) - Create a new <code>link</code> (directory entry) for an <code>existing</code> file via [Files/createLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createLink(java.nio.file.Path,java.nio.file.Path)).
    -  [`create-sym-link`](#babashka.fs/create-sym-link) - Create a symbolic <code>link</code> to <code>target</code> via [Files/createSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createSymbolicLink(java.nio.file.Path,java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
    -  [`create-temp-dir`](#babashka.fs/create-temp-dir) - Creates a directory using [Files/createTempDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createTempDirectory(java.nio.file.Path,java.lang.String,java.nio.file.attribute.FileAttribute...)).
    -  [`create-temp-file`](#babashka.fs/create-temp-file) - Creates an empty file using [Files/createTempFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createTempFile(java.nio.file.Path,java.lang.String,java.lang.String,java.nio.file.attribute.FileAttribute...)).
    -  [`creation-time`](#babashka.fs/creation-time) - Returns creation time of <code>f</code> as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).
    -  [`cwd`](#babashka.fs/cwd) - Returns current working directory as <code>Path</code>.
    -  [`delete`](#babashka.fs/delete) - Deletes <code>f</code> using [Files/delete](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#delete(java.nio.file.Path)).
    -  [`delete-if-exists`](#babashka.fs/delete-if-exists) - Deletes <code>f</code> if it exists via [Files/deleteIfExists](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#deleteIfExists(java.nio.file.Path)).
    -  [`delete-on-exit`](#babashka.fs/delete-on-exit) - Requests delete of file <code>f</code> on exit via [File#deleteOnExit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#deleteOnExit()).
    -  [`delete-tree`](#babashka.fs/delete-tree) - Deletes a file tree <code>root</code> using [[walk-file-tree]].
    -  [`directory?`](#babashka.fs/directory?) - Returns true if <code>f</code> is a directory, using [Files/isDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isDirectory(java.nio.file.Path,java.nio.file.LinkOption...)).
    -  [`ends-with?`](#babashka.fs/ends-with?) - Returns <code>true</code> if path <code>this</code> ends with path <code>other</code> via [Path#endsWith](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#endsWith(java.nio.file.Path)).
    -  [`exec-paths`](#babashka.fs/exec-paths) - Returns executable paths (using the <code>PATH</code> environment variable).
    -  [`executable?`](#babashka.fs/executable?) - Returns true if <code>f</code> has is executable via [Files/isExecutable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isExecutable(java.nio.file.Path)).
    -  [`exists?`](#babashka.fs/exists?) - Returns true if <code>f</code> exists via [Files/exists](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#exists(java.nio.file.Path,java.nio.file.LinkOption...)).
    -  [`expand-home`](#babashka.fs/expand-home) - If <code>f</code> begins with a tilde (<code>~</code>), expand the tilde to the value of the <code>user.home</code> system property.
    -  [`extension`](#babashka.fs/extension) - Returns the extension of <code>path</code> via [[split-ext]].
    -  [`file`](#babashka.fs/file) - Coerces arg(s) into a <code>File</code>, combining multiple paths into one.
    -  [`file-name`](#babashka.fs/file-name) - Returns the name of the file or directory via [File#getName](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#getName()).
    -  [`file-separator`](#babashka.fs/file-separator) - The system-dependent default name-separator character (as string).
    -  [`file-time->instant`](#babashka.fs/file-time->instant) - Converts <code>ft</code> [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html) to an [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html).
    -  [`file-time->millis`](#babashka.fs/file-time->millis) - Converts <code>ft</code> [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html) to epoch millis (long).
    -  [`get-attribute`](#babashka.fs/get-attribute) - Return <code>attribute</code> for <code>path</code> via [Files/getAttribute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getAttribute(java.nio.file.Path,java.lang.String,java.nio.file.LinkOption...)) Options: * [<code>:nofollow-links</code>](/README.md#nofollow-links).
    -  [`glob`](#babashka.fs/glob) - Given a file <code>root</code> and glob <code>pattern</code>, returns matches as vector of paths.
    -  [`gunzip`](#babashka.fs/gunzip) - Extracts <code>gz-file</code> to <code>dest</code> directory (default <code>&quot;.&quot;</code>).
    -  [`gzip`](#babashka.fs/gzip) - Gzips <code>source-file</code> and writes the output to <code>dir/out-file</code>.
    -  [`hidden?`](#babashka.fs/hidden?) - Returns true if <code>f</code> is hidden via [Files/isHidden](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isHidden(java.nio.file.Path)).
    -  [`home`](#babashka.fs/home) - With no arguments, returns the current value of the <code>user.home</code> system property as a <code>Path</code>.
    -  [`instant->file-time`](#babashka.fs/instant->file-time) - Converts <code>instant</code> [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html) to a [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).
    -  [`last-modified-time`](#babashka.fs/last-modified-time) - Returns last modified time of <code>f</code> as a [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).
    -  [`list-dir`](#babashka.fs/list-dir) - Returns all paths in <code>dir</code> as vector.
    -  [`list-dirs`](#babashka.fs/list-dirs) - Similar to list-dir but accepts multiple roots in <code>dirs</code> and returns the concatenated results.
    -  [`match`](#babashka.fs/match) - Given a file <code>root</code> and match <code>pattern</code>, returns matches as vector of paths.
    -  [`millis->file-time`](#babashka.fs/millis->file-time) - Converts epoch millis (long) to a [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).
    -  [`modified-since`](#babashka.fs/modified-since) - Returns seq of regular files (non-directories, non-symlinks) from <code>file-set</code> that were modified since the <code>anchor</code> path.
    -  [`move`](#babashka.fs/move) - Move or rename dir or file <code>source</code> to <code>target</code> dir or file via [Files/move](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#move(java.nio.file.Path,java.nio.file.Path,java.nio.file.CopyOption...)).
    -  [`normalize`](#babashka.fs/normalize) - Returns normalize <code>Path</code> for <code>f</code> via [Path#normalize](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#normalize()).
    -  [`owner`](#babashka.fs/owner) - Returns the owner of file <code>f</code> via [Files/getOwner](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getOwner(java.nio.file.Path,java.nio.file.LinkOption...)).
    -  [`parent`](#babashka.fs/parent) - Returns parent of <code>f</code>.
    -  [`path`](#babashka.fs/path) - Coerces arg(s) into a <code>Path</code>, combining multiple paths into one.
    -  [`path-separator`](#babashka.fs/path-separator) - The system-dependent path-separator character (as string).
    -  [`posix->str`](#babashka.fs/posix->str) - Converts a set of <code>PosixFilePermission</code> <code>p</code> to a string.
    -  [`posix-file-permissions`](#babashka.fs/posix-file-permissions) - Returns posix file permissions for <code>f</code>.
    -  [`read-all-bytes`](#babashka.fs/read-all-bytes) - Returns contents of file <code>f</code> as byte array via [Files/readAllBytes](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAllBytes(java.nio.file.Path)).
    -  [`read-all-lines`](#babashka.fs/read-all-lines) - Read all lines from a file <code>f</code> via [Files/readAllLines](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAllLines(java.nio.file.Path,java.nio.charset.Charset)).
    -  [`read-attributes`](#babashka.fs/read-attributes) - Same as [[read-attributes*]] but turns <code>attributes</code> for <code>path</code> into a map and keywordizes keys.
    -  [`read-attributes*`](#babashka.fs/read-attributes*) - Reads <code>attributes</code> for <code>path</code> via [Files/readAttributes](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAttributes(java.nio.file.Path,java.lang.Class,java.nio.file.LinkOption...)).
    -  [`read-link`](#babashka.fs/read-link) - Reads the target of a symbolic link <code>path</code> via [Files/readSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readSymbolicLink(java.nio.file.Path)).
    -  [`readable?`](#babashka.fs/readable?) - Returns true if <code>f</code> is readable via [Files/isReadable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isReadable(java.nio.file.Path)).
    -  [`real-path`](#babashka.fs/real-path) - Converts <code>f</code> into real <code>Path</code> via [Path#toRealPath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#toRealPath(java.nio.file.LinkOption...)).
    -  [`regular-file?`](#babashka.fs/regular-file?) - Returns true if <code>f</code> is a regular file, using [Files/isRegularFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isRegularFile(java.nio.file.Path,java.nio.file.LinkOption...)).
    -  [`relative?`](#babashka.fs/relative?) - Returns true if <code>f</code> represents a relative path (in other words, is not [[absolute?]]).
    -  [`relativize`](#babashka.fs/relativize) - Returns relative <code>Path</code> by comparing <code>this</code> with <code>other</code> via [Path#relativize](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#relativize(java.nio.file.Path)).
    -  [`root`](#babashka.fs/root) - Returns <code>root</code> for <code>path</code> as <code>Path</code>, or <code>nil</code> via [Path#getRoot](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#getRoot()).
    -  [`same-file?`](#babashka.fs/same-file?) - Returns <code>true</code> if <code>this</code> is the same file as <code>other</code> via [Files/isSamefile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isSameFile(java.nio.file.Path,java.nio.file.Path)).
    -  [`set-attribute`](#babashka.fs/set-attribute) - Set <code>attribute</code> for <code>path</code> to <code>value</code> via [Files/setAttribute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#setAttribute(java.nio.file.Path,java.lang.String,java.lang.Object,java.nio.file.LinkOption...)).
    -  [`set-creation-time`](#babashka.fs/set-creation-time) - Sets creation time of <code>f</code> to time (<code>epoch millis</code> or [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)).
    -  [`set-last-modified-time`](#babashka.fs/set-last-modified-time) - Sets last modified time of <code>f</code> to <code>time</code> (<code>epoch millis</code> or [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)).
    -  [`set-posix-file-permissions`](#babashka.fs/set-posix-file-permissions) - Sets <code>posix-file-permissions</code> on <code>f</code>.
    -  [`size`](#babashka.fs/size) - Returns the size of a file (in bytes).
    -  [`split-ext`](#babashka.fs/split-ext) - Splits <code>path</code> on extension.
    -  [`split-paths`](#babashka.fs/split-paths) - Splits a <code>joined-paths</code> list given as a string joined by the OS-specific [[path-separator]] into a vec of paths.
    -  [`starts-with?`](#babashka.fs/starts-with?) - Returns <code>true</code> if path <code>this</code> starts with path <code>other</code> via [Path#startsWith](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#startsWith(java.nio.file.Path)).
    -  [`str->posix`](#babashka.fs/str->posix) - Converts a string <code>s</code> to a set of <code>PosixFilePermission</code>.
    -  [`strip-ext`](#babashka.fs/strip-ext) - Strips extension for <code>path</code> via [[split-ext]].
    -  [`sym-link?`](#babashka.fs/sym-link?) - Determines if <code>f</code> is a symbolic link via [Files/isSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isSymbolicLink(java.nio.file.Path)).
    -  [`temp-dir`](#babashka.fs/temp-dir) - Returns <code>java.io.tmpdir</code> property as path.
    -  [`unixify`](#babashka.fs/unixify) - Returns path as string with Unix-style file separators (<code>/</code>).
    -  [`unzip`](#babashka.fs/unzip) - Unzips <code>zip-file</code> to <code>dest</code> directory (default <code>&quot;.&quot;</code>).
    -  [`update-file`](#babashka.fs/update-file) - Updates the contents of text file <code>path</code> using <code>f</code> applied to old contents and <code>xs</code>.
    -  [`walk-file-tree`](#babashka.fs/walk-file-tree) - Walks <code>f</code> using [Files/walkFileTree](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#walkFileTree(java.nio.file.Path,java.util.Set,int,java.nio.file.FileVisitor)).
    -  [`which`](#babashka.fs/which) - Returns <code>Path</code> to first executable <code>program</code> found in <code>:paths</code> <code>opt</code>, similar to the <code>which</code> Unix command.
    -  [`which-all`](#babashka.fs/which-all) - Returns every <code>Path</code> to <code>program</code> found in ([[exec-paths]]).
    -  [`windows?`](#babashka.fs/windows?) - Returns true if OS is Windows.
    -  [`with-temp-dir`](#babashka.fs/with-temp-dir) - Evaluates body with binding-name bound to the result of <code>(create-temp-dir options)</code>, then cleans up.
    -  [`writable?`](#babashka.fs/writable?) - Returns true if <code>f</code> is writable via [Files/isWritable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isWritable(java.nio.file.Path)).
    -  [`write-bytes`](#babashka.fs/write-bytes) - Writes <code>bytes</code> to <code>path</code> via [Files/write](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#write(java.nio.file.Path,byte%5B%5D,java.nio.file.OpenOption...)).
    -  [`write-lines`](#babashka.fs/write-lines) - Writes <code>lines</code>, a seqable of strings to <code>path</code> via [Files/write](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#write(java.nio.file.Path,java.lang.Iterable,java.nio.charset.Charset,java.nio.file.OpenOption...)).
    -  [`xdg-cache-home`](#babashka.fs/xdg-cache-home) - Path representing the base directory relative to which user-specific non-essential data files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).
    -  [`xdg-config-home`](#babashka.fs/xdg-config-home) - Path representing the base directory relative to which user-specific configuration files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).
    -  [`xdg-data-home`](#babashka.fs/xdg-data-home) - Path representing the base directory relative to which user-specific data files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).
    -  [`xdg-state-home`](#babashka.fs/xdg-state-home) - Path representing the base directory relative to which user-specific state files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).
    -  [`zip`](#babashka.fs/zip) - Zips entry or <code>entries</code> into <code>zip-file</code>.

-----
# <a name="babashka.fs">babashka.fs</a>






## <a name="babashka.fs/absolute?">`absolute?`</a>
``` clojure

(absolute? f)
```
Function.

Returns true if `f` represents an absolute path via [Path#isAbsolute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#isAbsolute()).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L135-L137">Source</a></sub></p>

## <a name="babashka.fs/absolutize">`absolutize`</a>
``` clojure

(absolutize f)
```
Function.

Converts `f` into an absolute `Path` via [Path#toAbsolutePath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#toAbsolutePath()).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L177-L179">Source</a></sub></p>

## <a name="babashka.fs/canonicalize">`canonicalize`</a>
``` clojure

(canonicalize f)
(canonicalize f {:keys [:nofollow-links]})
```
Function.

Returns the canonical `Path` for `f` via [File#getCanonicalPath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#getCanonicalPath()).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links), when set, falls back on [`absolutize`](#babashka.fs/absolutize) + [`normalize`](#babashka.fs/normalize).

  This function can be used as an alternative to [`real-path`](#babashka.fs/real-path) which requires files to exist.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L191-L202">Source</a></sub></p>

## <a name="babashka.fs/components">`components`</a>
``` clojure

(components f)
```
Function.

Returns a seq of all components of `f` as paths.
  i.e.: split on the [`file-separator`](#babashka.fs/file-separator).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L171-L175">Source</a></sub></p>

## <a name="babashka.fs/copy">`copy`</a>
``` clojure

(copy src dest)
(copy src dest {:keys [replace-existing copy-attributes nofollow-links]})
```
Function.

Copies `src` file to `dest` dir or file using [Files/copy](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#copy(java.nio.file.Path,java.nio.file.Path,java.nio.file.CopyOption...)).

  Options:
  * `:replace-existing`
  * `:copy-attributes`
  * [`:nofollow-links`](/README.md#nofollow-links) (used to determine to copy symbolic link itself or not).
  Returns `dest` as path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L447-L467">Source</a></sub></p>

## <a name="babashka.fs/copy-tree">`copy-tree`</a>
``` clojure

(copy-tree src dest)
(copy-tree src dest {:keys [:replace-existing :copy-attributes :nofollow-links], :as opts})
```
Function.

Copies entire file tree from `src` to `dest`. Creates `dest` if needed
  using [`create-dirs`](#babashka.fs/create-dirs), passing it the `:posix-file-permissions`
  option. Supports same options as [`copy`](#babashka.fs/copy).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L568-L621">Source</a></sub></p>

## <a name="babashka.fs/create-dir">`create-dir`</a>
``` clojure

(create-dir path)
(create-dir path {:keys [:posix-file-permissions]})
```
Function.

Creates dir using [Files/createDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createDirectory(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
  Does not create parents.

  Returns created directory as `Path`.

  Options:
  * `:posix-file-permissions` permission for unix-like systems
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L502-L514">Source</a></sub></p>

## <a name="babashka.fs/create-dirs">`create-dirs`</a>
``` clojure

(create-dirs path)
(create-dirs path {:keys [:posix-file-permissions]})
```
Function.

Creates directories for `path` using [Files/createDirectories](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createDirectories(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
  Also creates parents if needed.
  Doesn't throw an exception if the dirs exist already. Similar to `mkdir -p`

  Options:
  * `:posix-file-permissions` permission for unix-like systems
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L516-L528">Source</a></sub></p>

## <a name="babashka.fs/create-file">`create-file`</a>
``` clojure

(create-file path)
(create-file path {:keys [:posix-file-permissions]})
```
Function.

Creates empty file at `path` using [Files/createFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createFile(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).

  Options:
  * `:posix-file-permissions` string format for posix file permissions is described in the [`str->posix`](#babashka.fs/str->posix) docstring.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L779-L788">Source</a></sub></p>

## <a name="babashka.fs/create-link">`create-link`</a>
``` clojure

(create-link link existing)
```
Function.

Create a new `link` (directory entry) for an `existing` file via [Files/createLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createLink(java.nio.file.Path,java.nio.file.Path)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L723-L728">Source</a></sub></p>

## <a name="babashka.fs/create-sym-link">`create-sym-link`</a>
``` clojure

(create-sym-link link target)
```
Function.

Create a symbolic `link` to `target` via [Files/createSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createSymbolicLink(java.nio.file.Path,java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).

  As of this writing, JDKs do not recognize empty-string `target` `""` as the cwd.
  Consider instead using a `target` of `"."` to link to the cwd.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L712-L721">Source</a></sub></p>

## <a name="babashka.fs/create-temp-dir">`create-temp-dir`</a>
``` clojure

(create-temp-dir)
(create-temp-dir {:keys [:dir :prefix :posix-file-permissions], :as opts})
```
Function.

Creates a directory using [Files/createTempDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createTempDirectory(java.nio.file.Path,java.lang.String,java.nio.file.attribute.FileAttribute...)).

  This function does not set up any automatic deletion of the directories it
  creates. See [`with-temp-dir`](#babashka.fs/with-temp-dir) for that functionality.

  Options:
  * `:dir`: Directory in which to create the new directory. Defaults to default
  system temp dir (e.g. `/tmp`); see [`temp-dir`](#babashka.fs/temp-dir). Must already exist.
  * `:prefix`: Provided as a hint to the process that generates the name of the
  new directory. In most cases, this will be the beginning of the new directory
  name. Defaults to a random (v4) UUID.
  * `:posix-file-permissions`: The new directory will be created with these
  permissions, given as a String as described in [`str->posix`](#babashka.fs/str->posix). If not
  specified, uses the file system default permissions for new directories.
  * :warning: `:path` **[DEPRECATED]** Previous name for `:dir`, kept
  for backwards compatibility. If both `:path` and `:dir` are given (don't do
  that!), `:dir` is used.

  Examples:
  * `(create-temp-dir)`
  * `(create-temp-dir {:posix-file-permissions "rwx------"})`
  * `(create-temp-dir {:dir (path (cwd) "_workdir") :prefix "process-1-"})`
  
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L628-L664">Source</a></sub></p>

## <a name="babashka.fs/create-temp-file">`create-temp-file`</a>
``` clojure

(create-temp-file)
(create-temp-file {:keys [:dir :prefix :suffix :posix-file-permissions], :as opts})
```
Function.

Creates an empty file using [Files/createTempFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createTempFile(java.nio.file.Path,java.lang.String,java.lang.String,java.nio.file.attribute.FileAttribute...)).

  This function does not set up any automatic deletion of the files it
  creates. Create the file in a [`with-temp-dir`](#babashka.fs/with-temp-dir) for that functionality.

  Options:
  * `:dir`: Directory in which to create the new file. Defaults to default
  system temp dir (e.g. `/tmp`); see [`temp-dir`](#babashka.fs/temp-dir). Must already exist.
  * `:prefix`: Provided as a hint to the process that generates the name of the
  new file. In most cases, this will be the beginning of the new file name.
  Defaults to a random (v4) UUID.
  * `:suffix`: Provided as a hint to the process that generates the name of the
  new file. In most cases, this will be the end of the new file name.
  Defaults to a random (v4) UUID.
  * `:posix-file-permissions`: The new file will be created with these
  permissions, given as a String as described in [`str->posix`](#babashka.fs/str->posix). If not
  specified, uses the file system default permissions for new files.
  * :warning: `:path` **[DEPRECATED]** Previous name for `:dir`, kept
  for backwards compatibility. If both `:path` and `:dir` are given (don't do
  that!), `:dir` is used.

  Examples:
  * `(create-temp-file)`
  * `(create-temp-file {:posix-file-permissions "rw-------"})`
  * `(create-temp-file {:dir (path (cwd) "_workdir") :prefix "process-1-" :suffix "-queue"})`
  
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L666-L710">Source</a></sub></p>

## <a name="babashka.fs/creation-time">`creation-time`</a>
``` clojure

(creation-time f)
(creation-time f {:keys [nofollow-links], :as opts})
```
Function.

Returns creation time of `f` as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).

  See [README notes](/README.md#creation-time) for some details on behaviour.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L959-L966">Source</a></sub></p>

## <a name="babashka.fs/cwd">`cwd`</a>
``` clojure

(cwd)
```
Function.

Returns current working directory as `Path`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1368-L1371">Source</a></sub></p>

## <a name="babashka.fs/delete">`delete`</a>
``` clojure

(delete f)
```
Function.

Deletes `f` using [Files/delete](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#delete(java.nio.file.Path)).
  Returns `nil` if the delete was successful,
  throws otherwise. Does not follow symlinks.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L736-L743">Source</a></sub></p>

## <a name="babashka.fs/delete-if-exists">`delete-if-exists`</a>
``` clojure

(delete-if-exists f)
```
Function.

Deletes `f` if it exists via [Files/deleteIfExists](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#deleteIfExists(java.nio.file.Path)).
  Returns `true` if the delete was successful,
  `false` if `f` didn't exist. Does not follow symlinks.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L745-L750">Source</a></sub></p>

## <a name="babashka.fs/delete-on-exit">`delete-on-exit`</a>
``` clojure

(delete-on-exit f)
```
Function.

Requests delete of file `f` on exit via [File#deleteOnExit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#deleteOnExit()).
  Returns `f` unaltered.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L824-L829">Source</a></sub></p>

## <a name="babashka.fs/delete-tree">`delete-tree`</a>
``` clojure

(delete-tree root)
(delete-tree root {:keys [force]})
```
Function.

Deletes a file tree `root` using [`walk-file-tree`](#babashka.fs/walk-file-tree). Similar to `rm -rf`. Does not follow symlinks.
   `force` ensures read-only directories/files are deleted. Similar to `chmod -R +wx` + `rm -rf`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L757-L777">Source</a></sub></p>

## <a name="babashka.fs/directory?">`directory?`</a>
``` clojure

(directory? f)
(directory? f {:keys [:nofollow-links]})
```
Function.

Returns true if `f` is a directory, using [Files/isDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isDirectory(java.nio.file.Path,java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L112-L120">Source</a></sub></p>

## <a name="babashka.fs/ends-with?">`ends-with?`</a>
``` clojure

(ends-with? this other)
```
Function.

Returns `true` if path `this` ends with path `other` via [Path#endsWith](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#endsWith(java.nio.file.Path)).

  See also: [`starts-with?`](#babashka.fs/starts-with?)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L561-L566">Source</a></sub></p>

## <a name="babashka.fs/exec-paths">`exec-paths`</a>
``` clojure

(exec-paths)
```
Function.

Returns executable paths (using the `PATH` environment variable). Same
  as `(split-paths (System/getenv "PATH"))`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1024-L1028">Source</a></sub></p>

## <a name="babashka.fs/executable?">`executable?`</a>
``` clojure

(executable? f)
```
Function.

Returns true if `f` has is executable via [Files/isExecutable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isExecutable(java.nio.file.Path)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L139-L141">Source</a></sub></p>

## <a name="babashka.fs/exists?">`exists?`</a>
``` clojure

(exists? f)
(exists? f {:keys [:nofollow-links]})
```
Function.

Returns true if `f` exists via [Files/exists](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#exists(java.nio.file.Path,java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L155-L167">Source</a></sub></p>

## <a name="babashka.fs/expand-home">`expand-home`</a>
``` clojure

(expand-home f)
```
Function.

If `f` begins with a tilde (`~`), expand the tilde to the value
  of the `user.home` system property. If the `f` begins with a
  tilde immediately followed by some characters, they are assumed to
  be a username. This is expanded to the path to that user's home
  directory. This is (naively) assumed to be a directory with the same
  name as the user relative to the parent of the current value of
  `user.home`. Returns a `Path`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1345-L1361">Source</a></sub></p>

## <a name="babashka.fs/extension">`extension`</a>
``` clojure

(extension path)
```
Function.

Returns the extension of `path` via [`split-ext`](#babashka.fs/split-ext).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1013-L1016">Source</a></sub></p>

## <a name="babashka.fs/file">`file`</a>
``` clojure

(file f)
(file f & fs)
```
Function.

Coerces arg(s) into a `File`, combining multiple paths into one.
  Multiple-arg versions treat the first argument as parent and subsequent args
  as children relative to the parent.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L66-L72">Source</a></sub></p>

## <a name="babashka.fs/file-name">`file-name`</a>
``` clojure

(file-name x)
```
Function.

Returns the name of the file or directory via [File#getName](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#getName()).
  E.g. (file-name "foo/bar/baz") returns "baz".
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L219-L223">Source</a></sub></p>

## <a name="babashka.fs/file-separator">`file-separator`</a>




The system-dependent default name-separator character (as string)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L309-L311">Source</a></sub></p>

## <a name="babashka.fs/file-time->instant">`file-time->instant`</a>
``` clojure

(file-time->instant ft)
```
Function.

Converts `ft` [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)
  to an [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L917-L921">Source</a></sub></p>

## <a name="babashka.fs/file-time->millis">`file-time->millis`</a>
``` clojure

(file-time->millis ft)
```
Function.

Converts `ft` [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)
   to epoch millis (long).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L929-L933">Source</a></sub></p>

## <a name="babashka.fs/get-attribute">`get-attribute`</a>
``` clojure

(get-attribute path attribute)
(get-attribute path attribute {:keys [:nofollow-links]})
```
Function.

Return `attribute` for `path` via [Files/getAttribute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getAttribute(java.nio.file.Path,java.lang.String,java.nio.file.LinkOption...))

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L859-L869">Source</a></sub></p>

## <a name="babashka.fs/glob">`glob`</a>
``` clojure

(glob root pattern)
(glob root pattern opts)
```
Function.

Given a file `root` and glob `pattern`, returns matches as vector of
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
  `(fs/glob "." "**.clj")`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L407-L436">Source</a></sub></p>

## <a name="babashka.fs/gunzip">`gunzip`</a>
``` clojure

(gunzip gz-file)
(gunzip gz-file dest)
(gunzip gz-file dest {:keys [replace-existing]})
```
Function.

Extracts `gz-file` to `dest` directory (default `"."`).

   Options:
   * `:replace-existing` - `true` / `false`: overwrite existing files
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1253-L1273">Source</a></sub></p>

## <a name="babashka.fs/gzip">`gzip`</a>
``` clojure

(gzip source-file)
(gzip source-file {:keys [dir out-file], :or {dir "."}})
```
Function.

Gzips `source-file` and writes the output to `dir/out-file`.

  Options:
  * `out-file` if not provided, the `source-file` name with `.gz` appended is used.
  * `dir` if not provided, the current directory is used.

  Returns the created gzip file.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1275-L1299">Source</a></sub></p>

## <a name="babashka.fs/hidden?">`hidden?`</a>
``` clojure

(hidden? f)
```
Function.

Returns true if `f` is hidden via [Files/isHidden](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isHidden(java.nio.file.Path)).

  TIP: some older JDKs can throw on empty-string path `(hidden "")`.
  Consider instead checking cwd via `(hidden ".")`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L128-L133">Source</a></sub></p>

## <a name="babashka.fs/home">`home`</a>
``` clojure

(home)
(home user)
```
Function.

With no arguments, returns the current value of the `user.home`
  system property as a `Path`. If a `user` is passed, returns that user's home
  directory as found in the parent of home with no args.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1337-L1343">Source</a></sub></p>

## <a name="babashka.fs/instant->file-time">`instant->file-time`</a>
``` clojure

(instant->file-time instant)
```
Function.

Converts `instant` [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html)
  to a [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L923-L927">Source</a></sub></p>

## <a name="babashka.fs/last-modified-time">`last-modified-time`</a>
``` clojure

(last-modified-time f)
(last-modified-time f {:keys [nofollow-links], :as opts})
```
Function.

Returns last modified time of `f` as a [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L945-L950">Source</a></sub></p>

## <a name="babashka.fs/list-dir">`list-dir`</a>
``` clojure

(list-dir dir)
(list-dir dir glob-or-accept)
```
Function.

Returns all paths in `dir` as vector. For descending into subdirectories use `glob.`
     - `glob-or-accept` - a glob string such as "*.edn" or a `(fn accept [^java.nio.file.Path p]) -> truthy`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L292-L300">Source</a></sub></p>

## <a name="babashka.fs/list-dirs">`list-dirs`</a>
``` clojure

(list-dirs dirs glob-or-accept)
```
Function.

Similar to list-dir but accepts multiple roots in `dirs` and returns the concatenated results.
  - `glob-or-accept` - a glob string such as `"*.edn"` or a `(fn accept [^java.nio.file.Path p]) -> truthy`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L980-L984">Source</a></sub></p>

## <a name="babashka.fs/match">`match`</a>
``` clojure

(match root pattern)
(match root pattern {:keys [hidden follow-links max-depth recursive]})
```
Function.

Given a file `root` and match `pattern`, returns matches as vector of
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
  `(fs/match "." "regex:.*\\.clj" {:recursive true})`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L336-L405">Source</a></sub></p>

## <a name="babashka.fs/millis->file-time">`millis->file-time`</a>
``` clojure

(millis->file-time millis)
```
Function.

Converts epoch millis (long) to a [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L935-L938">Source</a></sub></p>

## <a name="babashka.fs/modified-since">`modified-since`</a>
``` clojure

(modified-since anchor file-set)
```
Function.

Returns seq of regular files (non-directories, non-symlinks) from `file-set` that were modified since the `anchor` path.
  The `anchor` path can be a regular file or directory, in which case
  the recursive max last modified time stamp is used as the timestamp
  to compare with.  The `file-set` may be a regular file, directory or
  collection of files (e.g. returned by glob). Directories are
  searched recursively.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1134-L1143">Source</a></sub></p>

## <a name="babashka.fs/move">`move`</a>
``` clojure

(move source target)
(move source target {:keys [:replace-existing :atomic-move]})
```
Function.

Move or rename dir or file `source` to `target` dir or file via [Files/move](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#move(java.nio.file.Path,java.nio.file.Path,java.nio.file.CopyOption...)).
  If `target` is a directory, moves `source` under `target`.
  Never follows symbolic links.

  Returns `target` as `Path`.

  Options:
  * `replace-existing` - overwrite existing `target`, default `false`
  * `atomic-move` - watchers will only see complete `target` file, default `false`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L790-L812">Source</a></sub></p>

## <a name="babashka.fs/normalize">`normalize`</a>
``` clojure

(normalize f)
```
Function.

Returns normalize `Path` for `f` via [Path#normalize](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#normalize()).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L186-L189">Source</a></sub></p>

## <a name="babashka.fs/owner">`owner`</a>
``` clojure

(owner f)
(owner f {:keys [:nofollow-links]})
```
Function.

Returns the owner of file `f` via [Files/getOwner](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getOwner(java.nio.file.Path,java.nio.file.LinkOption...)).
  Call `str` on return value to get the owner name as a string.

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L90-L98">Source</a></sub></p>

## <a name="babashka.fs/parent">`parent`</a>
``` clojure

(parent f)
```
Function.

Returns parent of `f`. Akin to `dirname` in bash.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L814-L817">Source</a></sub></p>

## <a name="babashka.fs/path">`path`</a>
``` clojure

(path f)
(path parent child)
(path parent child & more)
```
Function.

Coerces arg(s) into a `Path`, combining multiple paths into one.
  Multiple-arg versions treat the first argument as parent and subsequent
  args as children relative to the parent.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L51-L64">Source</a></sub></p>

## <a name="babashka.fs/path-separator">`path-separator`</a>




The system-dependent path-separator character (as string).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L312-L314">Source</a></sub></p>

## <a name="babashka.fs/posix->str">`posix->str`</a>
``` clojure

(posix->str p)
```
Function.

Converts a set of `PosixFilePermission` `p` to a string.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L469-L472">Source</a></sub></p>

## <a name="babashka.fs/posix-file-permissions">`posix-file-permissions`</a>
``` clojure

(posix-file-permissions f)
(posix-file-permissions f {:keys [:nofollow-links]})
```
Function.

Returns posix file permissions for `f`. Use [`posix->str`](#babashka.fs/posix->str) to view as a string.

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L535-L542">Source</a></sub></p>

## <a name="babashka.fs/read-all-bytes">`read-all-bytes`</a>
``` clojure

(read-all-bytes f)
```
Function.

Returns contents of file `f` as byte array via [Files/readAllBytes](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAllBytes(java.nio.file.Path)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L836-L840">Source</a></sub></p>

## <a name="babashka.fs/read-all-lines">`read-all-lines`</a>
``` clojure

(read-all-lines f)
(read-all-lines f {:keys [charset], :or {charset "utf-8"}})
```
Function.

Read all lines from a file `f` via [Files/readAllLines](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAllLines(java.nio.file.Path,java.nio.charset.Charset)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L847-L855">Source</a></sub></p>

## <a name="babashka.fs/read-attributes">`read-attributes`</a>
``` clojure

(read-attributes path attributes)
(read-attributes path attributes {:keys [:nofollow-links :key-fn], :as opts})
```
Function.

Same as [`read-attributes*`](#babashka.fs/read-attributes*) but turns `attributes` for `path` into a map and keywordizes keys.
  Keywordizing can be changed by passing a `:key-fn` in the `opts` map.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L897-L905">Source</a></sub></p>

## <a name="babashka.fs/read-attributes*">`read-attributes*`</a>
``` clojure

(read-attributes* path attributes)
(read-attributes* path attributes {:keys [:nofollow-links]})
```
Function.

Reads `attributes` for `path` via [Files/readAttributes](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAttributes(java.nio.file.Path,java.lang.Class,java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L876-L895">Source</a></sub></p>

## <a name="babashka.fs/read-link">`read-link`</a>
``` clojure

(read-link path)
```
Function.

Reads the target of a symbolic link `path` via [Files/readSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readSymbolicLink(java.nio.file.Path)).
  The target need not exist.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L730-L734">Source</a></sub></p>

## <a name="babashka.fs/readable?">`readable?`</a>
``` clojure

(readable? f)
```
Function.

Returns true if `f` is readable via [Files/isReadable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isReadable(java.nio.file.Path))
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L143-L145">Source</a></sub></p>

## <a name="babashka.fs/real-path">`real-path`</a>
``` clojure

(real-path f)
(real-path f {:keys [:nofollow-links]})
```
Function.

Converts `f` into real `Path` via [Path#toRealPath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#toRealPath(java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L81-L88">Source</a></sub></p>

## <a name="babashka.fs/regular-file?">`regular-file?`</a>
``` clojure

(regular-file? f)
(regular-file? f {:keys [:nofollow-links]})
```
Function.

Returns true if `f` is a regular file, using [Files/isRegularFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isRegularFile(java.nio.file.Path,java.nio.file.LinkOption...)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L102-L110">Source</a></sub></p>

## <a name="babashka.fs/relative?">`relative?`</a>
``` clojure

(relative? f)
```
Function.

Returns true if `f` represents a relative path (in other words, is not [`absolute?`](#babashka.fs/absolute?)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L151-L153">Source</a></sub></p>

## <a name="babashka.fs/relativize">`relativize`</a>
``` clojure

(relativize this other)
```
Function.

Returns relative `Path` by comparing `this` with `other` via [Path#relativize](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#relativize(java.nio.file.Path)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L181-L184">Source</a></sub></p>

## <a name="babashka.fs/root">`root`</a>
``` clojure

(root path)
```
Function.

Returns `root` for `path` as `Path`, or `nil` via [Path#getRoot](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#getRoot()).

  The return value depends upon the runtime platform.

  On Windows, returns Windows specific roots, ex:
  (replace forward slash with backslash):
  * `C:/` for `C:/foo/bar`
  * `C:`  for `C:foo/bar` 
  * `//server/share` for `//server/share/foo/bar`

  On Linux and macOS, returns the leading `/` for anything that looks like an absolute path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L204-L217">Source</a></sub></p>

## <a name="babashka.fs/same-file?">`same-file?`</a>
``` clojure

(same-file? this other)
```
Function.

Returns `true` if `this` is the same file as `other` via [Files/isSamefile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isSameFile(java.nio.file.Path,java.nio.file.Path)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L831-L834">Source</a></sub></p>

## <a name="babashka.fs/set-attribute">`set-attribute`</a>
``` clojure

(set-attribute path attribute value)
(set-attribute path attribute value {:keys [:nofollow-links]})
```
Function.

Set `attribute` for `path` to `value` via [Files/setAttribute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#setAttribute(java.nio.file.Path,java.lang.String,java.lang.Object,java.nio.file.LinkOption...))
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L907-L915">Source</a></sub></p>

## <a name="babashka.fs/set-creation-time">`set-creation-time`</a>
``` clojure

(set-creation-time f time)
(set-creation-time f time {:keys [nofollow-links], :as opts})
```
Function.

Sets creation time of `f` to time (`epoch millis` or [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)).

  Options:
  * [`:nofollow-links`](/README.md#nofollow-links)

  See [README notes](/README.md#set-creation-time) for some details on behaviour.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L968-L978">Source</a></sub></p>

## <a name="babashka.fs/set-last-modified-time">`set-last-modified-time`</a>
``` clojure

(set-last-modified-time f time)
(set-last-modified-time f time {:keys [nofollow-links], :as opts})
```
Function.

Sets last modified time of `f` to `time` (`epoch millis` or [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L952-L957">Source</a></sub></p>

## <a name="babashka.fs/set-posix-file-permissions">`set-posix-file-permissions`</a>
``` clojure

(set-posix-file-permissions f posix-file-permissions)
```
Function.

Sets `posix-file-permissions` on `f`. Accepts a string like `"rwx------"` or a set of `PosixFilePermission`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L530-L533">Source</a></sub></p>

## <a name="babashka.fs/size">`size`</a>
``` clojure

(size f)
```
Function.

Returns the size of a file (in bytes).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L819-L822">Source</a></sub></p>

## <a name="babashka.fs/split-ext">`split-ext`</a>
``` clojure

(split-ext path)
(split-ext path {:keys [ext]})
```
Function.

Splits `path` on extension. If provided, a specific extension `ext`, the
  extension (without dot), will be used for splitting.  Directories
  are not processed.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L986-L1004">Source</a></sub></p>

## <a name="babashka.fs/split-paths">`split-paths`</a>
``` clojure

(split-paths joined-paths)
```
Function.

Splits a `joined-paths` list given as a string joined by the OS-specific [`path-separator`](#babashka.fs/path-separator) into a vec of paths.
  On UNIX systems, the separator is ':', on Microsoft Windows systems it is ';'.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1018-L1022">Source</a></sub></p>

## <a name="babashka.fs/starts-with?">`starts-with?`</a>
``` clojure

(starts-with? this other)
```
Function.

Returns `true` if path `this` starts with path `other` via [Path#startsWith](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#startsWith(java.nio.file.Path)).

  See also: [`ends-with?`](#babashka.fs/ends-with?)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L554-L559">Source</a></sub></p>

## <a name="babashka.fs/str->posix">`str->posix`</a>
``` clojure

(str->posix s)
```
Function.

Converts a string `s` to a set of `PosixFilePermission`.

  `s` is a string like `"rwx------"`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L474-L479">Source</a></sub></p>

## <a name="babashka.fs/strip-ext">`strip-ext`</a>
``` clojure

(strip-ext path)
(strip-ext path {:keys [ext], :as opts})
```
Function.

Strips extension for `path` via [`split-ext`](#babashka.fs/split-ext).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1006-L1011">Source</a></sub></p>

## <a name="babashka.fs/sym-link?">`sym-link?`</a>
``` clojure

(sym-link? f)
```
Function.

Determines if `f` is a symbolic link via [Files/isSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isSymbolicLink(java.nio.file.Path)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L752-L755">Source</a></sub></p>

## <a name="babashka.fs/temp-dir">`temp-dir`</a>
``` clojure

(temp-dir)
```
Function.

Returns `java.io.tmpdir` property as path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L623-L626">Source</a></sub></p>

## <a name="babashka.fs/unixify">`unixify`</a>
``` clojure

(unixify f)
```
Function.

Returns path as string with Unix-style file separators (`/`).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1460-L1466">Source</a></sub></p>

## <a name="babashka.fs/unzip">`unzip`</a>
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
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1149-L1187">Source</a></sub></p>

## <a name="babashka.fs/update-file">`update-file`</a>
``` clojure

(update-file path f & xs)
(update-file path opts f & xs)
```
Function.

Updates the contents of text file `path` using `f` applied to old contents and `xs`.
  Returns the new contents.

  Options:
  * `:charset` - charset of file, default to "utf-8"
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1441-L1458">Source</a></sub></p>

## <a name="babashka.fs/walk-file-tree">`walk-file-tree`</a>
``` clojure

(walk-file-tree f {:keys [:pre-visit-dir :post-visit-dir :visit-file :visit-file-failed :follow-links :max-depth]})
```
Function.

Walks `f` using [Files/walkFileTree](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#walkFileTree(java.nio.file.Path,java.util.Set,int,java.nio.file.FileVisitor)).

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

  Returns `f` as `Path`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L227-L272">Source</a></sub></p>

## <a name="babashka.fs/which">`which`</a>
``` clojure

(which program)
(which program opts)
```
Function.

Returns `Path` to first executable `program` found in `:paths` `opt`, similar to the `which` Unix command.
  Default for `:paths` is ([`exec-paths`](#babashka.fs/exec-paths)).

  On Windows, searches for `program` with filename extensions specified in `:win-exts` option.
  Default is `["com" "exe" "bat" "cmd"]`.
  If `program` already includes an extension from `:win-exts`, it will be searched as-is first.

  When `program` is a relative or absolute path, `:paths` option is not consulted. On Windows, the `:win-exts`
  variants are still searched. On other OSes, the path for `program` will be returned if executable,
  else `nil`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1037-L1089">Source</a></sub></p>

## <a name="babashka.fs/which-all">`which-all`</a>
``` clojure

(which-all program)
(which-all program opts)
```
Function.

Returns every `Path` to `program` found in ([`exec-paths`](#babashka.fs/exec-paths)). See [`which`](#babashka.fs/which).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1091-L1095">Source</a></sub></p>

## <a name="babashka.fs/windows?">`windows?`</a>
``` clojure

(windows?)
```
Function.

Returns true if OS is Windows.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1363-L1366">Source</a></sub></p>

## <a name="babashka.fs/with-temp-dir">`with-temp-dir`</a>
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
  
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1303-L1329">Source</a></sub></p>

## <a name="babashka.fs/writable?">`writable?`</a>
``` clojure

(writable? f)
```
Function.

Returns true if `f` is writable via [Files/isWritable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isWritable(java.nio.file.Path))
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L147-L149">Source</a></sub></p>

## <a name="babashka.fs/write-bytes">`write-bytes`</a>
``` clojure

(write-bytes path bytes)
(write-bytes path bytes {:keys [append create truncate-existing write], :as opts})
```
Function.

Writes `bytes` to `path` via [Files/write](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#write(java.nio.file.Path,byte%5B%5D,java.nio.file.OpenOption...)).

  Options:
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
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1394-L1418">Source</a></sub></p>

## <a name="babashka.fs/write-lines">`write-lines`</a>
``` clojure

(write-lines path lines)
(write-lines path lines {:keys [charset], :or {charset "utf-8"}, :as opts})
```
Function.

Writes `lines`, a seqable of strings to `path` via [Files/write](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#write(java.nio.file.Path,java.lang.Iterable,java.nio.charset.Charset,java.nio.file.OpenOption...)).

  Options:
  * `:charset` (default `"utf-8"`)

  Open options:
  * `:create` (default `true`)
  * `:truncate-existing` (default `true`)
  * `:write` (default `true`)
  * `:append` (default `false`)
  * or any `java.nio.file.StandardOption`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1420-L1439">Source</a></sub></p>

## <a name="babashka.fs/xdg-cache-home">`xdg-cache-home`</a>
``` clojure

(xdg-cache-home)
(xdg-cache-home app)
```
Function.

Path representing the base directory relative to which user-specific non-essential data files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_CACHE_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) ".cache")`.
  When provided, appends `app` to the path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1498-L1506">Source</a></sub></p>

## <a name="babashka.fs/xdg-config-home">`xdg-config-home`</a>
``` clojure

(xdg-config-home)
(xdg-config-home app)
```
Function.

Path representing the base directory relative to which user-specific configuration files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_CONFIG_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) ".config")`.
  When provided, appends `app` to the path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1488-L1496">Source</a></sub></p>

## <a name="babashka.fs/xdg-data-home">`xdg-data-home`</a>
``` clojure

(xdg-data-home)
(xdg-data-home app)
```
Function.

Path representing the base directory relative to which user-specific data files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_DATA_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) ".local" "share")`.
  When provided, appends `app` to the path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1508-L1516">Source</a></sub></p>

## <a name="babashka.fs/xdg-state-home">`xdg-state-home`</a>
``` clojure

(xdg-state-home)
(xdg-state-home app)
```
Function.

Path representing the base directory relative to which user-specific state files should be stored as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

  Returns path based on the value of env-var `XDG_STATE_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) ".local" "state")`.
  When provided, appends `app` to the path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1518-L1526">Source</a></sub></p>

## <a name="babashka.fs/zip">`zip`</a>
``` clojure

(zip zip-file entries)
(zip zip-file entries opts)
```
Function.

Zips entry or `entries` into `zip-file`. An entry may be a file or
  directory. Directories are included recursively and their names are
  preserved in the zip file. Currently only accepts relative entries.

  Options:
  * `:root`: directory which will be elided in zip. E.g.: `(fs/zip ["src"] {:root "src"})`
  * `:path-fn`: a single-arg function from file system path to zip entry path.
  
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1220-L1247">Source</a></sub></p>
