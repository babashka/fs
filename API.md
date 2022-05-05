## babashka.fs
### `absolute?`
<code>[f]</code><br>

Returns true if f represents an absolute path.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L103-L105)
### `absolutize`
<code>[f]</code><br>

Converts f into an absolute path via Path#toAbsolutePath.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L138-L140)
### `canonicalize`
<code>[f]</code><br>
<code>[f {:keys [:nofollow-links]}]</code><br>

Returns the canonical path via
  java.io.File#getCanonicalPath. If :nofollow-links is set, then it
  will fall back on absolutize + normalize. This function can be used
  as an alternative to real-path which requires files to exist.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L152-L161)
### `components`
<code>[f]</code><br>

Returns a seq of all components of f.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L133-L136)
### `copy`
<code>[src dest]</code><br>
<code>[src dest {:keys [:replace-existing :copy-attributes :nofollow-links]}]</code><br>

Copies src file to dest dir or file.
  Options:
  - :replace-existing
  - :copy-attributes
  - :nofollow-links (used to determine to copy symbolic link itself or not).

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L342-L358)
### `copy-tree`
<code>[src dest]</code><br>
<code>[src dest {:keys [:replace-existing :copy-attributes :nofollow-links] :as opts}]</code><br>

Copies entire file tree from src to dest. Creates dest if needed
  using create-dirs, passing it the :posix-file-permissions
  option. Supports same options as copy.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L405-L443)
### `create-dir`
<code>[path]</code><br>
<code>[path {:keys [:posix-file-permissions]}]</code><br>

Creates dir using Files#createDirectory. Does not create parents.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L391-L397)
### `create-dirs`
<code>[path]</code><br>
<code>[path {:keys [:posix-file-permissions]}]</code><br>

Creates directories using Files#createDirectories. Also creates parents if needed.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L399-L403)
### `create-file`
<code>[path]</code><br>
<code>[path {:keys [:posix-file-permissions]}]</code><br>

Creates empty file using Files#createFile.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L546-L552)
### `create-link`
<code>[path target]</code><br>

Create a hard link from path to target.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L510-L515)
### `create-sym-link`
<code>[path target]</code><br>

Create a soft link from path to target.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L502-L508)
### `create-temp-dir`
<code>[]</code><br>
<code>[{:keys [:prefix :path :posix-file-permissions]}]</code><br>

Creates a temporary directory using Files#createDirectories.

  (create-temp-dir): creates temp dir with random prefix.
  (create-temp-dir {:keys [:prefix :path :posix-file-permissions]}):

  create temp dir in path with prefix. If prefix is not provided, a random one
  is generated. If path is not provided, the directory is created as if called with (create-temp-dir). The :posix-file-permissions option is a string like "rwx------".

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L450-L472)
### `create-temp-file`
<code>[]</code><br>
<code>[{:keys [:path :prefix :suffix :posix-file-permissions]}]</code><br>

Creates an empty temporary file using Files#createTempFile.

  - (create-temp-file): creates temp file with random prefix and suffix.
  - (create-temp-dir {:keys [:prefix :suffix :path :posix-file-permissions]}): create
  temp file in path with prefix. If prefix and suffix are not
  provided, random ones are generated. The :posix-file-permissions
  option is a string like "rwx------".

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L474-L500)
### `creation-time`
<code>[f]</code><br>
<code>[f {:keys [nofollow-links] :as opts}]</code><br>

Returns creation time as FileTime.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L700-L705)
### `cwd`
<code>[]</code><br>

Returns current working directory as path

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L1009-L1012)
### `delete`
<code>[f]</code><br>

Deletes f. Returns nil if the delete was successful,
  throws otherwise. Does not follow symlinks.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L517-L523)
### `delete-if-exists`
<code>[f]</code><br>

Deletes f if it exists. Returns true if the delete was successful,
  false if f didn't exist. Does not follow symlinks.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L525-L529)
### `delete-on-exit`
<code>[f]</code><br>

Requests delete on exit via File#deleteOnExit. Returns f.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L578-L582)
### `delete-tree`
<code>[root]</code><br>

Deletes a file tree using walk-file-tree. Similar to rm -rf. Does not follow symlinks.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L534-L544)
### `directory?`
<code>[f]</code><br>
<code>[f {:keys [:nofollow-links]}]</code><br>

Returns true if f is a directory, using Files/isDirectory.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L86-L91)
### `ends-with?`
<code>[this other]</code><br>

Returns true if path this ends with path other.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L833-L836)
### `exec-paths`
<code>[]</code><br>

Returns executable paths (using the PATH environment variable). Same
  as (split-paths (System/getenv "PATH")).

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L759-L763)
### `executable?`
<code>[f]</code><br>

Returns true if f is executable.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L107-L109)
### `exists?`
<code>[f]</code><br>
<code>[f {:keys [:nofollow-links]}]</code><br>

Returns true if f exists.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L123-L129)
### `expand-home`
<code>[f]</code><br>

If `path` begins with a tilde (`~`), expand the tilde to the value
  of the `user.home` system property. If the `path` begins with a
  tilde immediately followed by some characters, they are assumed to
  be a username. This is expanded to the path to that user's home
  directory. This is (naively) assumed to be a directory with the same
  name as the user relative to the parent of the current value of
  `user.home`.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L987-L1002)
### `extension`
<code>[path]</code><br>

Returns the extension of a file

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L749-L752)
### `file`
<code>[f]</code><br>
<code>[f & fs]</code><br>

Coerces f into a File. Multiple-arg versions treat the first argument
  as parent and subsequent args as children relative to the parent.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L57-L62)
### `file-name`
<code>[x]</code><br>

Returns the name of the file or directory. E.g. (file-name "foo/bar/baz") returns "baz".

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L163-L166)
### `file-separator`

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L234-L234)
### `file-time->instant`
<code>[ft]</code><br>

Converts a java.nio.file.attribute.FileTime to a java.time.Instant.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L661-L664)
### `file-time->millis`
<code>[ft]</code><br>

Converts a java.nio.file.attribute.FileTime to epoch millis (long).

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L671-L674)
### `get-attribute`
<code>[path attribute]</code><br>
<code>[path attribute {:keys [:nofollow-links]}]</code><br>

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L611-L617)
### `glob`
<code>[root pattern]</code><br>
<code>[root pattern opts]</code><br>

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

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L308-L331)
### `hidden?`
<code>[f]</code><br>

Returns true if f is hidden.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L99-L101)
### `home`
<code>[]</code><br>
<code>[user]</code><br>

With no arguments, returns the current value of the `user.home`
  system property. If a `user` is passed, returns that user's home
  directory as found in the parent of home with no args.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L979-L985)
### `instant->file-time`
<code>[instant]</code><br>

Converts a java.time.Instant to a java.nio.file.attribute.FileTime.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L666-L669)
### `last-modified-time`
<code>[f]</code><br>
<code>[f {:keys [nofollow-links] :as opts}]</code><br>

Returns last modified time as a java.nio.file.attribute.FileTime.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L686-L691)
### `list-dir`
<code>[dir]</code><br>
<code>[dir glob-or-accept]</code><br>

Returns all paths in dir as vector. For descending into subdirectories use glob.
     - `glob-or-accept` - a glob string such as "*.edn" or a (fn accept [^java.nio.file.Path p]) -> truthy

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L224-L232)
### `list-dirs`
<code>[dirs glob-or-accept]</code><br>

Similar to list-dir but accepts multiple roots and returns the concatenated results.
  - `glob-or-accept` - a glob string such as "*.edn" or a (fn accept [^java.nio.file.Path p]) -> truthy

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L714-L718)
### `match`
<code>[root pattern]</code><br>
<code>[root pattern {:keys [hidden follow-links max-depth recursive]}]</code><br>

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

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L242-L306)
### `millis->file-time`
<code>[millis]</code><br>

Converts epoch millis (long) to a java.nio.file.attribute.FileTime.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L676-L679)
### `modified-since`
<code>[anchor file-set]</code><br>

Returns seq of regular files (non-directories, non-symlinks) from file-set that were modified since the anchor path.
  The anchor path can be a regular file or directory, in which case
  the recursive max last modified time stamp is used as the timestamp
  to compare with.  The file-set may be a regular file, directory or
  collection of files (e.g. returned by glob). Directories are
  searched recursively.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L865-L874)
### `move`
<code>[source target]</code><br>
<code>[source target {:keys [:replace-existing :atomic-move :nofollow-links]}]</code><br>

Move or rename a file to a target dir or file via Files/move.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L554-L567)
### `normalize`
<code>[f]</code><br>

Normalizes f via Path#normalize.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L147-L150)
### `parent`
<code>[f]</code><br>

Returns parent of f, is it exists.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L569-L572)
### `path`
<code>[f]</code><br>
<code>[parent child]</code><br>
<code>[parent child & more]</code><br>

Coerces f into a Path. Multiple-arg versions treat the first argument as
  parent and subsequent args as children relative to the parent.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L47-L55)
### `path-separator`

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L235-L235)
### `posix->str`
<code>[p]</code><br>

Converts a set of PosixFilePermission to a string.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L360-L363)
### `posix-file-permissions`
<code>[f]</code><br>
<code>[f {:keys [:nofollow-links]}]</code><br>

Gets f's posix file permissions. Use posix->str to view as a string.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L589-L593)
### `read-all-bytes`
<code>[f]</code><br>

Returns contents of file as byte array.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L600-L603)
### `read-all-lines`
<code>[f]</code><br>

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L605-L607)
### `read-attributes`
<code>[path attributes]</code><br>
<code>[path attributes {:keys [:nofollow-links :key-fn] :as opts}]</code><br>

Same as read-attributes* but turns attributes into a map and keywordizes keys.
  Keywordizing can be changed by passing a :key-fn in the options map.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L642-L650)
### `read-attributes*`
<code>[path attributes]</code><br>
<code>[path attributes {:keys [:nofollow-links]}]</code><br>

Reads attributes via Files/readAttributes.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L624-L640)
### `readable?`
<code>[f]</code><br>

Returns true if f is readable

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L111-L113)
### `real-path`
<code>[f]</code><br>
<code>[f {:keys [:nofollow-links]}]</code><br>

Converts f into real path via Path#toRealPath.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L71-L75)
### `regular-file?`
<code>[f]</code><br>
<code>[f {:keys [:nofollow-links]}]</code><br>

Returns true if f is a regular file, using Files/isRegularFile.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L79-L84)
### `relative?`
<code>[f]</code><br>

Returns true if f represents a relative path.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L119-L121)
### `relativize`
<code>[this other]</code><br>

Returns relative path by comparing this with other.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L142-L145)
### `same-file?`
<code>[this other]</code><br>

Returns true if this is the same file as other.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L595-L598)
### `set-attribute`
<code>[path attribute value]</code><br>
<code>[path attribute value {:keys [:nofollow-links]}]</code><br>

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L652-L659)
### `set-creation-time`
<code>[f time]</code><br>
<code>[f time {:keys [nofollow-links] :as opts}]</code><br>

Sets creation time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L707-L712)
### `set-last-modified-time`
<code>[f time]</code><br>
<code>[f time {:keys [nofollow-links] :as opts}]</code><br>

Sets last modified time of f to time (millis, java.time.Instant or java.nio.file.attribute.FileTime).

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L693-L698)
### `set-posix-file-permissions`
<code>[f posix-file-permissions]</code><br>

Sets posix file permissions on f. Accepts a string like "rwx------" or a set of PosixFilePermission.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L584-L587)
### `size`
<code>[f]</code><br>

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L574-L576)
### `split-ext`
<code>[path]</code><br>

Splits a path into a vec of [path-without-ext ext]. Works with strings, files, or paths.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L720-L730)
### `split-paths`
<code>[joined-paths]</code><br>

Splits a string joined by the OS-specific path-seperator into a vec of paths.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L754-L757)
### `starts-with?`
<code>[this other]</code><br>

Returns true if path this starts with path other.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L828-L831)
### `str->posix`
<code>[s]</code><br>

Converts a string to a set of PosixFilePermission.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L365-L368)
### `strip-ext`
<code>[path]</code><br>
<code>[path {:keys [ext]}]</code><br>

Returns the path with the extension removed. If provided, a specific extension will be removed.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L732-L747)
### `sym-link?`
<code>[f]</code><br>

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L531-L532)
### `temp-dir`
<code>[]</code><br>

Returns java.io.tmpdir property as path.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L445-L448)
### `unzip`
<code>[zip-file]</code><br>
<code>[zip-file dest]</code><br>
<code>[zip-file dest {:keys [replace-existing]}]</code><br>

zip-file: zip archive to unzip (required)
   dest: destination directory (defaults to ".")
   Options:
     :replace-existing true/false: overwrite existing files

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L880-L907)
### `walk-file-tree`
<code>[f {:keys [:pre-visit-dir :post-visit-dir :visit-file :visit-file-failed :follow-links :max-depth]}]</code><br>

Walks f using Files/walkFileTree. Visitor functions: :pre-visit-dir,
  :post-visit-dir, :visit-file, :visit-file-failed. All visitor functions
  default to (constantly :continue). Supported return
  values: :continue, :skip-subtree, :skip-siblings, :terminate. A
  different return value will throw.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L170-L204)
### `which`
<code>[program]</code><br>
<code>[program opts]</code><br>

Locates a program in (exec-paths) similar to the which Unix command.
  On Windows it tries to resolve in the order of: .com, .exe, .bat,
  .cmd.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L772-L815)
### `which-all`
<code>[program]</code><br>
<code>[program opts]</code><br>

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L817-L820)
### `windows?`
<code>[]</code><br>

Returns true if OS is Windows.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L1004-L1007)
### `with-temp-dir`
<code>[[binding-name options & more] & body]</code><br>

Macro.


Evaluate body with binding-name bound to a temporary directory.

  The directory is created by passing `options` to create-temp-dir, and
  will be removed with `delete-tree` on exit from the scope.

  `options` is a map with the keys as for create-temp-dir.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L957-L971)
### `writable?`
<code>[f]</code><br>

Returns true if f is writable

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L115-L117)
### `zip`
<code>[zip-file entries]</code><br>
<code>[zip-file entries _opts]</code><br>

Zips entry or entries into zip-file. An entry may be a file or
  directory. Directories are included recursively and their names are
  preserved in the zip file. Currently only accepts relative entries.

[Source](https://github.com/babashka/process/blob/main/src/babashka/fs.cljc#L938-L953)
