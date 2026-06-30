# 2. Node.js parity: recurring review questions

Status: accepted

## Context

The Node.js port takes the JVM as the reference implementation. A handful of
Node `:cljs` branches get flagged repeatedly by automated review as divergences.
Most are not divergences; the rest are accepted with a reason. This records the
verdicts so they are not re-litigated. The findings below originate from
automated review, not from the maintainer.

## real-path with :nofollow-links - NOT a divergence

The cljs branch absolutizes and normalizes without resolving symlinks. Reviewers
assume JVM `toRealPath(NOFOLLOW_LINKS)` resolves intermediate symlinks and keeps
only the final component. It does not. The Javadoc states NOFOLLOW_LINKS "does
not resolve symbolic links", and an empirical test confirms it: `real-path` of
`dirlink/x` with `:nofollow-links true`, where `dirlink` is a symlink, returns
`dirlink/x` unresolved on both the JVM and Node. The cljs absolutize matches.

## copy-tree copying a directory onto itself - not a divergence

Both branches wrap `create-dirs` + the walk in `(when (not= csrc cdest) ...)`, so
`(copy-tree d d)` is a no-op returning the target on both runtimes.

The guard is required on cljs, not incidental. JVM `Files/copy` has a built-in
same-file no-op; Node `copyFileSync` does not. Without the guard the cljs walk
would call `copyFileSync` with the exclusive flag onto an existing file and throw
`EEXIST`. Verified empirically: `copyFileSync(src, dst, COPYFILE_EXCL)` onto the
same file via a `/var` vs `/private/var` alias throws `EEXIST`.

Reviewers repeatedly claim master `(copy-tree d d)` threw
`FileAlreadyExistsException` and that the guard hides a regression. It did not.
`Files/copy` of a file onto itself is a documented no-op, not a throw, so master
walked the tree and copied every file onto itself harmlessly, returning
`target-dir`. Verified three ways: master's released `babashka.fs` in babashka,
a direct `Files/copy` of a path onto itself without `REPLACE_EXISTING`, and the
current JVM build. All return without throwing. The guard only skips the now
pointless walk; the observable result is unchanged from master.

This is covered by `copy-tree-fails-on-parent-to-child-test` in
`test/babashka/fs_test.cljc`, which asserts `(copy-tree d d)` returns the target
and leaves the tree unchanged. That suite runs on the JVM, ClojureScript and
Squint, so the self-copy no-op is locked on all three.

The self-copy *detection* still differs: JVM `canonicalize` follows symlinks, cljs
uses `:nofollow-links true`, so a copy through a symlink alias is detected as
self-copy on the JVM but walked on Node. Symlink-aliased self-copy is rare and
both produce a correct tree.

## Write precision is millisecond - accepted

File times are read as BigInt nanoseconds but written through `utimesSync`, which
takes a Date or seconds-float. A set then read round-trip truncates to
milliseconds on Node. See [[0001-node-file-time-representation]] for the full
rationale. `copy` with `:copy-attributes` has the same millisecond write limit.

## write-bytes / write-lines open options - accepted

On the JVM these pass arbitrary `StandardOpenOption` values through to
`Files/write`. On Node only `:append` is honored, mapped to the `"a"`/`"w"`
`writeFileSync` flag; other options (e.g. `:truncate-existing false`) are
ignored. Node's `writeFileSync` has no equivalent for the full option set. The
common `:append` case is covered; the rest is an accepted port limitation.

## copy takes a path source on Node, not an InputStream - accepted

JVM `copy` accepts either a path or a `java.io.InputStream` as `source`,
dispatching to `Files/copy` for the stream case. The Node branch only handles a
path: it stats the source and calls `copyFileSync`. Passing a stream-like object
does not work. Reading a stream into a file would require an asynchronous read,
which conflicts with the port's synchronous-only design. Node callers use a path
source; this matches the synchronous design recorded in
[[0001-node-file-time-representation]] and the changelog.

## read-attributes / get-attribute serve only the basic view on Node - accepted

The JVM reads `basic:`, `posix:`, `unix:`, `dos:` and other attribute views. The
Node branch builds one map of basic attributes (times, size, kind), so
`get-attribute` for a `unix:`/`posix:` key such as `"unix:mode"` or
`"posix:permissions"` returns `nil` where the JVM returns a value. The common
permission case is covered by [[posix-file-permissions]]. The full view set is an
accepted port limitation.

## parent of a single dot-prefixed component differs on Node - accepted

`parent` on Node uses `path.dirname` with a guard so a single-component path like
`"foo"` returns `nil`, matching the JVM. `path.dirname` cannot distinguish
`"foo"` from `"./foo"` (both yield `"."`), so `(parent "./foo")` returns `nil` on
Node where the JVM returns `"."`. This is a lexical normalization difference, the
same class as other Node path-string diffs, and not worth a hand-written parser.

## copy of a directory source differs on Node - accepted

JVM `copy` of a directory source creates an empty target directory. The Node
branch calls `copyFileSync`, which throws `EISDIR` on a directory. `copy-tree` is
the directory API; copying a bare directory with `copy` is rare. The divergence
is accepted rather than guarded.

## move :atomic-move is ignored on Node - accepted

JVM `move` honors `:atomic-move` through `StandardCopyOption/ATOMIC_MOVE`. The Node
branch always uses `renameSync`, which has no option flag and ignores
`:atomic-move`. `renameSync` is atomic within a single filesystem, so the common
case is covered, but the option is not enforced and there is no copy-then-delete
fallback: a cross-filesystem move throws `EXDEV` on Node, where the JVM without
`:atomic-move` would copy then delete.

## walk-file-tree reads entry types from the directory listing

The Node `walk-file-tree` reads a directory with
`readdirSync(dir, {withFileTypes: true})`, which returns `Dirent` objects
carrying the entry kind. The walker uses `Dirent.isDirectory` /
`Dirent.isSymbolicLink` to decide recursion, so a non-symlink entry costs no
`statSync` at all, and a listed entry is known to exist so no `exists?` check is
needed either. This is the closest equivalent to the JVM walker carrying
`BasicFileAttributes` from the directory read.

`Dirent` is lstat-based, so it cannot tell whether a symlink points at a
directory. Under `:follow-links true` a symlink entry still needs a real
`directory?` (and an `exists?` to detect a broken link), matching the JVM
`FOLLOW_LINKS` semantics. Symlinks are the rare case; the common file/dir tree
walks with zero per-entry stats.

This is the one place the walker uses raw `readdirSync` rather than `list-dir`:
`list-dir` returns path strings and would drop the `Dirent` types, defeating the
optimization. The cycle guard still uses `realpathSync` per directory under
`:follow-links`.

## stat and stat-ns are kept as two helpers - accepted

`stat` and `stat-ns` differ only by passing `#js {:bigint true}`. Merging them
into one helper with a flag has been proposed repeatedly. It is not done: the
clean single-body version needs a leading `& [opts]` that defaults to `null`,
and `statSync(path, null)` throws (Node reads `options.bigint` off null). The
safe alternatives either keep the `if nofollow-links` branch duplicated across
arities anyway, or switch to method-via-property interop
(`(.-lstatSync node-fs)`) which is uglier than the two small fns. The six-line
duplication is the most readable option.

## same-file? reads inode and device as BigInt - accepted

The cljs branch compares `dev` and `ino` from `statSync(path, {bigint: true})`
and stringifies before comparing. A plain `statSync` returns these as JS numbers,
which lose precision above 2^53 and could report two distinct files as the same.
The BigInt read is exact.

## Consequences

- These items are settled. New reviews should consult this ADR before flagging
  them.
- Where a divergence is real but accepted (`same-file?`, write precision), it is
  documented and pragmatic, not an oversight.
