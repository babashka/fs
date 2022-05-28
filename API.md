# Table of contents
-  [`babashka.fs`](#babashkafs) 
    -  [`absolute?`](#absolute?) - Returns true if f represents an absolute path.
    -  [`absolutize`](#absolutize) - Converts f into an absolute path via Path#toAbsolutePath.
    -  [`canonicalize`](#canonicalize) - Returns the canonical path via
    -  [`components`](#components) - Returns a seq of all components of f.
    -  [`copy`](#copy) - Copies src file to dest dir or file.
    -  [`copy-tree`](#copy-tree) - Copies entire file tree from src to dest
    -  [`create-dir`](#create-dir) - Creates dir using <code>Files#createDirectory</code>
    -  [`create-dirs`](#create-dirs) - Creates directories using <code>Files#createDirectories</code>
    -  [`create-file`](#create-file) - Creates empty file using <code>Files#createFile</code>.
    -  [`create-link`](#create-link) - Create a hard link from path to target.
    -  [`create-sym-link`](#create-sym-link) - Create a soft link from path to target.
    -  [`create-temp-dir`](#create-temp-dir) - Creates a temporary directory using Files#createDirectories.
    -  [`create-temp-file`](#create-temp-file) - Creates an empty temporary file using Files#createTempFile.
    -  [`creation-time`](#creation-time) - Returns creation time as FileTime.
    -  [`cwd`](#cwd) - Returns current working directory as path
    -  [`delete`](#delete) - Deletes f
    -  [`delete-if-exists`](#delete-if-exists) - Deletes f if it exists
    -  [`delete-on-exit`](#delete-on-exit) - Requests delete on exit via <code>File#deleteOnExit</code>
    -  [`delete-tree`](#delete-tree) - Deletes a file tree using <code>walk-file-tree</code>
    -  [`directory?`](#directory?) - Returns true if f is a directory, using Files/isDirectory.
    -  [`ends-with?`](#ends-with?) - Returns true if path this ends with path other.
    -  [`exec-paths`](#exec-paths) - Returns executable paths (using the PATH environment variable)
    -  [`executable?`](#executable?) - Returns true if f is executable.
    -  [`exists?`](#exists?) - Returns true if f exists.
    -  [`expand-home`](#expand-home) - If <code>path</code> begins with a tilde (<code>~</code>), expand the tilde to the value
    -  [`extension`](#extension) - Returns the extension of a file
    -  [`file`](#file) - Coerces f into a File
    -  [`file-name`](#file-name) - Returns the name of the file or directory
    -  [`file-separator`](#file-separator)
    -  [`file-time->instant`](#file-time->instant) - Converts a java.nio.file.attribute.FileTime to a java.time.Instant.
    -  [`file-time->millis`](#file-time->millis) - Converts a java.nio.file.attribute.FileTime to epoch millis (long).
    -  [`get-attribute`](#get-attribute)
    -  [`glob`](#glob) - Given a file and glob pattern, returns matches as vector of
    -  [`hidden?`](#hidden?) - Returns true if f is hidden.
    -  [`home`](#home) - With no arguments, returns the current value of the <code>user.home</code>
    -  [`instant->file-time`](#instant->file-time) - Converts a java.time.Instant to a java.nio.file.attribute.FileTime.
    -  [`last-modified-time`](#last-modified-time) - Returns last modified time as a java.nio.file.attribute.FileTime.
    -  [`list-dir`](#list-dir) - Returns all paths in dir as vector
    -  [`list-dirs`](#list-dirs) - Similar to list-dir but accepts multiple roots and returns the concatenated resu
    -  [`match`](#match) - Given a file and match pattern, returns matches as vector of
    -  [`millis->file-time`](#millis->file-time) - Converts epoch millis (long) to a java.nio.file.attribute.FileTime.
    -  [`modified-since`](#modified-since) - Returns seq of regular files (non-directories, non-symlinks) from file-set that 
    -  [`move`](#move) - Move or rename a file to a target dir or file via <code>Files/move</code>.
    -  [`normalize`](#normalize) - Normalizes f via Path#normalize.
    -  [`parent`](#parent) - Returns parent of f, is it exists.
    -  [`path`](#path) - Coerces f into a Path
    -  [`path-separator`](#path-separator)
    -  [`posix->str`](#posix->str) - Converts a set of PosixFilePermission to a string.
    -  [`posix-file-permissions`](#posix-file-permissions) - Gets f's posix file permissions
    -  [`read-all-bytes`](#read-all-bytes) - Returns contents of file as byte array.
    -  [`read-all-lines`](#read-all-lines) - Read all lines from a file.
    -  [`read-attributes`](#read-attributes) - Same as <code>read-attributes*</code> but turns attributes into a map and keywordizes keys.
    -  [`read-attributes*`](#read-attributes-1) - Reads attributes via Files/readAttributes.
    -  [`readable?`](#readable?) - Returns true if f is readable
    -  [`real-path`](#real-path) - Converts f into real path via Path#toRealPath.
    -  [`regular-file?`](#regular-file?) - Returns true if f is a regular file, using Files/isRegularFile.
    -  [`relative?`](#relative?) - Returns true if f represents a relative path.
    -  [`relativize`](#relativize) - Returns relative path by comparing this with other.
    -  [`same-file?`](#same-file?) - Returns true if this is the same file as other.
    -  [`set-attribute`](#set-attribute)
    -  [`set-creation-time`](#set-creation-time) - Sets creation time of f to time (millis, java.time.Instant or java.nio.file.attr
    -  [`set-last-modified-time`](#set-last-modified-time) - Sets last modified time of f to time (millis, java.time.Instant or java.nio.file
    -  [`set-posix-file-permissions`](#set-posix-file-permissions) - Sets posix file permissions on f
    -  [`size`](#size)
    -  [`split-ext`](#split-ext) - Splits a path into a vec of [path-without-ext ext]
    -  [`split-paths`](#split-paths) - Splits a string joined by the OS-specific path-seperator into a vec of paths.
    -  [`starts-with?`](#starts-with?) - Returns true if path this starts with path other.
    -  [`str->posix`](#str->posix) - Converts a string to a set of PosixFilePermission.
    -  [`strip-ext`](#strip-ext) - Returns the path with the extension removed
    -  [`sym-link?`](#sym-link?)
    -  [`temp-dir`](#temp-dir) - Returns <code>java.io.tmpdir</code> property as path.
    -  [`unzip`](#unzip) - zip-file: zip archive to unzip (required)
    -  [`walk-file-tree`](#walk-file-tree) - Walks f using Files/walkFileTree
    -  [`which`](#which) - Locates a program in (exec-paths) similar to the which Unix command.
    -  [`which-all`](#which-all)
    -  [`windows?`](#windows?) - Returns true if OS is Windows.
    -  [`with-temp-dir`](#with-temp-dir) - Evaluate body with binding-name bound to a temporary directory.
    -  [`writable?`](#writable?) - Returns true if f is writable
    -  [`zip`](#zip) - Zips entry or entries into zip-file
# babashka.fs 





## [`absolute?`](#absolute?)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L104-L106)</sub>
``` clojure

(absolute? [f])
```


Returns true if f represents an absolute path.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L104-L106)</sub>
## [`absolutize`](#absolutize)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L139-L141)</sub>
``` clojure

(absolutize [f])
```


Converts f into an absolute path via Path#toAbsolutePath.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L139-L141)</sub>
## [`canonicalize`](#canonicalize)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L153-L162)</sub>
``` clojure

(canonicalize [f])
(canonicalize [f {:keys [:nofollow-links]}])
```


Returns the canonical path via
  java.io.File#getCanonicalPath. If `:nofollow-links` is set, then it
  will fall back on [`absolutize`](#absolutize) + [`normalize.`](#normalize) This function can be used
  as an alternative to [`real-path`](#real-path) which requires files to exist.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L153-L162)</sub>
## [`components`](#components)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L134-L137)</sub>
``` clojure

(components [f])
```


Returns a seq of all components of f.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L134-L137)</sub>
## [`copy`](#copy)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L343-L359)</sub>
``` clojure

(copy [src dest])
(copy [src dest {:keys [:replace-existing :copy-attributes :nofollow-links]}])
```


Copies src file to dest dir or file.
  Options:
  * `:replace-existing`
  * `:copy-attributes`
  * `:nofollow-links` (used to determine to copy symbolic link itself or not).

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L343-L359)</sub>
## [`copy-tree`](#copy-tree)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L406-L444)</sub>
``` clojure

(copy-tree [src dest])
(copy-tree [src dest {:keys [:replace-existing :copy-attributes :nofollow-links], :as opts}])
```


Copies entire file tree from src to dest. Creates dest if needed
  using [`create-dirs`](#create-dirs), passing it the `:posix-file-permissions`
  option. Supports same options as copy.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L406-L444)</sub>
## [`create-dir`](#create-dir)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L392-L398)</sub>
``` clojure

(create-dir [path])
(create-dir [path {:keys [:posix-file-permissions]}])
```


Creates dir using `Files#createDirectory`. Does not create parents.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L392-L398)</sub>
## [`create-dirs`](#create-dirs)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L400-L404)</sub>
``` clojure

(create-dirs [path])
(create-dirs [path {:keys [:posix-file-permissions]}])
```


Creates directories using `Files#createDirectories`. Also creates parents if needed.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L400-L404)</sub>
## [`create-file`](#create-file)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L547-L553)</sub>
``` clojure

(create-file [path])
(create-file [path {:keys [:posix-file-permissions]}])
```


Creates empty file using `Files#createFile`.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L547-L553)</sub>
## [`create-link`](#create-link)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L511-L516)</sub>
``` clojure

(create-link [path target])
```


Create a hard link from path to target.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L511-L516)</sub>
## [`create-sym-link`](#create-sym-link)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L503-L509)</sub>
``` clojure

(create-sym-link [path target])
```


Create a soft link from path to target.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L503-L509)</sub>
## [`create-temp-dir`](#create-temp-dir)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L451-L473)</sub>
``` clojure

(create-temp-dir [])
(create-temp-dir [{:keys [:prefix :path :posix-file-permissions]}])
```


Creates a temporary directory using Files#createDirectories.

  `(create-temp-dir)`: creates temp dir with random prefix.
  `(create-temp-dir {:keys [:prefix :path :posix-file-permissions]})`:

  create temp dir in path with prefix. If prefix is not provided, a random one
  is generated. If path is not provided, the directory is created as if called with `(create-temp-dir)`. The `:posix-file-permissions` option is a string like `"rwx------"`.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L451-L473)</sub>
## [`create-temp-file`](#create-temp-file)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L475-L501)</sub>
``` clojure

(create-temp-file [])
(create-temp-file [{:keys [:path :prefix :suffix :posix-file-permissions]}])
```


Creates an empty temporary file using Files#createTempFile.

  - `(create-temp-file)`: creates temp file with random prefix and suffix.
  - `(create-temp-dir {:keys [:prefix :suffix :path :posix-file-permissions]})`: create
  temp file in path with prefix. If prefix and suffix are not
  provided, random ones are generated. The `:posix-file-permissions`
  option is a string like `"rwx------"`.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L475-L501)</sub>
## [`creation-time`](#creation-time)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L710-L715)</sub>
``` clojure

(creation-time [f])
(creation-time [f {:keys [nofollow-links], :as opts}])
```


Returns creation time as FileTime.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L710-L715)</sub>
## [`cwd`](#cwd)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1017-L1020)</sub>
``` clojure

(cwd [])
```


Returns current working directory as path

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1017-L1020)</sub>
## [`delete`](#delete)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L518-L524)</sub>
``` clojure

(delete [f])
```


Deletes f. Returns nil if the delete was successful,
  throws otherwise. Does not follow symlinks.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L518-L524)</sub>
## [`delete-if-exists`](#delete-if-exists)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L526-L530)</sub>
``` clojure

(delete-if-exists [f])
```


Deletes f if it exists. Returns true if the delete was successful,
  false if f didn't exist. Does not follow symlinks.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L526-L530)</sub>
## [`delete-on-exit`](#delete-on-exit)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L579-L583)</sub>
``` clojure

(delete-on-exit [f])
```


Requests delete on exit via `File#deleteOnExit`. Returns f.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L579-L583)</sub>
## [`delete-tree`](#delete-tree)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L535-L545)</sub>
``` clojure

(delete-tree [root])
```


Deletes a file tree using [`walk-file-tree`](#walk-file-tree). Similar to `rm -rf`. Does not follow symlinks.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L535-L545)</sub>
## [`directory?`](#directory?)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L87-L92)</sub>
``` clojure

(directory? [f])
(directory? [f {:keys [:nofollow-links]}])
```


Returns true if f is a directory, using Files/isDirectory.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L87-L92)</sub>
## [`ends-with?`](#ends-with?)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L841-L844)</sub>
``` clojure

(ends-with? [this other])
```


Returns true if path this ends with path other.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L841-L844)</sub>
## [`exec-paths`](#exec-paths)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L767-L771)</sub>
``` clojure

(exec-paths [])
```


Returns executable paths (using the PATH environment variable). Same
  as `(split-paths (System/getenv "PATH"))`.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L767-L771)</sub>
## [`executable?`](#executable?)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L108-L110)</sub>
``` clojure

(executable? [f])
```


Returns true if f is executable.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L108-L110)</sub>
## [`exists?`](#exists?)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L124-L130)</sub>
``` clojure

(exists? [f])
(exists? [f {:keys [:nofollow-links]}])
```


Returns true if f exists.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L124-L130)</sub>
## [`expand-home`](#expand-home)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L995-L1010)</sub>
``` clojure

(expand-home [f])
```


If [`path`](#path) begins with a tilde (`~`), expand the tilde to the value
  of the `user.home` system property. If the [`path`](#path) begins with a
  tilde immediately followed by some characters, they are assumed to
  be a username. This is expanded to the path to that user's home
  directory. This is (naively) assumed to be a directory with the same
  name as the user relative to the parent of the current value of
  `user.home`.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L995-L1010)</sub>
## [`extension`](#extension)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L757-L760)</sub>
``` clojure

(extension [path])
```


Returns the extension of a file

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L757-L760)</sub>
## [`file`](#file)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L58-L63)</sub>
``` clojure

(file [f])
(file [f & fs])
```


Coerces f into a File. Multiple-arg versions treat the first argument
  as parent and subsequent args as children relative to the parent.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L58-L63)</sub>
## [`file-name`](#file-name)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L164-L167)</sub>
``` clojure

(file-name [x])
```


Returns the name of the file or directory. E.g. (file-name "foo/bar/baz") returns "baz".

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L164-L167)</sub>
## [`file-separator`](#file-separator)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L235-L235)</sub>

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L235-L235)</sub>
## [`file-time->instant`](#file-time->instant)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L671-L674)</sub>
``` clojure

(file-time->instant [ft])
```


Converts a java.nio.file.attribute.FileTime to a java.time.Instant.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L671-L674)</sub>
## [`file-time->millis`](#file-time->millis)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L681-L684)</sub>
``` clojure

(file-time->millis [ft])
```


Converts a java.nio.file.attribute.FileTime to epoch millis (long).

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L681-L684)</sub>
## [`get-attribute`](#get-attribute)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L621-L627)</sub>
``` clojure

(get-attribute [path attribute])
(get-attribute [path attribute {:keys [:nofollow-links]}])
```


<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L621-L627)</sub>
## [`glob`](#glob)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L309-L332)</sub>
``` clojure

(glob [root pattern])
(glob [root pattern opts])
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

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L309-L332)</sub>
## [`hidden?`](#hidden?)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L100-L102)</sub>
``` clojure

(hidden? [f])
```


Returns true if f is hidden.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L100-L102)</sub>
## [`home`](#home)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L987-L993)</sub>
``` clojure

(home [])
(home [user])
```


With no arguments, returns the current value of the `user.home`
  system property. If a `user` is passed, returns that user's home
  directory as found in the parent of home with no args.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L987-L993)</sub>
## [`instant->file-time`](#instant->file-time)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L676-L679)</sub>
``` clojure

(instant->file-time [instant])
```


Converts a java.time.Instant to a java.nio.file.attribute.FileTime.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L676-L679)</sub>
## [`last-modified-time`](#last-modified-time)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L696-L701)</sub>
``` clojure

(last-modified-time [f])
(last-modified-time [f {:keys [nofollow-links], :as opts}])
```


Returns last modified time as a java.nio.file.attribute.FileTime.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L696-L701)</sub>
## [`list-dir`](#list-dir)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L225-L233)</sub>
``` clojure

(list-dir [dir])
(list-dir [dir glob-or-accept])
```


Returns all paths in dir as vector. For descending into subdirectories use `glob.`
     - `glob-or-accept` - a glob string such as "*.edn" or a (fn accept [^java.nio.file.Path p]) -> truthy

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L225-L233)</sub>
## [`list-dirs`](#list-dirs)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L724-L728)</sub>
``` clojure

(list-dirs [dirs glob-or-accept])
```


Similar to list-dir but accepts multiple roots and returns the concatenated results.
  - `glob-or-accept` - a glob string such as "*.edn" or a (fn accept [^java.nio.file.Path p]) -> truthy

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L724-L728)</sub>
## [`match`](#match)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L243-L307)</sub>
``` clojure

(match [root pattern])
(match [root pattern {:keys [hidden follow-links max-depth recursive]}])
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

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L243-L307)</sub>
## [`millis->file-time`](#millis->file-time)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L686-L689)</sub>
``` clojure

(millis->file-time [millis])
```


Converts epoch millis (long) to a java.nio.file.attribute.FileTime.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L686-L689)</sub>
## [`modified-since`](#modified-since)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L873-L882)</sub>
``` clojure

(modified-since [anchor file-set])
```


Returns seq of regular files (non-directories, non-symlinks) from file-set that were modified since the anchor path.
  The anchor path can be a regular file or directory, in which case
  the recursive max last modified time stamp is used as the timestamp
  to compare with.  The file-set may be a regular file, directory or
  collection of files (e.g. returned by glob). Directories are
  searched recursively.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L873-L882)</sub>
## [`move`](#move)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L555-L568)</sub>
``` clojure

(move [source target])
(move [source target {:keys [:replace-existing :atomic-move :nofollow-links]}])
```


Move or rename a file to a target dir or file via `Files/move`.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L555-L568)</sub>
## [`normalize`](#normalize)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L148-L151)</sub>
``` clojure

(normalize [f])
```


Normalizes f via Path#normalize.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L148-L151)</sub>
## [`parent`](#parent)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L570-L573)</sub>
``` clojure

(parent [f])
```


Returns parent of f, is it exists.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L570-L573)</sub>
## [`path`](#path)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L48-L56)</sub>
``` clojure

(path [f])
(path [parent child])
(path [parent child & more])
```


Coerces f into a Path. Multiple-arg versions treat the first argument as
  parent and subsequent args as children relative to the parent.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L48-L56)</sub>
## [`path-separator`](#path-separator)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L236-L236)</sub>

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L236-L236)</sub>
## [`posix->str`](#posix->str)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L361-L364)</sub>
``` clojure

(posix->str [p])
```


Converts a set of PosixFilePermission to a string.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L361-L364)</sub>
## [`posix-file-permissions`](#posix-file-permissions)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L590-L594)</sub>
``` clojure

(posix-file-permissions [f])
(posix-file-permissions [f {:keys [:nofollow-links]}])
```


Gets f's posix file permissions. Use posix->str to view as a string.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L590-L594)</sub>
## [`read-all-bytes`](#read-all-bytes)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L601-L604)</sub>
``` clojure

(read-all-bytes [f])
```


Returns contents of file as byte array.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L601-L604)</sub>
## [`read-all-lines`](#read-all-lines)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L606-L617)</sub>
``` clojure

(read-all-lines [f])
(read-all-lines [f {:keys [charset], :or {charset "utf-8"}}])
```


Read all lines from a file.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L606-L617)</sub>
## [`read-attributes`](#read-attributes-1)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L652-L660)</sub>
``` clojure

(read-attributes [path attributes])
(read-attributes [path attributes {:keys [:nofollow-links :key-fn], :as opts}])
```


Same as [`read-attributes*`](#read-attributes-1) but turns attributes into a map and keywordizes keys.
  Keywordizing can be changed by passing a :key-fn in the options map.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L652-L660)</sub>
## [`read-attributes*`](#read-attributes-1)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L634-L650)</sub>
``` clojure

(read-attributes* [path attributes])
(read-attributes* [path attributes {:keys [:nofollow-links]}])
```


Reads attributes via Files/readAttributes.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L634-L650)</sub>
## [`readable?`](#readable?)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L112-L114)</sub>
``` clojure

(readable? [f])
```


Returns true if f is readable

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L112-L114)</sub>
## [`real-path`](#real-path)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L72-L76)</sub>
``` clojure

(real-path [f])
(real-path [f {:keys [:nofollow-links]}])
```


Converts f into real path via Path#toRealPath.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L72-L76)</sub>
## [`regular-file?`](#regular-file?)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L80-L85)</sub>
``` clojure

(regular-file? [f])
(regular-file? [f {:keys [:nofollow-links]}])
```


Returns true if f is a regular file, using Files/isRegularFile.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L80-L85)</sub>
## [`relative?`](#relative?)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L120-L122)</sub>
``` clojure

(relative? [f])
```


Returns true if f represents a relative path.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L120-L122)</sub>
## [`relativize`](#relativize)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L143-L146)</sub>
``` clojure

(relativize [this other])
```


Returns relative path by comparing this with other.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L143-L146)</sub>
## [`same-file?`](#same-file?)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L596-L599)</sub>
``` clojure

(same-file? [this other])
```


Returns true if this is the same file as other.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L596-L599)</sub>
## [`set-attribute`](#set-attribute)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L662-L669)</sub>
``` clojure

(set-attribute [path attribute value])
(set-attribute [path attribute value {:keys [:nofollow-links]}])
```


<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L662-L669)</sub>
## [`set-creation-time`](#set-creation-time)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L717-L722)</sub>
``` clojure

(set-creation-time [f time])
(set-creation-time [f time {:keys [nofollow-links], :as opts}])
```


Sets creation time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L717-L722)</sub>
## [`set-last-modified-time`](#set-last-modified-time)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L703-L708)</sub>
``` clojure

(set-last-modified-time [f time])
(set-last-modified-time [f time {:keys [nofollow-links], :as opts}])
```


Sets last modified time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L703-L708)</sub>
## [`set-posix-file-permissions`](#set-posix-file-permissions)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L585-L588)</sub>
``` clojure

(set-posix-file-permissions [f posix-file-permissions])
```


Sets posix file permissions on f. Accepts a string like `"rwx------"` or a set of PosixFilePermission.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L585-L588)</sub>
## [`size`](#size)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L575-L577)</sub>
``` clojure

(size [f])
```


<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L575-L577)</sub>
## [`split-ext`](#split-ext)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L730-L739)</sub>
``` clojure

(split-ext [path])
```


Splits a path into a vec of [path-without-ext ext]. Works with strings, files, or paths.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L730-L739)</sub>
## [`split-paths`](#split-paths)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L762-L765)</sub>
``` clojure

(split-paths [joined-paths])
```


Splits a string joined by the OS-specific path-seperator into a vec of paths.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L762-L765)</sub>
## [`starts-with?`](#starts-with?)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L836-L839)</sub>
``` clojure

(starts-with? [this other])
```


Returns true if path this starts with path other.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L836-L839)</sub>
## [`str->posix`](#str->posix)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L366-L369)</sub>
``` clojure

(str->posix [s])
```


Converts a string to a set of PosixFilePermission.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L366-L369)</sub>
## [`strip-ext`](#strip-ext)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L741-L755)</sub>
``` clojure

(strip-ext [path])
(strip-ext [path {:keys [ext]}])
```


Returns the path with the extension removed. If provided, a specific extension will be removed.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L741-L755)</sub>
## [`sym-link?`](#sym-link?)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L532-L533)</sub>
``` clojure

(sym-link? [f])
```


<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L532-L533)</sub>
## [`temp-dir`](#temp-dir)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L446-L449)</sub>
``` clojure

(temp-dir [])
```


Returns `java.io.tmpdir` property as path.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L446-L449)</sub>
## [`unzip`](#unzip)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L888-L915)</sub>
``` clojure

(unzip [zip-file])
(unzip [zip-file dest])
(unzip [zip-file dest {:keys [replace-existing]}])
```


zip-file: zip archive to unzip (required)
   dest: destination directory (defaults to ".")
   Options:
   * `:replace-existing` - `true` / `false`: overwrite existing files

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L888-L915)</sub>
## [`walk-file-tree`](#walk-file-tree)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L171-L205)</sub>
``` clojure

(walk-file-tree [f {:keys [:pre-visit-dir :post-visit-dir :visit-file :visit-file-failed :follow-links :max-depth]}])
```


Walks f using Files/walkFileTree. Visitor functions: :pre-visit-dir,
  :post-visit-dir, :visit-file, :visit-file-failed. All visitor functions
  default to (constantly :continue). Supported return
  values: :continue, :skip-subtree, :skip-siblings, :terminate. A
  different return value will throw.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L171-L205)</sub>
## [`which`](#which)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L780-L823)</sub>
``` clojure

(which [program])
(which [program opts])
```


Locates a program in (exec-paths) similar to the which Unix command.
  On Windows it tries to resolve in the order of: .com, .exe, .bat,
  .cmd.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L780-L823)</sub>
## [`which-all`](#which-all)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L825-L828)</sub>
``` clojure

(which-all [program])
(which-all [program opts])
```


<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L825-L828)</sub>
## [`windows?`](#windows?)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1012-L1015)</sub>
``` clojure

(windows? [])
```


Returns true if OS is Windows.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1012-L1015)</sub>
## [`with-temp-dir`](#with-temp-dir)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L965-L979)</sub>
``` clojure

(with-temp-dir [[binding-name options & more] & body])
```


Macro.


Evaluate body with binding-name bound to a temporary directory.

  The directory is created by passing `options` to [`create-temp-dir`](#create-temp-dir),
  and will be removed with [`delete-tree`](#delete-tree) on exit from the scope.

  `options` is a map with the keys as for create-temp-dir.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L965-L979)</sub>
## [`writable?`](#writable?)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L116-L118)</sub>
``` clojure

(writable? [f])
```


Returns true if f is writable

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L116-L118)</sub>
## [`zip`](#zip)  -  <sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L946-L961)</sub>
``` clojure

(zip [zip-file entries])
(zip [zip-file entries _opts])
```


Zips entry or entries into zip-file. An entry may be a file or
  directory. Directories are included recursively and their names are
  preserved in the zip file. Currently only accepts relative entries.

<sub>[source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L946-L961)</sub>
