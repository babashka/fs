## babashka.fs
<details>


<summary><code>absolute?</code> - Returns true if f represents an absolute path. </summary>


### `absolute?`
> <code>[f]</code><br>

Returns true if f represents an absolute path.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L103-L105)
</details>


<details>


<summary><code>absolutize</code> - Converts f into an absolute path via Path#toAbsolutePath. </summary>


### `absolutize`
> <code>[f]</code><br>

Converts f into an absolute path via Path#toAbsolutePath.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L138-L140)
</details>


<details>


<summary><code>canonicalize</code> - Returns the canonical path via </summary>


### `canonicalize`
> <code>[f]</code><br>
> <code>[f {:keys [:nofollow-links]}]</code><br>

Returns the canonical path via
  java.io.File#getCanonicalPath. If :nofollow-links is set, then it
  will fall back on absolutize + normalize. This function can be used
  as an alternative to real-path which requires files to exist.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L152-L161)
</details>


<details>


<summary><code>components</code> - Returns a seq of all components of f. </summary>


### `components`
> <code>[f]</code><br>

Returns a seq of all components of f.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L133-L136)
</details>


<details>


<summary><code>copy</code> - Copies src file to dest dir or file. </summary>


### `copy`
> <code>[src dest]</code><br>
> <code>[src dest {:keys [:replace-existing :copy-attributes :nofollow-links]}]</code><br>

Copies src file to dest dir or file.
  Options:
  - :replace-existing
  - :copy-attributes
  - :nofollow-links (used to determine to copy symbolic link itself or not).

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L342-L358)
</details>


<details>


<summary><code>copy-tree</code> - Copies entire file tree from src to dest </summary>


### `copy-tree`
> <code>[src dest]</code><br>
> <code>[src dest {:keys [:replace-existing :copy-attributes :nofollow-links], :as opts}]</code><br>

Copies entire file tree from src to dest. Creates dest if needed
  using create-dirs, passing it the :posix-file-permissions
  option. Supports same options as copy.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L405-L443)
</details>


<details>


<summary><code>create-dir</code> - Creates dir using Files#createDirectory </summary>


### `create-dir`
> <code>[path]</code><br>
> <code>[path {:keys [:posix-file-permissions]}]</code><br>

Creates dir using Files#createDirectory. Does not create parents.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L391-L397)
</details>


<details>


<summary><code>create-dirs</code> - Creates directories using Files#createDirectories </summary>


### `create-dirs`
> <code>[path]</code><br>
> <code>[path {:keys [:posix-file-permissions]}]</code><br>

Creates directories using Files#createDirectories. Also creates parents if needed.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L399-L403)
</details>


<details>


<summary><code>create-file</code> - Creates empty file using Files#createFile. </summary>


### `create-file`
> <code>[path]</code><br>
> <code>[path {:keys [:posix-file-permissions]}]</code><br>

Creates empty file using Files#createFile.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L546-L552)
</details>


<details>


<summary><code>create-link</code> - Create a hard link from path to target. </summary>


### `create-link`
> <code>[path target]</code><br>

Create a hard link from path to target.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L510-L515)
</details>


<details>


<summary><code>create-sym-link</code> - Create a soft link from path to target. </summary>


### `create-sym-link`
> <code>[path target]</code><br>

Create a soft link from path to target.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L502-L508)
</details>


<details>


<summary><code>create-temp-dir</code> - Creates a temporary directory using Files#createDirectories. </summary>


### `create-temp-dir`
> <code>[]</code><br>
> <code>[{:keys [:prefix :path :posix-file-permissions]}]</code><br>

Creates a temporary directory using Files#createDirectories.

  (create-temp-dir): creates temp dir with random prefix.
  (create-temp-dir {:keys [:prefix :path :posix-file-permissions]}):

  create temp dir in path with prefix. If prefix is not provided, a random one
  is generated. If path is not provided, the directory is created as if called with (create-temp-dir). The :posix-file-permissions option is a string like "rwx------".

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L450-L472)
</details>


<details>


<summary><code>create-temp-file</code> - Creates an empty temporary file using Files#createTempFile. </summary>


### `create-temp-file`
> <code>[]</code><br>
> <code>[{:keys [:path :prefix :suffix :posix-file-permissions]}]</code><br>

Creates an empty temporary file using Files#createTempFile.

  - (create-temp-file): creates temp file with random prefix and suffix.
  - (create-temp-dir {:keys [:prefix :suffix :path :posix-file-permissions]}): create
  temp file in path with prefix. If prefix and suffix are not
  provided, random ones are generated. The :posix-file-permissions
  option is a string like "rwx------".

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L474-L500)
</details>


<details>


<summary><code>creation-time</code> - Returns creation time as FileTime. </summary>


### `creation-time`
> <code>[f]</code><br>
> <code>[f {:keys [nofollow-links], :as opts}]</code><br>

Returns creation time as FileTime.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L700-L705)
</details>


<details>


<summary><code>cwd</code> - Returns current working directory as path </summary>


### `cwd`
> <code>[]</code><br>

Returns current working directory as path

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1009-L1012)
</details>


<details>


<summary><code>delete</code> - Deletes f </summary>


### `delete`
> <code>[f]</code><br>

Deletes f. Returns nil if the delete was successful,
  throws otherwise. Does not follow symlinks.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L517-L523)
</details>


<details>


<summary><code>delete-if-exists</code> - Deletes f if it exists </summary>


### `delete-if-exists`
> <code>[f]</code><br>

Deletes f if it exists. Returns true if the delete was successful,
  false if f didn't exist. Does not follow symlinks.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L525-L529)
</details>


<details>


<summary><code>delete-on-exit</code> - Requests delete on exit via File#deleteOnExit </summary>


### `delete-on-exit`
> <code>[f]</code><br>

Requests delete on exit via File#deleteOnExit. Returns f.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L578-L582)
</details>


<details>


<summary><code>delete-tree</code> - Deletes a file tree using walk-file-tree </summary>


### `delete-tree`
> <code>[root]</code><br>

Deletes a file tree using walk-file-tree. Similar to rm -rf. Does not follow symlinks.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L534-L544)
</details>


<details>


<summary><code>directory?</code> - Returns true if f is a directory, using Files/isDirectory. </summary>


### `directory?`
> <code>[f]</code><br>
> <code>[f {:keys [:nofollow-links]}]</code><br>

Returns true if f is a directory, using Files/isDirectory.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L86-L91)
</details>


<details>


<summary><code>ends-with?</code> - Returns true if path this ends with path other. </summary>


### `ends-with?`
> <code>[this other]</code><br>

Returns true if path this ends with path other.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L833-L836)
</details>


<details>


<summary><code>exec-paths</code> - Returns executable paths (using the PATH environment variable) </summary>


### `exec-paths`
> <code>[]</code><br>

Returns executable paths (using the PATH environment variable). Same
  as (split-paths (System/getenv "PATH")).

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L759-L763)
</details>


<details>


<summary><code>executable?</code> - Returns true if f is executable. </summary>


### `executable?`
> <code>[f]</code><br>

Returns true if f is executable.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L107-L109)
</details>


<details>


<summary><code>exists?</code> - Returns true if f exists. </summary>


### `exists?`
> <code>[f]</code><br>
> <code>[f {:keys [:nofollow-links]}]</code><br>

Returns true if f exists.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L123-L129)
</details>


<details>


<summary><code>expand-home</code> - If `path` begins with a tilde (`~`), expand the tilde to the value </summary>


### `expand-home`
> <code>[f]</code><br>

If `path` begins with a tilde (`~`), expand the tilde to the value
  of the `user.home` system property. If the `path` begins with a
  tilde immediately followed by some characters, they are assumed to
  be a username. This is expanded to the path to that user's home
  directory. This is (naively) assumed to be a directory with the same
  name as the user relative to the parent of the current value of
  `user.home`.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L987-L1002)
</details>


<details>


<summary><code>extension</code> - Returns the extension of a file </summary>


### `extension`
> <code>[path]</code><br>

Returns the extension of a file

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L749-L752)
</details>


<details>


<summary><code>file</code> - Coerces f into a File </summary>


### `file`
> <code>[f]</code><br>
> <code>[f & fs]</code><br>

Coerces f into a File. Multiple-arg versions treat the first argument
  as parent and subsequent args as children relative to the parent.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L57-L62)
</details>


<details>


<summary><code>file-name</code> - Returns the name of the file or directory </summary>


### `file-name`
> <code>[x]</code><br>

Returns the name of the file or directory. E.g. (file-name "foo/bar/baz") returns "baz".

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L163-L166)
</details>


<details>


<summary><code>file-separator</code> </summary>


### `file-separator`

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L234-L234)
</details>


<details>


<summary><code>file-time->instant</code> - Converts a java.nio.file.attribute.FileTime to a java.time.Instant. </summary>


### `file-time->instant`
> <code>[ft]</code><br>

Converts a java.nio.file.attribute.FileTime to a java.time.Instant.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L661-L664)
</details>


<details>


<summary><code>file-time->millis</code> - Converts a java.nio.file.attribute.FileTime to epoch millis (long). </summary>


### `file-time->millis`
> <code>[ft]</code><br>

Converts a java.nio.file.attribute.FileTime to epoch millis (long).

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L671-L674)
</details>


<details>


<summary><code>get-attribute</code> </summary>


### `get-attribute`
> <code>[path attribute]</code><br>
> <code>[path attribute {:keys [:nofollow-links]}]</code><br>

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L611-L617)
</details>


<details>


<summary><code>glob</code> - Given a file and glob pattern, returns matches as vector of </summary>


### `glob`
> <code>[root pattern]</code><br>
> <code>[root pattern opts]</code><br>

Given a file and glob pattern, returns matches as vector of
  files. Patterns containing ** or / will cause a recursive walk over
  path, unless overriden with :recursive. Glob interpretation is done
  using the rules described in
  https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String).

  Options:

  - :hidden: match hidden files. Note: on Windows files starting with
  a dot are not hidden, unless their hidden attribute is set.
  - :follow-links: follow symlinks.
  - :recursive: force recursive search.

  Examples:
  (fs/glob "." "**.clj")

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L308-L331)
</details>


<details>


<summary><code>hidden?</code> - Returns true if f is hidden. </summary>


### `hidden?`
> <code>[f]</code><br>

Returns true if f is hidden.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L99-L101)
</details>


<details>


<summary><code>home</code> - With no arguments, returns the current value of the `user.home` </summary>


### `home`
> <code>[]</code><br>
> <code>[user]</code><br>

With no arguments, returns the current value of the `user.home`
  system property. If a `user` is passed, returns that user's home
  directory as found in the parent of home with no args.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L979-L985)
</details>


<details>


<summary><code>instant->file-time</code> - Converts a java.time.Instant to a java.nio.file.attribute.FileTime. </summary>


### `instant->file-time`
> <code>[instant]</code><br>

Converts a java.time.Instant to a java.nio.file.attribute.FileTime.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L666-L669)
</details>


<details>


<summary><code>last-modified-time</code> - Returns last modified time as a java.nio.file.attribute.FileTime. </summary>


### `last-modified-time`
> <code>[f]</code><br>
> <code>[f {:keys [nofollow-links], :as opts}]</code><br>

Returns last modified time as a java.nio.file.attribute.FileTime.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L686-L691)
</details>


<details>


<summary><code>list-dir</code> - Returns all paths in dir as vector </summary>


### `list-dir`
> <code>[dir]</code><br>
> <code>[dir glob-or-accept]</code><br>

Returns all paths in dir as vector. For descending into subdirectories use glob.
     - `glob-or-accept` - a glob string such as "*.edn" or a (fn accept [^java.nio.file.Path p]) -> truthy

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L224-L232)
</details>


<details>


<summary><code>list-dirs</code> - Similar to list-dir but accepts multiple roots and returns the concatenated resu </summary>


### `list-dirs`
> <code>[dirs glob-or-accept]</code><br>

Similar to list-dir but accepts multiple roots and returns the concatenated results.
  - `glob-or-accept` - a glob string such as "*.edn" or a (fn accept [^java.nio.file.Path p]) -> truthy

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L714-L718)
</details>


<details>


<summary><code>match</code> - Given a file and match pattern, returns matches as vector of </summary>


### `match`
> <code>[root pattern]</code><br>
> <code>[root pattern {:keys [hidden follow-links max-depth recursive]}]</code><br>

Given a file and match pattern, returns matches as vector of
  files. Pattern interpretation is done using the rules described in
  https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String).

  Options:

  - :hidden: match hidden files. Note: on Windows files starting with
  a dot are not hidden, unless their hidden attribute is set.
  - :follow-links: follow symlinks
  - :recursive: match recursively.
  - :max-depth: max depth to descend into directory structure.

  Examples:
  (fs/match "." "regex:.*\\.clj" {:recursive true})

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L242-L306)
</details>


<details>


<summary><code>millis->file-time</code> - Converts epoch millis (long) to a java.nio.file.attribute.FileTime. </summary>


### `millis->file-time`
> <code>[millis]</code><br>

Converts epoch millis (long) to a java.nio.file.attribute.FileTime.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L676-L679)
</details>


<details>


<summary><code>modified-since</code> - Returns seq of regular files (non-directories, non-symlinks) from file-set that  </summary>


### `modified-since`
> <code>[anchor file-set]</code><br>

Returns seq of regular files (non-directories, non-symlinks) from file-set that were modified since the anchor path.
  The anchor path can be a regular file or directory, in which case
  the recursive max last modified time stamp is used as the timestamp
  to compare with.  The file-set may be a regular file, directory or
  collection of files (e.g. returned by glob). Directories are
  searched recursively.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L865-L874)
</details>


<details>


<summary><code>move</code> - Move or rename a file to a target dir or file via Files/move. </summary>


### `move`
> <code>[source target]</code><br>
> <code>[source target {:keys [:replace-existing :atomic-move :nofollow-links]}]</code><br>

Move or rename a file to a target dir or file via Files/move.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L554-L567)
</details>


<details>


<summary><code>normalize</code> - Normalizes f via Path#normalize. </summary>


### `normalize`
> <code>[f]</code><br>

Normalizes f via Path#normalize.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L147-L150)
</details>


<details>


<summary><code>parent</code> - Returns parent of f, is it exists. </summary>


### `parent`
> <code>[f]</code><br>

Returns parent of f, is it exists.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L569-L572)
</details>


<details>


<summary><code>path</code> - Coerces f into a Path </summary>


### `path`
> <code>[f]</code><br>
> <code>[parent child]</code><br>
> <code>[parent child & more]</code><br>

Coerces f into a Path. Multiple-arg versions treat the first argument as
  parent and subsequent args as children relative to the parent.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L47-L55)
</details>


<details>


<summary><code>path-separator</code> </summary>


### `path-separator`

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L235-L235)
</details>


<details>


<summary><code>posix->str</code> - Converts a set of PosixFilePermission to a string. </summary>


### `posix->str`
> <code>[p]</code><br>

Converts a set of PosixFilePermission to a string.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L360-L363)
</details>


<details>


<summary><code>posix-file-permissions</code> - Gets f's posix file permissions </summary>


### `posix-file-permissions`
> <code>[f]</code><br>
> <code>[f {:keys [:nofollow-links]}]</code><br>

Gets f's posix file permissions. Use posix->str to view as a string.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L589-L593)
</details>


<details>


<summary><code>read-all-bytes</code> - Returns contents of file as byte array. </summary>


### `read-all-bytes`
> <code>[f]</code><br>

Returns contents of file as byte array.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L600-L603)
</details>


<details>


<summary><code>read-all-lines</code> </summary>


### `read-all-lines`
> <code>[f]</code><br>

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L605-L607)
</details>


<details>


<summary><code>read-attributes</code> - Same as read-attributes* but turns attributes into a map and keywordizes keys. </summary>


### `read-attributes`
> <code>[path attributes]</code><br>
> <code>[path attributes {:keys [:nofollow-links :key-fn], :as opts}]</code><br>

Same as read-attributes* but turns attributes into a map and keywordizes keys.
  Keywordizing can be changed by passing a :key-fn in the options map.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L642-L650)
</details>


<details>


<summary><code>read-attributes*</code> - Reads attributes via Files/readAttributes. </summary>


### `read-attributes*`
> <code>[path attributes]</code><br>
> <code>[path attributes {:keys [:nofollow-links]}]</code><br>

Reads attributes via Files/readAttributes.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L624-L640)
</details>


<details>


<summary><code>readable?</code> - Returns true if f is readable </summary>


### `readable?`
> <code>[f]</code><br>

Returns true if f is readable

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L111-L113)
</details>


<details>


<summary><code>real-path</code> - Converts f into real path via Path#toRealPath. </summary>


### `real-path`
> <code>[f]</code><br>
> <code>[f {:keys [:nofollow-links]}]</code><br>

Converts f into real path via Path#toRealPath.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L71-L75)
</details>


<details>


<summary><code>regular-file?</code> - Returns true if f is a regular file, using Files/isRegularFile. </summary>


### `regular-file?`
> <code>[f]</code><br>
> <code>[f {:keys [:nofollow-links]}]</code><br>

Returns true if f is a regular file, using Files/isRegularFile.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L79-L84)
</details>


<details>


<summary><code>relative?</code> - Returns true if f represents a relative path. </summary>


### `relative?`
> <code>[f]</code><br>

Returns true if f represents a relative path.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L119-L121)
</details>


<details>


<summary><code>relativize</code> - Returns relative path by comparing this with other. </summary>


### `relativize`
> <code>[this other]</code><br>

Returns relative path by comparing this with other.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L142-L145)
</details>


<details>


<summary><code>same-file?</code> - Returns true if this is the same file as other. </summary>


### `same-file?`
> <code>[this other]</code><br>

Returns true if this is the same file as other.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L595-L598)
</details>


<details>


<summary><code>set-attribute</code> </summary>


### `set-attribute`
> <code>[path attribute value]</code><br>
> <code>[path attribute value {:keys [:nofollow-links]}]</code><br>

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L652-L659)
</details>


<details>


<summary><code>set-creation-time</code> - Sets creation time of f to time (millis, java.time.Instant or java.nio.file.attr </summary>


### `set-creation-time`
> <code>[f time]</code><br>
> <code>[f time {:keys [nofollow-links], :as opts}]</code><br>

Sets creation time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L707-L712)
</details>


<details>


<summary><code>set-last-modified-time</code> - Sets last modified time of f to time (millis, java.time.Instant or java.nio.file </summary>


### `set-last-modified-time`
> <code>[f time]</code><br>
> <code>[f time {:keys [nofollow-links], :as opts}]</code><br>

Sets last modified time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L693-L698)
</details>


<details>


<summary><code>set-posix-file-permissions</code> - Sets posix file permissions on f </summary>


### `set-posix-file-permissions`
> <code>[f posix-file-permissions]</code><br>

Sets posix file permissions on f. Accepts a string like "rwx------" or a set of PosixFilePermission.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L584-L587)
</details>


<details>


<summary><code>size</code> </summary>


### `size`
> <code>[f]</code><br>

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L574-L576)
</details>


<details>


<summary><code>split-ext</code> - Splits a path into a vec of [path-without-ext ext] </summary>


### `split-ext`
> <code>[path]</code><br>

Splits a path into a vec of [path-without-ext ext]. Works with strings, files, or paths.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L720-L730)
</details>


<details>


<summary><code>split-paths</code> - Splits a string joined by the OS-specific path-seperator into a vec of paths. </summary>


### `split-paths`
> <code>[joined-paths]</code><br>

Splits a string joined by the OS-specific path-seperator into a vec of paths.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L754-L757)
</details>


<details>


<summary><code>starts-with?</code> - Returns true if path this starts with path other. </summary>


### `starts-with?`
> <code>[this other]</code><br>

Returns true if path this starts with path other.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L828-L831)
</details>


<details>


<summary><code>str->posix</code> - Converts a string to a set of PosixFilePermission. </summary>


### `str->posix`
> <code>[s]</code><br>

Converts a string to a set of PosixFilePermission.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L365-L368)
</details>


<details>


<summary><code>strip-ext</code> - Returns the path with the extension removed </summary>


### `strip-ext`
> <code>[path]</code><br>
> <code>[path {:keys [ext]}]</code><br>

Returns the path with the extension removed. If provided, a specific extension will be removed.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L732-L747)
</details>


<details>


<summary><code>sym-link?</code> </summary>


### `sym-link?`
> <code>[f]</code><br>

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L531-L532)
</details>


<details>


<summary><code>temp-dir</code> - Returns java.io.tmpdir property as path. </summary>


### `temp-dir`
> <code>[]</code><br>

Returns java.io.tmpdir property as path.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L445-L448)
</details>


<details>


<summary><code>unzip</code> - zip-file: zip archive to unzip (required) </summary>


### `unzip`
> <code>[zip-file]</code><br>
> <code>[zip-file dest]</code><br>
> <code>[zip-file dest {:keys [replace-existing]}]</code><br>

zip-file: zip archive to unzip (required)
   dest: destination directory (defaults to ".")
   Options:
     :replace-existing true/false: overwrite existing files

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L880-L907)
</details>


<details>


<summary><code>walk-file-tree</code> - Walks f using Files/walkFileTree </summary>


### `walk-file-tree`
> <code>[f {:keys [:pre-visit-dir :post-visit-dir :visit-file :visit-file-failed :follow-links :max-depth]}]</code><br>

Walks f using Files/walkFileTree. Visitor functions: :pre-visit-dir,
  :post-visit-dir, :visit-file, :visit-file-failed. All visitor functions
  default to (constantly :continue). Supported return
  values: :continue, :skip-subtree, :skip-siblings, :terminate. A
  different return value will throw.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L170-L204)
</details>


<details>


<summary><code>which</code> - Locates a program in (exec-paths) similar to the which Unix command. </summary>


### `which`
> <code>[program]</code><br>
> <code>[program opts]</code><br>

Locates a program in (exec-paths) similar to the which Unix command.
  On Windows it tries to resolve in the order of: .com, .exe, .bat,
  .cmd.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L772-L815)
</details>


<details>


<summary><code>which-all</code> </summary>


### `which-all`
> <code>[program]</code><br>
> <code>[program opts]</code><br>

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L817-L820)
</details>


<details>


<summary><code>windows?</code> - Returns true if OS is Windows. </summary>


### `windows?`
> <code>[]</code><br>

Returns true if OS is Windows.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1004-L1007)
</details>


<details>


<summary><code>with-temp-dir</code> - Evaluate body with binding-name bound to a temporary directory. </summary>


### `with-temp-dir`
> <code>[[binding-name options & more] & body]</code><br>

Macro.


Evaluate body with binding-name bound to a temporary directory.

  The directory is created by passing `options` to create-temp-dir, and
  will be removed with `delete-tree` on exit from the scope.

  `options` is a map with the keys as for create-temp-dir.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L957-L971)
</details>


<details>


<summary><code>writable?</code> - Returns true if f is writable </summary>


### `writable?`
> <code>[f]</code><br>

Returns true if f is writable

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L115-L117)
</details>


<details>


<summary><code>zip</code> - Zips entry or entries into zip-file </summary>


### `zip`
> <code>[zip-file entries]</code><br>
> <code>[zip-file entries _opts]</code><br>

Zips entry or entries into zip-file. An entry may be a file or
  directory. Directories are included recursively and their names are
  preserved in the zip file. Currently only accepts relative entries.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L938-L953)
</details>


<hr>
