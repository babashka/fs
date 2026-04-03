# Table of contents
-  [`babashka.fs`](#babashka.fs) 
    -  [`absolute?`](#babashka.fs/absolute?) - Returns <code>true</code> if <code>path</code> is absolute via [Path#isAbsolute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#isAbsolute()).
    -  [`absolutize`](#babashka.fs/absolutize) - Returns absolute path for <code>path</code> via [Path#toAbsolutePath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#toAbsolutePath()).
    -  [`canonicalize`](#babashka.fs/canonicalize) - Returns canonical path for <code>path</code> via [File#getCanonicalPath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#getCanonicalPath()).
    -  [`components`](#babashka.fs/components) - Returns a seq of paths for all components of <code>path</code>.
    -  [`copy`](#babashka.fs/copy) - Copies <code>source-file</code> to <code>target-path</code> dir or file via [Files/copy](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#copy(java.nio.file.Path,java.nio.file.Path,java.nio.file.CopyOption...)).
    -  [`copy-tree`](#babashka.fs/copy-tree) - Copies entire file tree from <code>source-dir</code> to <code>target-dir</code>.
    -  [`create-dir`](#babashka.fs/create-dir) - Returns new <code>dir</code> created via [Files/createDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createDirectory(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
    -  [`create-dirs`](#babashka.fs/create-dirs) - Returns <code>dir</code> after creating directories for <code>dir</code> via [Files/createDirectories](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createDirectories(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
    -  [`create-file`](#babashka.fs/create-file) - Returns new empty <code>file</code> created via [Files/createFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createFile(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
    -  [`create-link`](#babashka.fs/create-link) - Returns a new hard <code>link</code> (directory entry) for an <code>existing-file</code> created via [Files/createLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createLink(java.nio.file.Path,java.nio.file.Path)).
    -  [`create-sym-link`](#babashka.fs/create-sym-link) - Returns new symbolic <code>link</code> to <code>target-path</code> created via [Files/createSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createSymbolicLink(java.nio.file.Path,java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
    -  [`create-temp-dir`](#babashka.fs/create-temp-dir) - Returns path to directory created via [Files/createTempDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createTempDirectory(java.nio.file.Path,java.lang.String,java.nio.file.attribute.FileAttribute...)).
    -  [`create-temp-file`](#babashka.fs/create-temp-file) - Returns path to empty file created via [Files/createTempFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createTempFile(java.nio.file.Path,java.lang.String,java.lang.String,java.nio.file.attribute.FileAttribute...)).
    -  [`creation-time`](#babashka.fs/creation-time) - Returns creation time of <code>path</code> as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).
    -  [`cwd`](#babashka.fs/cwd) - Returns current working directory path.
    -  [`delete`](#babashka.fs/delete) - Deletes <code>path</code> via [Files/delete](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#delete(java.nio.file.Path)).
    -  [`delete-if-exists`](#babashka.fs/delete-if-exists) - Deletes <code>path</code> if it exists via [Files/deleteIfExists](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#deleteIfExists(java.nio.file.Path)).
    -  [`delete-on-exit`](#babashka.fs/delete-on-exit) - Requests delete of <code>path</code> on exit via [File#deleteOnExit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#deleteOnExit()).
    -  [`delete-tree`](#babashka.fs/delete-tree) - Deletes the file tree at <code>root-path</code> using [<code>walk-file-tree</code>](#babashka.fs/walk-file-tree).
    -  [`directory?`](#babashka.fs/directory?) - Returns <code>true</code> if <code>path</code> is a directory via [Files/isDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isDirectory(java.nio.file.Path,java.nio.file.LinkOption...)).
    -  [`ends-with?`](#babashka.fs/ends-with?) - Returns <code>true</code> if <code>this-path</code> ends with <code>other-path</code> via [Path#endsWith](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#endsWith(java.nio.file.Path)).
    -  [`exec-paths`](#babashka.fs/exec-paths) - Returns a vector of command search paths (from the <code>PATH</code> environment variable).
    -  [`executable?`](#babashka.fs/executable?) - Returns <code>true</code> if <code>path</code> is executable via [Files/isExecutable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isExecutable(java.nio.file.Path)).
    -  [`exists?`](#babashka.fs/exists?) - Returns <code>true</code> if <code>path</code> exists via [Files/exists](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#exists(java.nio.file.Path,java.nio.file.LinkOption...)).
    -  [`expand-home`](#babashka.fs/expand-home) - Returns <code>path</code> replacing <code>~</code> (tilde) with home dir.
    -  [`extension`](#babashka.fs/extension) - Returns the extension of <code>path</code> via [<code>split-ext</code>](#babashka.fs/split-ext).
    -  [`file`](#babashka.fs/file) - Returns <code>path</code>(s) coerced to a <code>File</code>, combining multiple paths into one.
    -  [`file-name`](#babashka.fs/file-name) - Returns the name of the file or directory for <code>path</code> via [File#getName](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#getName()).
    -  [`file-separator`](#babashka.fs/file-separator) - The system-dependent default path component separator character (as string).
    -  [`file-time->instant`](#babashka.fs/file-time->instant) - Returns [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html) <code>ft</code> as [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html).
    -  [`file-time->millis`](#babashka.fs/file-time->millis) - Returns [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html) <code>ft</code> as epoch milliseconds (long).
    -  [`get-attribute`](#babashka.fs/get-attribute) - Returns <code>attribute</code> for <code>path</code> via [Files/getAttribute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getAttribute(java.nio.file.Path,java.lang.String,java.nio.file.LinkOption...)).
    -  [`glob`](#babashka.fs/glob) - Returns a vector of paths matching glob <code>pattern</code> (on path and filename) relative to <code>root-dir</code>.
    -  [`gunzip`](#babashka.fs/gunzip) - Extracts <code>gz-file</code> to <code>target-dir</code>.
    -  [`gzip`](#babashka.fs/gzip) - Gzips <code>source-file</code> to <code>:dir</code>/<code>:out-file</code>.
    -  [`hidden?`](#babashka.fs/hidden?) - Returns <code>true</code> if <code>path</code> is hidden via [Files/isHidden](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isHidden(java.nio.file.Path)).
    -  [`home`](#babashka.fs/home) - Returns home dir path.
    -  [`instant->file-time`](#babashka.fs/instant->file-time) - Returns [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html) <code>instant</code> as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).
    -  [`last-modified-time`](#babashka.fs/last-modified-time) - Returns last modified time of <code>path</code> as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).
    -  [`list-dir`](#babashka.fs/list-dir) - Returns a vector of all paths in <code>dir</code>.
    -  [`list-dirs`](#babashka.fs/list-dirs) - Similar to [<code>list-dir</code>](#babashka.fs/list-dir) but accepts multiple roots in <code>dirs</code> and returns the concatenated results.
    -  [`match`](#babashka.fs/match) - Returns a vector of paths matching <code>pattern</code> (on path and filename) relative to <code>root-dir</code>.
    -  [`millis->file-time`](#babashka.fs/millis->file-time) - Returns epoch milliseconds (long) as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).
    -  [`modified-since`](#babashka.fs/modified-since) - Returns seq of regular files (non-directories, non-symlinks) from <code>path-set</code> that were modified since the <code>anchor-path</code>.
    -  [`move`](#babashka.fs/move) - Moves or renames dir or file at <code>source-path</code> to <code>target-path</code> dir or file via [Files/move](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#move(java.nio.file.Path,java.nio.file.Path,java.nio.file.CopyOption...)).
    -  [`normalize`](#babashka.fs/normalize) - Returns normalized path for <code>path</code> via [Path#normalize](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#normalize()).
    -  [`owner`](#babashka.fs/owner) - Returns the owner of <code>path</code> via [Files/getOwner](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getOwner(java.nio.file.Path,java.nio.file.LinkOption...)).
    -  [`parent`](#babashka.fs/parent) - Returns parent path of <code>path</code> via [Paths/getParent](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#getParent()).
    -  [`path`](#babashka.fs/path) - Returns <code>path</code>(s) coerced to a <code>Path</code>, combining multiple paths into one.
    -  [`path-separator`](#babashka.fs/path-separator) - The system-dependent path-separator character (as string).
    -  [`posix->str`](#babashka.fs/posix->str) - Returns permissions string, like <code>&quot;rwx------&quot;</code>, for a set of <code>PosixFilePermission</code> <code>p</code>.
    -  [`posix-file-permissions`](#babashka.fs/posix-file-permissions) - Returns a set of <code>PosixFilePermissions</code> for <code>path</code> via [Files/getPosixFilePermissions](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getPosixFilePermissions(java.nio.file.Path,java.nio.file.LinkOption...)).
    -  [`read-all-bytes`](#babashka.fs/read-all-bytes) - Returns contents of <code>file</code> as byte array via [Files/readAllBytes](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAllBytes(java.nio.file.Path)).
    -  [`read-all-lines`](#babashka.fs/read-all-lines) - Return contents of <code>file</code> as a vector of lines via [Files/readAllLines](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAllLines(java.nio.file.Path,java.nio.charset.Charset)).
    -  [`read-attributes`](#babashka.fs/read-attributes) - Same as [<code>read-attributes*</code>](#babashka.fs/read-attributes*) but returns requested <code>attributes</code> for <code>path</code> as a map with keywordized attribute keys.
    -  [`read-attributes*`](#babashka.fs/read-attributes*) - Returns requested <code>attributes</code> for <code>path</code> via [Files/readAttributes](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAttributes(java.nio.file.Path,java.lang.Class,java.nio.file.LinkOption...)).
    -  [`read-link`](#babashka.fs/read-link) - Returns the immediate target of <code>sym-link-path</code> via [Files/readSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readSymbolicLink(java.nio.file.Path)).
    -  [`readable?`](#babashka.fs/readable?) - Returns <code>true</code> if <code>path</code> is readable via [Files/isReadable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isReadable(java.nio.file.Path)).
    -  [`real-path`](#babashka.fs/real-path) - Returns real path for <code>path</code> via [Path#toRealPath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#toRealPath(java.nio.file.LinkOption...)).
    -  [`regular-file?`](#babashka.fs/regular-file?) - Returns <code>true</code> if <code>path</code> is a regular file via [Files/isRegularFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isRegularFile(java.nio.file.Path,java.nio.file.LinkOption...)).
    -  [`relative?`](#babashka.fs/relative?) - Returns <code>true</code> if <code>path</code> is relative (in other words, is not [<code>absolute?</code>](#babashka.fs/absolute?)).
    -  [`relativize`](#babashka.fs/relativize) - Returns <code>other-path</code> relative to <code>base-path</code> via [Path#relativize](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#relativize(java.nio.file.Path)).
    -  [`root`](#babashka.fs/root) - Returns root path for <code>path</code>, or <code>nil</code>, via [Path#getRoot](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#getRoot()).
    -  [`same-file?`](#babashka.fs/same-file?) - Returns <code>true</code> if <code>this-path</code> is the same file as <code>other-path</code> via [Files/isSamefile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isSameFile(java.nio.file.Path,java.nio.file.Path)).
    -  [`set-attribute`](#babashka.fs/set-attribute) - Sets <code>attribute</code> for <code>path</code> to <code>value</code> via [Files/setAttribute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#setAttribute(java.nio.file.Path,java.lang.String,java.lang.Object,java.nio.file.LinkOption...)).
    -  [`set-creation-time`](#babashka.fs/set-creation-time) - Sets creation <code>time</code> of <code>path</code>.
    -  [`set-last-modified-time`](#babashka.fs/set-last-modified-time) - Sets last modified <code>time</code> of <code>path</code>.
    -  [`set-posix-file-permissions`](#babashka.fs/set-posix-file-permissions) - Sets <code>posix-file-permissions</code> on <code>path</code> via [Files/setPosixFilePermissions](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#setPosixFilePermissions(java.nio.file.Path,java.util.Set)).
    -  [`size`](#babashka.fs/size) - Returns the size of <code>path</code> in bytes.
    -  [`split-ext`](#babashka.fs/split-ext) - Returns <code>path</code> split on extension.
    -  [`split-paths`](#babashka.fs/split-paths) - Returns a vector of paths from paths in <code>joined-paths</code> string.
    -  [`starts-with?`](#babashka.fs/starts-with?) - Returns <code>true</code> if <code>this-path</code> starts with <code>other-path</code> via [Path#startsWith](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#startsWith(java.nio.file.Path)).
    -  [`str->posix`](#babashka.fs/str->posix) - Returns a set of <code>PosixFilePermission</code> for permissions string <code>s</code>.
    -  [`strip-ext`](#babashka.fs/strip-ext) - Returns <code>path</code> with extension stripped via [<code>split-ext</code>](#babashka.fs/split-ext).
    -  [`sym-link?`](#babashka.fs/sym-link?) - Returns <code>true</code> if <code>path</code> is a symbolic link via [Files/isSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isSymbolicLink(java.nio.file.Path)).
    -  [`temp-dir`](#babashka.fs/temp-dir) - Returns <code>java.io.tmpdir</code> property as path.
    -  [`touch`](#babashka.fs/touch) - Update last modified time of <code>path</code> to <code>:time</code>, creating <code>path</code> as a file if it does not exist.
    -  [`unixify`](#babashka.fs/unixify) - Returns <code>path</code> as string with Unix-style file separators (<code>/</code>).
    -  [`unzip`](#babashka.fs/unzip) - Unzips <code>zip-file</code> to <code>target-dir</code> (default <code>&quot;.&quot;</code>).
    -  [`update-file`](#babashka.fs/update-file) - Updates the contents of text <code>file</code> with result of applying function <code>f</code> with old contents and args <code>xs</code>.
    -  [`walk-file-tree`](#babashka.fs/walk-file-tree) - Walks <code>path</code> via [Files/walkFileTree](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#walkFileTree(java.nio.file.Path,java.util.Set,int,java.nio.file.FileVisitor)).
    -  [`which`](#babashka.fs/which) - Returns path to first executable <code>program</code> found in <code>:paths</code>, similar to the <code>which</code> Unix command.
    -  [`which-all`](#babashka.fs/which-all) - Returns a vector of every path to <code>program</code> found in ([<code>exec-paths</code>](#babashka.fs/exec-paths)).
    -  [`windows?`](#babashka.fs/windows?) - Returns <code>true</code> if OS is Windows.
    -  [`with-temp-dir`](#babashka.fs/with-temp-dir) - Evaluates body with <code>temp-dir</code> bound to the result of <code>(create-temp-dir opts)</code>.
    -  [`writable?`](#babashka.fs/writable?) - Returns <code>true</code> if <code>path</code> is writable via [Files/isWritable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isWritable(java.nio.file.Path)).
    -  [`write-bytes`](#babashka.fs/write-bytes) - Writes <code>bytes</code> to <code>file</code> via [Files/write](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#write(java.nio.file.Path,byte%5B%5D,java.nio.file.OpenOption...)).
    -  [`write-lines`](#babashka.fs/write-lines) - Writes <code>lines</code>, a seqable of strings, to <code>file</code> via [Files/write](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#write(java.nio.file.Path,java.lang.Iterable,java.nio.charset.Charset,java.nio.file.OpenOption...)).
    -  [`xdg-cache-home`](#babashka.fs/xdg-cache-home) - Returns path to user-specific non-essential data as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).
    -  [`xdg-config-home`](#babashka.fs/xdg-config-home) - Returns path to user-specific configuration files as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).
    -  [`xdg-data-home`](#babashka.fs/xdg-data-home) - Returns path to user-specific data files as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).
    -  [`xdg-state-home`](#babashka.fs/xdg-state-home) - Returns path to user-specific state files as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).
    -  [`zip`](#babashka.fs/zip) - Zips <code>path-or-paths</code> into <code>zip-file</code>.

-----
# <a name="babashka.fs">babashka.fs</a>






## <a name="babashka.fs/absolute?">`absolute?`</a>
``` clojure
(absolute? path)
```
Function.

Returns `true` if `path` is absolute via [Path#isAbsolute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#isAbsolute()).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L137-L139">Source</a></sub></p>

## <a name="babashka.fs/absolutize">`absolutize`</a>
``` clojure
(absolutize path)
```
Function.

Returns absolute path for `path` via [Path#toAbsolutePath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#toAbsolutePath()).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L179-L181">Source</a></sub></p>

## <a name="babashka.fs/canonicalize">`canonicalize`</a>
``` clojure
(canonicalize path)
(canonicalize path {:keys [:nofollow-links]})
```
Function.

Returns canonical path for `path` via [File#getCanonicalPath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#getCanonicalPath()).

Options:
* [`:nofollow-links`](/README.md#nofollow-links) - when set, falls back on [`absolutize`](#babashka.fs/absolutize) + [`normalize`](#babashka.fs/normalize).

This function can be used as an alternative to [`real-path`](#babashka.fs/real-path) which requires files to exist.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L197-L208">Source</a></sub></p>

## <a name="babashka.fs/components">`components`</a>
``` clojure
(components path)
```
Function.

Returns a seq of paths for all components of `path`.
i.e.: split on the [`file-separator`](#babashka.fs/file-separator).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L173-L177">Source</a></sub></p>

## <a name="babashka.fs/copy">`copy`</a>
``` clojure
(copy source-file target-path)
(copy source-file target-path {:keys [replace-existing copy-attributes nofollow-links]})
```
Function.

Copies `source-file` to `target-path` dir or file via [Files/copy](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#copy(java.nio.file.Path,java.nio.file.Path,java.nio.file.CopyOption...)).

Options:
* `:replace-existing`
* `:copy-attributes`
* [`:nofollow-links`](/README.md#nofollow-links) - used to determine to copy symbolic link itself or not.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L465-L484">Source</a></sub></p>

## <a name="babashka.fs/copy-tree">`copy-tree`</a>
``` clojure
(copy-tree source-dir target-dir)
(copy-tree source-dir target-dir {:keys [:replace-existing :copy-attributes :nofollow-links], :as opts})
```
Function.

Copies entire file tree from `source-dir` to `target-dir`. Creates `target-dir` if needed.
 
Options:
* same as [`copy`](#babashka.fs/copy)
* `:posix-file-permissions` - string format unix-like system permissions passed to [`create-dirs`](#babashka.fs/create-dirs) when creating `target-dir`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L595-L650">Source</a></sub></p>

## <a name="babashka.fs/create-dir">`create-dir`</a>
``` clojure
(create-dir dir)
(create-dir dir {:keys [:posix-file-permissions]})
```
Function.

Returns new `dir` created via [Files/createDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createDirectory(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
Does not create parents.

Options:
* `:posix-file-permissions` - string format for unix-like system permissions for `dir`, as described in [`str->posix`](#babashka.fs/str->posix).
Affected by [umask](/README.md#umask).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L523-L534">Source</a></sub></p>

## <a name="babashka.fs/create-dirs">`create-dirs`</a>
``` clojure
(create-dirs dir)
(create-dirs dir {:keys [:posix-file-permissions]})
```
Function.

Returns `dir` after creating directories for `dir` via [Files/createDirectories](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createDirectories(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).
Also creates parents if needed.
Does not throw an exception if the dirs exist already. Similar to `mkdir -p` shell command.
  
Options:
* `:posix-file-permissions` - string format for unix-like system permissions for `dir`, as described in [`str->posix`](#babashka.fs/str->posix).
Affected by [umask](/README.md#umask).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L536-L549">Source</a></sub></p>

## <a name="babashka.fs/create-file">`create-file`</a>
``` clojure
(create-file file)
(create-file file {:keys [:posix-file-permissions]})
```
Function.

Returns new empty `file` created via [Files/createFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createFile(java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).

Options:
* `:posix-file-permissions` - string format for unix-like system permissions for `file`, as described in [`str->posix`](#babashka.fs/str->posix).
Affected by [umask](/README.md#umask).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L810-L820">Source</a></sub></p>

## <a name="babashka.fs/create-link">`create-link`</a>
``` clojure
(create-link link existing-file)
```
Function.

Returns a new hard `link` (directory entry) for an `existing-file` created via [Files/createLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createLink(java.nio.file.Path,java.nio.file.Path)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L752-L757">Source</a></sub></p>

## <a name="babashka.fs/create-sym-link">`create-sym-link`</a>
``` clojure
(create-sym-link link target-path)
```
Function.

Returns new symbolic `link` to `target-path` created via [Files/createSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createSymbolicLink(java.nio.file.Path,java.nio.file.Path,java.nio.file.attribute.FileAttribute...)).

As of this writing, JDKs do not recognize empty-string `target-path` `""` as the cwd.
Consider instead using a `target-path` of `"."` to link to the cwd.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L741-L750">Source</a></sub></p>

## <a name="babashka.fs/create-temp-dir">`create-temp-dir`</a>
``` clojure
(create-temp-dir)
(create-temp-dir {:keys [:dir :prefix :posix-file-permissions], :as opts})
```
Function.

Returns path to directory created via [Files/createTempDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createTempDirectory(java.nio.file.Path,java.lang.String,java.nio.file.attribute.FileAttribute...)).

This function does not set up any automatic deletion of the directories it
creates. See [`with-temp-dir`](#babashka.fs/with-temp-dir) for that functionality.

Options:
* `:dir` - directory in which to create the new directory. Defaults to default
system temp dir (e.g. `/tmp`); see [`temp-dir`](#babashka.fs/temp-dir). Must already exist.
* `:prefix` - provided as a hint to the process that generates the name of the
new directory. In most cases, this will be the beginning of the new directory
name. Defaults to a random (v4) UUID.
* `:posix-file-permissions` - string format unix-like system permissions as described in [`str->posix`](#babashka.fs/str->posix) for new directory.
If not specified, uses the file system default permissions for new directories.
Affected by [umask](/README.md#umask).
* :warning: `:path` - **[DEPRECATED]** previous name for `:dir`, kept
for backwards compatibility. If both `:path` and `:dir` are given (don't do
that!), `:dir` is used.

Examples:
* `(create-temp-dir)`
* `(create-temp-dir {:posix-file-permissions "rwx------"})`
* `(create-temp-dir {:dir (path (cwd) "_workdir") :prefix "process-1-"})`
  
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L657-L693">Source</a></sub></p>

## <a name="babashka.fs/create-temp-file">`create-temp-file`</a>
``` clojure
(create-temp-file)
(create-temp-file {:keys [:dir :prefix :suffix :posix-file-permissions], :as opts})
```
Function.

Returns path to empty file created via [Files/createTempFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#createTempFile(java.nio.file.Path,java.lang.String,java.lang.String,java.nio.file.attribute.FileAttribute...)).

This function does not set up any automatic deletion of the files it
creates. Create the file in a [`with-temp-dir`](#babashka.fs/with-temp-dir) for that functionality.

Options:
* `:dir` - directory in which to create the new file. Defaults to default
system temp dir (e.g. `/tmp`); see [`temp-dir`](#babashka.fs/temp-dir). Must already exist.
* `:prefix` - provided as a hint to the process that generates the name of the
new file. In most cases, this will be the beginning of the new file name.
Defaults to a random (v4) UUID.
* `:suffix` - provided as a hint to the process that generates the name of the
new file. In most cases, this will be the end of the new file name.
Defaults to a random (v4) UUID.
* `:posix-file-permissions` - string format unix-like system permissions for new file, as described in [`str->posix`](#babashka.fs/str->posix).
If not specified, uses the file system default permissions for new files.
Affected by [umask](/README.md#umask).
* :warning: `:path` - **[DEPRECATED]** Previous name for `:dir`, kept
for backwards compatibility. If both `:path` and `:dir` are given (don't do
that!), `:dir` is used.

Examples:
* `(create-temp-file)`
* `(create-temp-file {:posix-file-permissions "rw-------"})`
* `(create-temp-file {:dir (path (cwd) "_workdir") :prefix "process-1-" :suffix "-queue"})`
  
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L695-L739">Source</a></sub></p>

## <a name="babashka.fs/creation-time">`creation-time`</a>
``` clojure
(creation-time path)
(creation-time path {:keys [nofollow-links], :as opts})
```
Function.

Returns creation time of `path` as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).

See [README notes](/README.md#creation-time) for some details on behaviour.

See also: [`set-creation-time`](#babashka.fs/set-creation-time), [`last-modified-time`](#babashka.fs/last-modified-time), [`file-time->instant`](#babashka.fs/file-time->instant), [`file-time->millis`](#babashka.fs/file-time->millis)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1007-L1016">Source</a></sub></p>

## <a name="babashka.fs/cwd">`cwd`</a>
``` clojure
(cwd)
```
Function.

Returns current working directory path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1488-L1491">Source</a></sub></p>

## <a name="babashka.fs/delete">`delete`</a>
``` clojure
(delete path)
```
Function.

Deletes `path` via [Files/delete](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#delete(java.nio.file.Path)).
Returns `nil` if the delete was successful,
throws otherwise. Does not follow symlinks.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L765-L772">Source</a></sub></p>

## <a name="babashka.fs/delete-if-exists">`delete-if-exists`</a>
``` clojure
(delete-if-exists path)
```
Function.

Deletes `path` if it exists via [Files/deleteIfExists](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#deleteIfExists(java.nio.file.Path)).
Returns `true` if the delete was successful,
`false` if `path` didn't exist. Does not follow symlinks.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L774-L779">Source</a></sub></p>

## <a name="babashka.fs/delete-on-exit">`delete-on-exit`</a>
``` clojure
(delete-on-exit path)
```
Function.

Requests delete of `path` on exit via [File#deleteOnExit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#deleteOnExit()).
Returns `path`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L857-L862">Source</a></sub></p>

## <a name="babashka.fs/delete-tree">`delete-tree`</a>
``` clojure
(delete-tree root-path)
(delete-tree root-path {:keys [force]})
```
Function.

Deletes the file tree at `root-path` using [`walk-file-tree`](#babashka.fs/walk-file-tree). Similar to `rm -rf` shell command. Does not follow symlinks.

Options:
* `:force` - if `true` forces deletion of read-only files/directories. Similar to `chmod -R +wx` + `rm -rf` shell commands.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L786-L808">Source</a></sub></p>

## <a name="babashka.fs/directory?">`directory?`</a>
``` clojure
(directory? path)
(directory? path {:keys [:nofollow-links]})
```
Function.

Returns `true` if `path` is a directory via [Files/isDirectory](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isDirectory(java.nio.file.Path,java.nio.file.LinkOption...)).

Options:
* [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L114-L122">Source</a></sub></p>

## <a name="babashka.fs/ends-with?">`ends-with?`</a>
``` clojure
(ends-with? this-path other-path)
```
Function.

Returns `true` if `this-path` ends with `other-path` via [Path#endsWith](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#endsWith(java.nio.file.Path)).

See also: [`starts-with?`](#babashka.fs/starts-with?)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L588-L593">Source</a></sub></p>

## <a name="babashka.fs/exec-paths">`exec-paths`</a>
``` clojure
(exec-paths)
```
Function.

Returns a vector of command search paths (from the `PATH` environment variable). Same
as `(split-paths (System/getenv "PATH"))`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1116-L1120">Source</a></sub></p>

## <a name="babashka.fs/executable?">`executable?`</a>
``` clojure
(executable? path)
```
Function.

Returns `true` if `path` is executable via [Files/isExecutable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isExecutable(java.nio.file.Path)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L141-L143">Source</a></sub></p>

## <a name="babashka.fs/exists?">`exists?`</a>
``` clojure
(exists? path)
(exists? path {:keys [:nofollow-links]})
```
Function.

Returns `true` if `path` exists via [Files/exists](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#exists(java.nio.file.Path,java.nio.file.LinkOption...)).

Options:
* [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L157-L169">Source</a></sub></p>

## <a name="babashka.fs/expand-home">`expand-home`</a>
``` clojure
(expand-home path)
```
Function.

Returns `path` replacing `~` (tilde) with home dir.

If `path`:
- does not start with `~`, returns `path`.
- starts with `~` then [`file-separator`](#babashka.fs/file-separator), `~` is replaced with `(home)`.
e.g., `~/foo` -> `/home/myuser/foo` 
- starts with `~` then some other chars, those other chars are
assumed to be a username, then naively expanded to `(home username)`.
e.g., `~someuser/foo` -> `/home/someuser/foo`  

See also: [`home`](#babashka.fs/home)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1461-L1481">Source</a></sub></p>

## <a name="babashka.fs/extension">`extension`</a>
``` clojure
(extension path)
```
Function.

Returns the extension of `path` via [`split-ext`](#babashka.fs/split-ext).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1104-L1107">Source</a></sub></p>

## <a name="babashka.fs/file">`file`</a>
``` clojure
(file path)
(file path & paths)
```
Function.

Returns `path`(s) coerced to a `File`, combining multiple paths into one.
Multiple-arg versions treat the first argument as parent and subsequent args
as children relative to the parent.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L68-L74">Source</a></sub></p>

## <a name="babashka.fs/file-name">`file-name`</a>
``` clojure
(file-name path)
```
Function.

Returns the name of the file or directory for `path` via [File#getName](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/File.html#getName()).
E.g. `(file-name "foo/bar/baz")` returns `"baz"`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L225-L229">Source</a></sub></p>

## <a name="babashka.fs/file-separator">`file-separator`</a>




The system-dependent default path component separator character (as string).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L316-L318">Source</a></sub></p>

## <a name="babashka.fs/file-time->instant">`file-time->instant`</a>
``` clojure
(file-time->instant ft)
```
Function.

Returns [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html) `ft`
as [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L956-L960">Source</a></sub></p>

## <a name="babashka.fs/file-time->millis">`file-time->millis`</a>
``` clojure
(file-time->millis ft)
```
Function.

Returns [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html) `ft`
as epoch milliseconds (long).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L968-L972">Source</a></sub></p>

## <a name="babashka.fs/get-attribute">`get-attribute`</a>
``` clojure
(get-attribute path attribute)
(get-attribute path attribute {:keys [:nofollow-links]})
```
Function.

Returns `attribute` for `path` via [Files/getAttribute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getAttribute(java.nio.file.Path,java.lang.String,java.nio.file.LinkOption...)).

Options:
* [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L895-L905">Source</a></sub></p>

## <a name="babashka.fs/glob">`glob`</a>
``` clojure
(glob root-dir pattern)
(glob root-dir pattern opts)
```
Function.

Returns a vector of paths matching glob `pattern` (on path and filename) relative to `root-dir`.
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
- `(fs/glob "." "**.clj")` - finds `.clj` files and dirs under `.` dir and its subdirs
- `(fs/glob "." "**.clj" {:recursive false})` - finds `.clj` files and dirs immediately under `.` dir only
- `(fs/glob "." "*.clj" {:recursive true})` - finds `.clj` files and dirs immediately under `.` only (`pattern` lacks directory wildcards)

If on macOS, see [note on glob](/README.md#glob)

See also: [`match`](#babashka.fs/match)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L419-L454">Source</a></sub></p>

## <a name="babashka.fs/gunzip">`gunzip`</a>
``` clojure
(gunzip gz-file)
(gunzip gz-file target-dir)
(gunzip gz-file target-dir {:keys [replace-existing]})
```
Function.

Extracts `gz-file` to `target-dir`.

If `target-dir` not specified (or `nil`) defaults to `gz-file` dir.

File is extracted to `target-dir` with `gz-file` [`file-name`](#babashka.fs/file-name) without `.gz` extension.

Creates `target-dir` dir(s) if necessary.
The `gz-file` is not deleted.

Options:
* `:replace-existing` - when `true` overwrites existing file

See also: [`gzip`](#babashka.fs/gzip)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1348-L1377">Source</a></sub></p>

## <a name="babashka.fs/gzip">`gzip`</a>
``` clojure
(gzip source-file)
(gzip source-file {:keys [dir out-file]})
```
Function.

Gzips `source-file` to `:dir`/`:out-file`.

Does not store the `source-file` name in the `.gz` file.
The `source-file` is not deleted.

Options:
* `:dir`(s) - created if necessary. If not specified, defaults to `source-file` dir.
* `:out-file` - if not specified, defaults to `source-file` [`file-name`](#babashka.fs/file-name) with `.gz` extension.

Returns the created gzip file.

See also: [`gunzip`](#babashka.fs/gunzip)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1379-L1409">Source</a></sub></p>

## <a name="babashka.fs/hidden?">`hidden?`</a>
``` clojure
(hidden? path)
```
Function.

Returns `true` if `path` is hidden via [Files/isHidden](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isHidden(java.nio.file.Path)).

TIP: some older JDKs can throw on empty-string path `(hidden "")`.
Consider instead checking cwd via `(hidden ".")`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L130-L135">Source</a></sub></p>

## <a name="babashka.fs/home">`home`</a>
``` clojure
(home)
(home user)
```
Function.

Returns home dir path.

With no arguments, returns the current value of the `user.home`
system property as a path. If a `user` is passed, returns that user's home
directory as found in the parent of home with no args.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1451-L1459">Source</a></sub></p>

## <a name="babashka.fs/instant->file-time">`instant->file-time`</a>
``` clojure
(instant->file-time instant)
```
Function.

Returns [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html) `instant`
as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L962-L966">Source</a></sub></p>

## <a name="babashka.fs/last-modified-time">`last-modified-time`</a>
``` clojure
(last-modified-time path)
(last-modified-time path {:keys [nofollow-links], :as opts})
```
Function.

Returns last modified time of `path` as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html).

See also: [`set-last-modified-time`](#babashka.fs/set-last-modified-time), [`creation-time`](#babashka.fs/creation-time), [`file-time->instant`](#babashka.fs/file-time->instant), [`file-time->millis`](#babashka.fs/file-time->millis)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L986-L993">Source</a></sub></p>

## <a name="babashka.fs/list-dir">`list-dir`</a>
``` clojure
(list-dir dir)
(list-dir dir glob-or-accept)
```
Function.

Returns a vector of all paths in `dir`. For descending into subdirectories use [`glob`](#babashka.fs/glob).

- `glob-or-accept` - a [`glob`](#babashka.fs/glob) string such as "*.edn" or a `(fn accept [^java.nio.file.Path p]) -> truthy`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L298-L307">Source</a></sub></p>

## <a name="babashka.fs/list-dirs">`list-dirs`</a>
``` clojure
(list-dirs dirs glob-or-accept)
```
Function.

Similar to [`list-dir`](#babashka.fs/list-dir) but accepts multiple roots in `dirs` and returns the concatenated results.
- `glob-or-accept` - a [`glob`](#babashka.fs/glob) string such as `"*.edn"` or a `(fn accept [^java.nio.file.Path p]) -> truthy`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1064-L1068">Source</a></sub></p>

## <a name="babashka.fs/match">`match`</a>
``` clojure
(match root-dir pattern)
(match root-dir pattern {:keys [hidden follow-links max-depth recursive]})
```
Function.

Returns a vector of paths matching `pattern` (on path and filename) relative to `root-dir`.
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
- `(fs/match "." "regex:.*\\.clj" {:recursive true})`

See also: [`glob`](#babashka.fs/glob)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L345-L417">Source</a></sub></p>

## <a name="babashka.fs/millis->file-time">`millis->file-time`</a>
``` clojure
(millis->file-time millis)
```
Function.

Returns epoch milliseconds (long)
as [FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L974-L978">Source</a></sub></p>

## <a name="babashka.fs/modified-since">`modified-since`</a>
``` clojure
(modified-since anchor-path path-set)
```
Function.

Returns seq of regular files (non-directories, non-symlinks) from `path-set` that were modified since the `anchor-path`.
The `anchor-path` can be a regular file or directory, in which case
the recursive max last modified time stamp is used as the timestamp
to compare with.  The `path-set` may be a regular file, directory or
collection of paths (e.g. as returned by [`glob`](#babashka.fs/glob)). Directories are
searched recursively.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1227-L1236">Source</a></sub></p>

## <a name="babashka.fs/move">`move`</a>
``` clojure
(move source-path target-path)
(move source-path target-path {:keys [:replace-existing :atomic-move]})
```
Function.

Moves or renames dir or file at `source-path` to `target-path` dir or file via [Files/move](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#move(java.nio.file.Path,java.nio.file.Path,java.nio.file.CopyOption...)).
If `target-path` is a directory, moves `source-path` under `target-path`.
Never follows symbolic links.

Returns `target-path` path.

Options:
* `replace-existing` - overwrite existing `target-path`, default `false`
* `atomic-move` - watchers will only see complete `target-path` file, default `false`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L822-L844">Source</a></sub></p>

## <a name="babashka.fs/normalize">`normalize`</a>
``` clojure
(normalize path)
```
Function.

Returns normalized path for `path` via [Path#normalize](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#normalize()).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L192-L195">Source</a></sub></p>

## <a name="babashka.fs/owner">`owner`</a>
``` clojure
(owner path)
(owner path {:keys [:nofollow-links]})
```
Function.

Returns the owner of `path` via [Files/getOwner](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getOwner(java.nio.file.Path,java.nio.file.LinkOption...)).
Call `str` on return value to get the owner name as a string.

Options:
* [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L92-L100">Source</a></sub></p>

## <a name="babashka.fs/parent">`parent`</a>
``` clojure
(parent path)
```
Function.

Returns parent path of `path` via [Paths/getParent](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#getParent()).
Akin to `dirname` in bash.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L846-L850">Source</a></sub></p>

## <a name="babashka.fs/path">`path`</a>
``` clojure
(path path)
(path parent child)
(path parent child & more)
```
Function.

Returns `path`(s) coerced to a `Path`, combining multiple paths into one.
Multiple-arg versions treat the first argument as parent and subsequent
args as children relative to the parent.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L51-L64">Source</a></sub></p>

## <a name="babashka.fs/path-separator">`path-separator`</a>




The system-dependent path-separator character (as string).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L320-L322">Source</a></sub></p>

## <a name="babashka.fs/posix->str">`posix->str`</a>
``` clojure
(posix->str p)
```
Function.

Returns permissions string, like `"rwx------"`, for a set of `PosixFilePermission` `p`.

See also [`str->posix`](#babashka.fs/str->posix)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L486-L491">Source</a></sub></p>

## <a name="babashka.fs/posix-file-permissions">`posix-file-permissions`</a>
``` clojure
(posix-file-permissions path)
(posix-file-permissions path {:keys [:nofollow-links]})
```
Function.

Returns a set of `PosixFilePermissions` for `path` via [Files/getPosixFilePermissions](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#getPosixFilePermissions(java.nio.file.Path,java.nio.file.LinkOption...)).
Use [`posix->str`](#babashka.fs/posix->str) to convert to a string.

Options:
* [`:nofollow-links`](/README.md#nofollow-links)

See also: [`set-posix-file-permissions`](#babashka.fs/set-posix-file-permissions)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L559-L569">Source</a></sub></p>

## <a name="babashka.fs/read-all-bytes">`read-all-bytes`</a>
``` clojure
(read-all-bytes file)
```
Function.

Returns contents of `file` as byte array via [Files/readAllBytes](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAllBytes(java.nio.file.Path)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L869-L873">Source</a></sub></p>

## <a name="babashka.fs/read-all-lines">`read-all-lines`</a>
``` clojure
(read-all-lines file)
(read-all-lines file {:keys [charset], :or {charset "utf-8"}})
```
Function.

Return contents of `file` as a vector of lines via [Files/readAllLines](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAllLines(java.nio.file.Path,java.nio.charset.Charset)).

Options:
- `:charset` - defaults to `"utf-8"`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L880-L891">Source</a></sub></p>

## <a name="babashka.fs/read-attributes">`read-attributes`</a>
``` clojure
(read-attributes path attributes)
(read-attributes path attributes {:keys [:nofollow-links :key-fn], :as opts})
```
Function.

Same as [`read-attributes*`](#babashka.fs/read-attributes*) but returns requested `attributes` for `path` as a map with keywordized attribute keys.

Options:
* `:key-fn` - optionally override keywordizing function with your own.
* [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L933-L944">Source</a></sub></p>

## <a name="babashka.fs/read-attributes*">`read-attributes*`</a>
``` clojure
(read-attributes* path attributes)
(read-attributes* path attributes {:keys [:nofollow-links]})
```
Function.

Returns requested `attributes` for `path` via [Files/readAttributes](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readAttributes(java.nio.file.Path,java.lang.Class,java.nio.file.LinkOption...)).

Options:
* [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L912-L931">Source</a></sub></p>

## <a name="babashka.fs/read-link">`read-link`</a>
``` clojure
(read-link sym-link-path)
```
Function.

Returns the immediate target of `sym-link-path` via [Files/readSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readSymbolicLink(java.nio.file.Path)).
The target need not exist.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L759-L763">Source</a></sub></p>

## <a name="babashka.fs/readable?">`readable?`</a>
``` clojure
(readable? path)
```
Function.

Returns `true` if `path` is readable via [Files/isReadable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isReadable(java.nio.file.Path))
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L145-L147">Source</a></sub></p>

## <a name="babashka.fs/real-path">`real-path`</a>
``` clojure
(real-path path)
(real-path path {:keys [:nofollow-links]})
```
Function.

Returns real path for `path` via [Path#toRealPath](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#toRealPath(java.nio.file.LinkOption...)).

Options:
* [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L83-L90">Source</a></sub></p>

## <a name="babashka.fs/regular-file?">`regular-file?`</a>
``` clojure
(regular-file? path)
(regular-file? path {:keys [:nofollow-links]})
```
Function.

Returns `true` if `path` is a regular file via [Files/isRegularFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isRegularFile(java.nio.file.Path,java.nio.file.LinkOption...)).

Options:
* [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L104-L112">Source</a></sub></p>

## <a name="babashka.fs/relative?">`relative?`</a>
``` clojure
(relative? path)
```
Function.

Returns `true` if `path` is relative (in other words, is not [`absolute?`](#babashka.fs/absolute?)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L153-L155">Source</a></sub></p>

## <a name="babashka.fs/relativize">`relativize`</a>
``` clojure
(relativize base-path other-path)
```
Function.

Returns `other-path` relative to `base-path` via [Path#relativize](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#relativize(java.nio.file.Path)).

Examples:
- `(fs/relativize "a/b" "a/b/c/d")` => `c/d`
- `(fs/relativize "a/b/c/d" "a/b")` => `../..`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L183-L190">Source</a></sub></p>

## <a name="babashka.fs/root">`root`</a>
``` clojure
(root path)
```
Function.

Returns root path for `path`, or `nil`, via [Path#getRoot](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#getRoot()).

The return value depends upon the runtime platform.

On Windows, returns Windows specific roots, ex:
(replace forward slash with backslash):
* `C:/` for `C:/foo/bar`
* `C:`  for `C:foo/bar`
* `//server/share` for `//server/share/foo/bar`

On Linux and macOS, returns the leading `/` for anything that looks like an absolute path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L210-L223">Source</a></sub></p>

## <a name="babashka.fs/same-file?">`same-file?`</a>
``` clojure
(same-file? this-path other-path)
```
Function.

Returns `true` if `this-path` is the same file as `other-path` via [Files/isSamefile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isSameFile(java.nio.file.Path,java.nio.file.Path)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L864-L867">Source</a></sub></p>

## <a name="babashka.fs/set-attribute">`set-attribute`</a>
``` clojure
(set-attribute path attribute value)
(set-attribute path attribute value {:keys [:nofollow-links]})
```
Function.

Sets `attribute` for `path` to `value` via [Files/setAttribute](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#setAttribute(java.nio.file.Path,java.lang.String,java.lang.Object,java.nio.file.LinkOption...))
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L946-L954">Source</a></sub></p>

## <a name="babashka.fs/set-creation-time">`set-creation-time`</a>
``` clojure
(set-creation-time path time)
(set-creation-time path time {:keys [nofollow-links], :as opts})
```
Function.

Sets creation `time` of `path`.
`time` can be `epoch milliseconds`,
[FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html),
or [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html) .

Options:
* [`:nofollow-links`](/README.md#nofollow-links)

See [README notes](/README.md#set-creation-time) for some details on behaviour.

See also: [`creation-time`](#babashka.fs/creation-time)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1018-L1033">Source</a></sub></p>

## <a name="babashka.fs/set-last-modified-time">`set-last-modified-time`</a>
``` clojure
(set-last-modified-time path time)
(set-last-modified-time path time {:keys [nofollow-links], :as opts})
```
Function.

Sets last modified `time` of `path`. 
`time` can be `epoch milliseconds`,
[FileTime](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/FileTime.html),
or [Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html).

See also: [`last-modified-time`](#babashka.fs/last-modified-time)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L995-L1005">Source</a></sub></p>

## <a name="babashka.fs/set-posix-file-permissions">`set-posix-file-permissions`</a>
``` clojure
(set-posix-file-permissions path posix-file-permissions)
```
Function.

Sets `posix-file-permissions` on `path` via [Files/setPosixFilePermissions](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#setPosixFilePermissions(java.nio.file.Path,java.util.Set)).
Accepts a string like `"rwx------"` or a set of `PosixFilePermission`.

See also: [`posix-file-permissions`](#babashka.fs/posix-file-permissions)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L551-L557">Source</a></sub></p>

## <a name="babashka.fs/size">`size`</a>
``` clojure
(size path)
```
Function.

Returns the size of `path` in bytes.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L852-L855">Source</a></sub></p>

## <a name="babashka.fs/split-ext">`split-ext`</a>
``` clojure
(split-ext path)
(split-ext path {:keys [ext]})
```
Function.

Returns `path` split on extension.
Leading directories in `path` are not processed.

Options:
* `:ext` - split on specified extension (do not include a leading dot) 
  
Examples:
- `(fs/split-ext "foo.bar.baz")` => `["foo.bar" "baz"]`
- `(fs/split-ext "foo.bar.baz" {:ext "bar.baz"})`  => `["foo" "bar.baz"]`
- `(fs/split-ext "foo.bar.baz" {:ext "png"})`  => `["foo.bar.baz" nil]`
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1070-L1095">Source</a></sub></p>

## <a name="babashka.fs/split-paths">`split-paths`</a>
``` clojure
(split-paths joined-paths)
```
Function.

Returns a vector of paths from paths in `joined-paths` string.
`joined-paths` is split on OS-specific [`path-separator`](#babashka.fs/path-separator).
On UNIX systems, the separator is `:`, on Microsoft Windows systems it is `;`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1109-L1114">Source</a></sub></p>

## <a name="babashka.fs/starts-with?">`starts-with?`</a>
``` clojure
(starts-with? this-path other-path)
```
Function.

Returns `true` if `this-path` starts with `other-path` via [Path#startsWith](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Path.html#startsWith(java.nio.file.Path)).

See also: [`ends-with?`](#babashka.fs/ends-with?)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L581-L586">Source</a></sub></p>

## <a name="babashka.fs/str->posix">`str->posix`</a>
``` clojure
(str->posix s)
```
Function.

Returns a set of `PosixFilePermission` for permissions string `s`.

`s` is a string like `"rwx------"`.

See also [`posix->str`](#babashka.fs/posix->str)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L493-L500">Source</a></sub></p>

## <a name="babashka.fs/strip-ext">`strip-ext`</a>
``` clojure
(strip-ext path)
(strip-ext path {:keys [ext], :as opts})
```
Function.

Returns `path` with extension stripped via [`split-ext`](#babashka.fs/split-ext).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1097-L1102">Source</a></sub></p>

## <a name="babashka.fs/sym-link?">`sym-link?`</a>
``` clojure
(sym-link? path)
```
Function.

Returns `true` if `path` is a symbolic link via [Files/isSymbolicLink](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isSymbolicLink(java.nio.file.Path)).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L781-L784">Source</a></sub></p>

## <a name="babashka.fs/temp-dir">`temp-dir`</a>
``` clojure
(temp-dir)
```
Function.

Returns `java.io.tmpdir` property as path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L652-L655">Source</a></sub></p>

## <a name="babashka.fs/touch">`touch`</a>
``` clojure
(touch path)
(touch path {:keys [time nofollow-links], :as opts})
```
Function.

Update last modified time of `path` to `:time`, creating `path` as a file if it does not exist.

If `path` is deleted by some other process/thread before `:time` is set,
a `NoSuchFileException` will be thrown. Callers can, if their use case requires it,
implement their own retry loop.

Options:
* `:time` - last modified time (epoch milliseconds, `Instant`, or `FileTime`), defaults to current time
* [`:nofollow-links`](/README.md#nofollow-links)
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1035-L1062">Source</a></sub></p>

## <a name="babashka.fs/unixify">`unixify`</a>
``` clojure
(unixify path)
```
Function.

Returns `path` as string with Unix-style file separators (`/`).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1580-L1586">Source</a></sub></p>

## <a name="babashka.fs/unzip">`unzip`</a>
``` clojure
(unzip zip-file)
(unzip zip-file target-dir)
(unzip zip-file target-dir {:keys [replace-existing extract-fn]})
```
Function.

Unzips `zip-file` to `target-dir` (default `"."`).

 Options:
 * `:replace-existing` - `true` / `false`: overwrite existing files
 * `:extract-fn` - function that decides if the current `ZipEntry`
   should be extracted. Extraction only occurs if a truthy value is returned (i.e. not nil/false).
   The function is only called for for files (not directories) with a single map arg:
   * `:entry` - the current `ZipEntry`
   * `:name` - the name of the `ZipEntry` (result of calling `getName`)

See also: [`zip`](#babashka.fs/zip).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1242-L1280">Source</a></sub></p>

## <a name="babashka.fs/update-file">`update-file`</a>
``` clojure
(update-file file f & xs)
(update-file file opts f & xs)
```
Function.

Updates the contents of text `file` with result of applying function `f` with old contents and args `xs`.
Returns the new contents.

Options:
* `:charset` - charset of file, default to "utf-8"
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1561-L1578">Source</a></sub></p>

## <a name="babashka.fs/walk-file-tree">`walk-file-tree`</a>
``` clojure
(walk-file-tree path {:keys [:pre-visit-dir :post-visit-dir :visit-file :visit-file-failed :follow-links :max-depth]})
```
Function.

Walks `path` via [Files/walkFileTree](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#walkFileTree(java.nio.file.Path,java.util.Set,int,java.nio.file.FileVisitor)).

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

Returns `path`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L233-L278">Source</a></sub></p>

## <a name="babashka.fs/which">`which`</a>
``` clojure
(which program)
(which program opts)
```
Function.

Returns path to first executable `program` found in `:paths`, similar to the `which` Unix command.

When `program` is a relative or absolute path, `:paths` option is not consulted.
On Windows, the `:win-exts` variants are still searched.
On other OSes, the path for `program` will be returned if executable, else `nil`.

Options:
* `:paths` - paths to search, default is return of ([`exec-paths`](#babashka.fs/exec-paths))
* `:win-exts` - active on Windows only. Searches for `program` with filename extensions specified in `:win-exts` option.
If `program` already includes an extension from `:win-exts`, it will be searched as-is first.
Default is `["com" "exe" "bat" "cmd"]`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1129-L1182">Source</a></sub></p>

## <a name="babashka.fs/which-all">`which-all`</a>
``` clojure
(which-all program)
(which-all program opts)
```
Function.

Returns a vector of every path to `program` found in ([`exec-paths`](#babashka.fs/exec-paths)). See [`which`](#babashka.fs/which).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1184-L1188">Source</a></sub></p>

## <a name="babashka.fs/windows?">`windows?`</a>
``` clojure
(windows?)
```
Function.

Returns `true` if OS is Windows.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1483-L1486">Source</a></sub></p>

## <a name="babashka.fs/with-temp-dir">`with-temp-dir`</a>
``` clojure
(with-temp-dir [temp-dir] & body)
(with-temp-dir [temp-dir opts] & body)
```
Macro.

Evaluates body with `temp-dir` bound to the result of `(create-temp-dir
opts)`.

By default, the `temp-dir` will be removed with [`delete-tree`](#babashka.fs/delete-tree) on exit from the scope.

Options:
* see [`delete-tree`](#babashka.fs/delete-tree)
* `:keep` - if `true` does not delete the directory on exit from macro scope. 
  
Example:
```
(with-temp-dir [d]
  (let [t (path d "extract")
    (create-dir t)
    (gunzip path-to-zip t)
    (copy (path t "the-one-file-I-wanted.txt") (path permanent-dir "file-I-extracted.txt"))))
;; d no longer exists here
```
  
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1413-L1443">Source</a></sub></p>

## <a name="babashka.fs/writable?">`writable?`</a>
``` clojure
(writable? path)
```
Function.

Returns `true` if `path` is writable via [Files/isWritable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#isWritable(java.nio.file.Path))
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L149-L151">Source</a></sub></p>

## <a name="babashka.fs/write-bytes">`write-bytes`</a>
``` clojure
(write-bytes file bytes)
(write-bytes file bytes {:keys [append create truncate-existing write], :as opts})
```
Function.

Writes `bytes` to `file` via [Files/write](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#write(java.nio.file.Path,byte%5B%5D,java.nio.file.OpenOption...)).

Options:
* `:create` - (default `true`)
* `:truncate-existing` - (default `true`)
* `:write` - (default `true`)
* `:append` - (default `false`)
* or any `java.nio.file.StandardOption`.

Examples:

``` clojure
(fs/write-bytes f (.getBytes (String. "foo"))) ;; overwrites + truncates or creates new file
(fs/write-bytes f (.getBytes (String. "foo")) {:append true})
```
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1514-L1538">Source</a></sub></p>

## <a name="babashka.fs/write-lines">`write-lines`</a>
``` clojure
(write-lines file lines)
(write-lines file lines {:keys [charset], :or {charset "utf-8"}, :as opts})
```
Function.

Writes `lines`, a seqable of strings, to `file` via [Files/write](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#write(java.nio.file.Path,java.lang.Iterable,java.nio.charset.Charset,java.nio.file.OpenOption...)).

Options:
* `:charset` - (default `"utf-8"`)

Open options:
* `:create` (default `true`)
* `:truncate-existing` (default `true`)
* `:write` (default `true`)
* `:append` (default `false`)
* or any `java.nio.file.StandardOption`.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1540-L1559">Source</a></sub></p>

## <a name="babashka.fs/xdg-cache-home">`xdg-cache-home`</a>
``` clojure
(xdg-cache-home)
(xdg-cache-home app)
```
Function.

Returns path to user-specific non-essential data as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

Uses env-var `XDG_CACHE_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) ".cache")`.
When provided, appends `app` to the returned path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1618-L1626">Source</a></sub></p>

## <a name="babashka.fs/xdg-config-home">`xdg-config-home`</a>
``` clojure
(xdg-config-home)
(xdg-config-home app)
```
Function.

Returns path to user-specific configuration files as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

Uses env-var `XDG_CONFIG_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) ".config")`.
When provided, appends `app` to the returned path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1608-L1616">Source</a></sub></p>

## <a name="babashka.fs/xdg-data-home">`xdg-data-home`</a>
``` clojure
(xdg-data-home)
(xdg-data-home app)
```
Function.

Returns path to user-specific data files as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

Uses env-var `XDG_DATA_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) ".local" "share")`.
When provided, appends `app` to the returned path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1628-L1636">Source</a></sub></p>

## <a name="babashka.fs/xdg-state-home">`xdg-state-home`</a>
``` clojure
(xdg-state-home)
(xdg-state-home app)
```
Function.

Returns path to user-specific state files as described in the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html).

Uses env-var `XDG_STATE_HOME` (if set and representing an absolute path), else `(fs/path (fs/home) ".local" "state")`.
When provided, appends `app` to the returned path.
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1638-L1646">Source</a></sub></p>

## <a name="babashka.fs/zip">`zip`</a>
``` clojure
(zip zip-file path-or-paths)
(zip zip-file path-or-paths opts)
```
Function.

Zips `path-or-paths` into `zip-file`. A path may be a file or
directory. Directories are included recursively and their names are
preserved in the zip file. Currently only accepts relative paths.

Options:
* `:root` - optional directory to be elided in `zip-file` entries. E.g.: `(fs/zip ["src"] {:root "src"})`
* `:path-fn` - an optional custom path conversion function.
A single-arg function called for each file sytem path returning the path to be used for the corresponding zip entry.

See also: [`unzip`](#babashka.fs/unzip).
<p><sub><a href="https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1313-L1342">Source</a></sub></p>
