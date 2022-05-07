## babashka.fs
<details>


<summary><code>absolute?</code> - Returns true if f represents an absolute path. </summary>


### `absolute?`
``` clojure

(absolute? [f])
```


Returns true if f represents an absolute path.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L103-L105)
</details>


<details>


<summary><code>absolutize</code> - Converts f into an absolute path via Path#toAbsolutePath. </summary>


### `absolutize`
``` clojure

(absolutize [f])
```


Converts f into an absolute path via Path#toAbsolutePath.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L138-L140)
</details>


<details>


<summary><code>canonicalize</code> - Returns the canonical path via </summary>


### `canonicalize`
``` clojure

(canonicalize [f])
(canonicalize [f {:keys [:nofollow-links]}])
```


Returns the canonical path via
  java.io.File#getCanonicalPath. If :nofollow-links is set, then it
  will fall back on absolutize + normalize. This function can be used
  as an alternative to real-path which requires files to exist.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L152-L161)
</details>


<details>


<summary><code>components</code> - Returns a seq of all components of f. </summary>


### `components`
``` clojure

(components [f])
```


Returns a seq of all components of f.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L133-L136)
</details>


<details>


<summary><code>copy</code> - Copies src file to dest dir or file. </summary>


### `copy`
``` clojure

(copy [src dest])
(copy [src dest {:keys [:replace-existing :copy-attributes :nofollow-links]}])
```


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
``` clojure

(copy-tree [src dest])
(copy-tree [src dest {:keys [:replace-existing :copy-attributes :nofollow-links], :as opts}])
```


Copies entire file tree from src to dest. Creates dest if needed
  using create-dirs, passing it the :posix-file-permissions
  option. Supports same options as copy.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L405-L443)
</details>


<details>


<summary><code>create-dir</code> - Creates dir using Files#createDirectory </summary>


### `create-dir`
``` clojure

(create-dir [path])
(create-dir [path {:keys [:posix-file-permissions]}])
```


Creates dir using Files#createDirectory. Does not create parents.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L391-L397)
</details>


<details>


<summary><code>create-dirs</code> - Creates directories using Files#createDirectories </summary>


### `create-dirs`
``` clojure

(create-dirs [path])
(create-dirs [path {:keys [:posix-file-permissions]}])
```


Creates directories using Files#createDirectories. Also creates parents if needed.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L399-L403)
</details>


<details>


<summary><code>create-file</code> - Creates empty file using Files#createFile. </summary>


### `create-file`
``` clojure

(create-file [path])
(create-file [path {:keys [:posix-file-permissions]}])
```


Creates empty file using Files#createFile.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L546-L552)
</details>


<details>


<summary><code>create-link</code> - Create a hard link from path to target. </summary>


### `create-link`
``` clojure

(create-link [path target])
```


Create a hard link from path to target.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L510-L515)
</details>


<details>


<summary><code>create-sym-link</code> - Create a soft link from path to target. </summary>


### `create-sym-link`
``` clojure

(create-sym-link [path target])
```


Create a soft link from path to target.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L502-L508)
</details>


<details>


<summary><code>create-temp-dir</code> - Creates a temporary directory using Files#createDirectories. </summary>


### `create-temp-dir`
``` clojure

(create-temp-dir [])
(create-temp-dir [{:keys [:prefix :path :posix-file-permissions]}])
```


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
``` clojure

(create-temp-file [])
(create-temp-file [{:keys [:path :prefix :suffix :posix-file-permissions]}])
```


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
``` clojure

(creation-time [f])
(creation-time [f {:keys [nofollow-links], :as opts}])
```


Returns creation time as FileTime.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L700-L705)
</details>


<details>


<summary><code>cwd</code> - Returns current working directory as path </summary>


### `cwd`
``` clojure

(cwd [])
```


Returns current working directory as path

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1009-L1012)
</details>


<details>


<summary><code>delete</code> - Deletes f </summary>


### `delete`
``` clojure

(delete [f])
```


Deletes f. Returns nil if the delete was successful,
  throws otherwise. Does not follow symlinks.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L517-L523)
</details>


<details>


<summary><code>delete-if-exists</code> - Deletes f if it exists </summary>


### `delete-if-exists`
``` clojure

(delete-if-exists [f])
```


Deletes f if it exists. Returns true if the delete was successful,
  false if f didn't exist. Does not follow symlinks.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L525-L529)
</details>


<details>


<summary><code>delete-on-exit</code> - Requests delete on exit via File#deleteOnExit </summary>


### `delete-on-exit`
``` clojure

(delete-on-exit [f])
```


Requests delete on exit via File#deleteOnExit. Returns f.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L578-L582)
</details>


<details>


<summary><code>delete-tree</code> - Deletes a file tree using walk-file-tree </summary>


### `delete-tree`
``` clojure

(delete-tree [root])
```


Deletes a file tree using walk-file-tree. Similar to rm -rf. Does not follow symlinks.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L534-L544)
</details>


<details>


<summary><code>directory?</code> - Returns true if f is a directory, using Files/isDirectory. </summary>


### `directory?`
``` clojure

(directory? [f])
(directory? [f {:keys [:nofollow-links]}])
```


Returns true if f is a directory, using Files/isDirectory.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L86-L91)
</details>


<details>


<summary><code>ends-with?</code> - Returns true if path this ends with path other. </summary>


### `ends-with?`
``` clojure

(ends-with? [this other])
```


Returns true if path this ends with path other.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L833-L836)
</details>


<details>


<summary><code>exec-paths</code> - Returns executable paths (using the PATH environment variable) </summary>


### `exec-paths`
``` clojure

(exec-paths [])
```


Returns executable paths (using the PATH environment variable). Same
  as (split-paths (System/getenv "PATH")).

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L759-L763)
</details>


<details>


<summary><code>executable?</code> - Returns true if f is executable. </summary>


### `executable?`
``` clojure

(executable? [f])
```


Returns true if f is executable.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L107-L109)
</details>


<details>


<summary><code>exists?</code> - Returns true if f exists. </summary>


### `exists?`
``` clojure

(exists? [f])
(exists? [f {:keys [:nofollow-links]}])
```


Returns true if f exists.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L123-L129)
</details>


<details>


<summary><code>expand-home</code> - If <code>path</code> begins with a tilde (<code>~</code>), expand the tilde to the value </summary>


### `expand-home`
``` clojure

(expand-home [f])
```


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
``` clojure

(extension [path])
```


Returns the extension of a file

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L749-L752)
</details>


<details>


<summary><code>file</code> - Coerces f into a File </summary>


### `file`
``` clojure

(file [f])
(file [f & fs])
```


Coerces f into a File. Multiple-arg versions treat the first argument
  as parent and subsequent args as children relative to the parent.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L57-L62)
</details>


<details>


<summary><code>file-name</code> - Returns the name of the file or directory </summary>


### `file-name`
``` clojure

(file-name [x])
```


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
``` clojure

(file-time->instant [ft])
```


Converts a java.nio.file.attribute.FileTime to a java.time.Instant.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L661-L664)
</details>


<details>


<summary><code>file-time->millis</code> - Converts a java.nio.file.attribute.FileTime to epoch millis (long). </summary>


### `file-time->millis`
``` clojure

(file-time->millis [ft])
```


Converts a java.nio.file.attribute.FileTime to epoch millis (long).

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L671-L674)
</details>


<details>


<summary><code>get-attribute</code> </summary>


### `get-attribute`
``` clojure

(get-attribute [path attribute])
(get-attribute [path attribute {:keys [:nofollow-links]}])
```


[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L611-L617)
</details>


<details>


<summary><code>glob</code> - Given a file and glob pattern, returns matches as vector of </summary>


### `glob`
``` clojure

(glob [root pattern])
(glob [root pattern opts])
```


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
``` clojure

(hidden? [f])
```


Returns true if f is hidden.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L99-L101)
</details>


<details>


<summary><code>home</code> - With no arguments, returns the current value of the <code>user.home</code> </summary>


### `home`
``` clojure

(home [])
(home [user])
```


With no arguments, returns the current value of the `user.home`
  system property. If a `user` is passed, returns that user's home
  directory as found in the parent of home with no args.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L979-L985)
</details>


<details>


<summary><code>instant->file-time</code> - Converts a java.time.Instant to a java.nio.file.attribute.FileTime. </summary>


### `instant->file-time`
``` clojure

(instant->file-time [instant])
```


Converts a java.time.Instant to a java.nio.file.attribute.FileTime.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L666-L669)
</details>


<details>


<summary><code>last-modified-time</code> - Returns last modified time as a java.nio.file.attribute.FileTime. </summary>


### `last-modified-time`
``` clojure

(last-modified-time [f])
(last-modified-time [f {:keys [nofollow-links], :as opts}])
```


Returns last modified time as a java.nio.file.attribute.FileTime.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L686-L691)
</details>


<details>


<summary><code>list-dir</code> - Returns all paths in dir as vector </summary>


### `list-dir`
``` clojure

(list-dir [dir])
(list-dir [dir glob-or-accept])
```


Returns all paths in dir as vector. For descending into subdirectories use glob.
     - `glob-or-accept` - a glob string such as "*.edn" or a (fn accept [^java.nio.file.Path p]) -> truthy

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L224-L232)
</details>


<details>


<summary><code>list-dirs</code> - Similar to list-dir but accepts multiple roots and returns the concatenated resu </summary>


### `list-dirs`
``` clojure

(list-dirs [dirs glob-or-accept])
```


Similar to list-dir but accepts multiple roots and returns the concatenated results.
  - `glob-or-accept` - a glob string such as "*.edn" or a (fn accept [^java.nio.file.Path p]) -> truthy

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L714-L718)
</details>


<details>


<summary><code>match</code> - Given a file and match pattern, returns matches as vector of </summary>


### `match`
``` clojure

(match [root pattern])
(match [root pattern {:keys [hidden follow-links max-depth recursive]}])
```


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
``` clojure

(millis->file-time [millis])
```


Converts epoch millis (long) to a java.nio.file.attribute.FileTime.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L676-L679)
</details>


<details>


<summary><code>modified-since</code> - Returns seq of regular files (non-directories, non-symlinks) from file-set that  </summary>


### `modified-since`
``` clojure

(modified-since [anchor file-set])
```


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
``` clojure

(move [source target])
(move [source target {:keys [:replace-existing :atomic-move :nofollow-links]}])
```


Move or rename a file to a target dir or file via Files/move.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L554-L567)
</details>


<details>


<summary><code>normalize</code> - Normalizes f via Path#normalize. </summary>


### `normalize`
``` clojure

(normalize [f])
```


Normalizes f via Path#normalize.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L147-L150)
</details>


<details>


<summary><code>parent</code> - Returns parent of f, is it exists. </summary>


### `parent`
``` clojure

(parent [f])
```


Returns parent of f, is it exists.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L569-L572)
</details>


<details>


<summary><code>path</code> - Coerces f into a Path </summary>


### `path`
``` clojure

(path [f])
(path [parent child])
(path [parent child & more])
```


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
``` clojure

(posix->str [p])
```


Converts a set of PosixFilePermission to a string.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L360-L363)
</details>


<details>


<summary><code>posix-file-permissions</code> - Gets f's posix file permissions </summary>


### `posix-file-permissions`
``` clojure

(posix-file-permissions [f])
(posix-file-permissions [f {:keys [:nofollow-links]}])
```


Gets f's posix file permissions. Use posix->str to view as a string.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L589-L593)
</details>


<details>


<summary><code>read-all-bytes</code> - Returns contents of file as byte array. </summary>


### `read-all-bytes`
``` clojure

(read-all-bytes [f])
```


Returns contents of file as byte array.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L600-L603)
</details>


<details>


<summary><code>read-all-lines</code> </summary>


### `read-all-lines`
``` clojure

(read-all-lines [f])
```


[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L605-L607)
</details>


<details>


<summary><code>read-attributes</code> - Same as read-attributes* but turns attributes into a map and keywordizes keys. </summary>


### `read-attributes`
``` clojure

(read-attributes [path attributes])
(read-attributes [path attributes {:keys [:nofollow-links :key-fn], :as opts}])
```


Same as read-attributes* but turns attributes into a map and keywordizes keys.
  Keywordizing can be changed by passing a :key-fn in the options map.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L642-L650)
</details>


<details>


<summary><code>read-attributes*</code> - Reads attributes via Files/readAttributes. </summary>


### `read-attributes*`
``` clojure

(read-attributes* [path attributes])
(read-attributes* [path attributes {:keys [:nofollow-links]}])
```


Reads attributes via Files/readAttributes.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L624-L640)
</details>


<details>


<summary><code>readable?</code> - Returns true if f is readable </summary>


### `readable?`
``` clojure

(readable? [f])
```


Returns true if f is readable

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L111-L113)
</details>


<details>


<summary><code>real-path</code> - Converts f into real path via Path#toRealPath. </summary>


### `real-path`
``` clojure

(real-path [f])
(real-path [f {:keys [:nofollow-links]}])
```


Converts f into real path via Path#toRealPath.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L71-L75)
</details>


<details>


<summary><code>regular-file?</code> - Returns true if f is a regular file, using Files/isRegularFile. </summary>


### `regular-file?`
``` clojure

(regular-file? [f])
(regular-file? [f {:keys [:nofollow-links]}])
```


Returns true if f is a regular file, using Files/isRegularFile.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L79-L84)
</details>


<details>


<summary><code>relative?</code> - Returns true if f represents a relative path. </summary>


### `relative?`
``` clojure

(relative? [f])
```


Returns true if f represents a relative path.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L119-L121)
</details>


<details>


<summary><code>relativize</code> - Returns relative path by comparing this with other. </summary>


### `relativize`
``` clojure

(relativize [this other])
```


Returns relative path by comparing this with other.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L142-L145)
</details>


<details>


<summary><code>same-file?</code> - Returns true if this is the same file as other. </summary>


### `same-file?`
``` clojure

(same-file? [this other])
```


Returns true if this is the same file as other.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L595-L598)
</details>


<details>


<summary><code>set-attribute</code> </summary>


### `set-attribute`
``` clojure

(set-attribute [path attribute value])
(set-attribute [path attribute value {:keys [:nofollow-links]}])
```


[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L652-L659)
</details>


<details>


<summary><code>set-creation-time</code> - Sets creation time of f to time (millis, java.time.Instant or java.nio.file.attr </summary>


### `set-creation-time`
``` clojure

(set-creation-time [f time])
(set-creation-time [f time {:keys [nofollow-links], :as opts}])
```


Sets creation time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L707-L712)
</details>


<details>


<summary><code>set-last-modified-time</code> - Sets last modified time of f to time (millis, java.time.Instant or java.nio.file </summary>


### `set-last-modified-time`
``` clojure

(set-last-modified-time [f time])
(set-last-modified-time [f time {:keys [nofollow-links], :as opts}])
```


Sets last modified time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L693-L698)
</details>


<details>


<summary><code>set-posix-file-permissions</code> - Sets posix file permissions on f </summary>


### `set-posix-file-permissions`
``` clojure

(set-posix-file-permissions [f posix-file-permissions])
```


Sets posix file permissions on f. Accepts a string like "rwx------" or a set of PosixFilePermission.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L584-L587)
</details>


<details>


<summary><code>size</code> </summary>


### `size`
``` clojure

(size [f])
```


[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L574-L576)
</details>


<details>


<summary><code>split-ext</code> - Splits a path into a vec of [path-without-ext ext] </summary>


### `split-ext`
``` clojure

(split-ext [path])
```


Splits a path into a vec of [path-without-ext ext]. Works with strings, files, or paths.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L720-L730)
</details>


<details>


<summary><code>split-paths</code> - Splits a string joined by the OS-specific path-seperator into a vec of paths. </summary>


### `split-paths`
``` clojure

(split-paths [joined-paths])
```


Splits a string joined by the OS-specific path-seperator into a vec of paths.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L754-L757)
</details>


<details>


<summary><code>starts-with?</code> - Returns true if path this starts with path other. </summary>


### `starts-with?`
``` clojure

(starts-with? [this other])
```


Returns true if path this starts with path other.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L828-L831)
</details>


<details>


<summary><code>str->posix</code> - Converts a string to a set of PosixFilePermission. </summary>


### `str->posix`
``` clojure

(str->posix [s])
```


Converts a string to a set of PosixFilePermission.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L365-L368)
</details>


<details>


<summary><code>strip-ext</code> - Returns the path with the extension removed </summary>


### `strip-ext`
``` clojure

(strip-ext [path])
(strip-ext [path {:keys [ext]}])
```


Returns the path with the extension removed. If provided, a specific extension will be removed.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L732-L747)
</details>


<details>


<summary><code>sym-link?</code> </summary>


### `sym-link?`
``` clojure

(sym-link? [f])
```


[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L531-L532)
</details>


<details>


<summary><code>temp-dir</code> - Returns java.io.tmpdir property as path. </summary>


### `temp-dir`
``` clojure

(temp-dir [])
```


Returns java.io.tmpdir property as path.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L445-L448)
</details>


<details>


<summary><code>unzip</code> - zip-file: zip archive to unzip (required) </summary>


### `unzip`
``` clojure

(unzip [zip-file])
(unzip [zip-file dest])
(unzip [zip-file dest {:keys [replace-existing]}])
```


zip-file: zip archive to unzip (required)
   dest: destination directory (defaults to ".")
   Options:
     :replace-existing true/false: overwrite existing files

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L880-L907)
</details>


<details>


<summary><code>walk-file-tree</code> - Walks f using Files/walkFileTree </summary>


### `walk-file-tree`
``` clojure

(walk-file-tree [f {:keys [:pre-visit-dir :post-visit-dir :visit-file :visit-file-failed :follow-links :max-depth]}])
```


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
``` clojure

(which [program])
(which [program opts])
```


Locates a program in (exec-paths) similar to the which Unix command.
  On Windows it tries to resolve in the order of: .com, .exe, .bat,
  .cmd.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L772-L815)
</details>


<details>


<summary><code>which-all</code> </summary>


### `which-all`
``` clojure

(which-all [program])
(which-all [program opts])
```


[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L817-L820)
</details>


<details>


<summary><code>windows?</code> - Returns true if OS is Windows. </summary>


### `windows?`
``` clojure

(windows? [])
```


Returns true if OS is Windows.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L1004-L1007)
</details>


<details>


<summary><code>with-temp-dir</code> - Evaluate body with binding-name bound to a temporary directory. </summary>


### `with-temp-dir`
``` clojure

(with-temp-dir [[binding-name options & more] & body])
```


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
``` clojure

(writable? [f])
```


Returns true if f is writable

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L115-L117)
</details>


<details>


<summary><code>zip</code> - Zips entry or entries into zip-file </summary>


### `zip`
``` clojure

(zip [zip-file entries])
(zip [zip-file entries _opts])
```


Zips entry or entries into zip-file. An entry may be a file or
  directory. Directories are included recursively and their names are
  preserved in the zip file. Currently only accepts relative entries.

[Source](https://github.com/babashka/fs/blob/master/src/babashka/fs.cljc#L938-L953)
</details>


<hr>
