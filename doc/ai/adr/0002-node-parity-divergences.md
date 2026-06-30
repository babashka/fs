# 2. Node.js parity: recurring review questions

Status: accepted

## Context

The Node.js port takes the JVM as the reference implementation. A handful of
Node `:cljs` branches get flagged repeatedly in review as divergences. Most are
not divergences; the rest are accepted with a reason. This records the verdicts
so they are not re-litigated.

## real-path with :nofollow-links - NOT a divergence

The cljs branch absolutizes and normalizes without resolving symlinks. Reviewers
assume JVM `toRealPath(NOFOLLOW_LINKS)` resolves intermediate symlinks and keeps
only the final component. It does not. The Javadoc states NOFOLLOW_LINKS "does
not resolve symbolic links", and an empirical test confirms it: `real-path` of
`dirlink/x` with `:nofollow-links true`, where `dirlink` is a symlink, returns
`dirlink/x` unresolved on both the JVM and Node. The cljs absolutize matches.

## copy-tree copying a directory onto itself - accepted, matches JVM

The cljs branch wraps `create-dirs` + the walk in `(when (not= csrc cdest) ...)`.
The JVM has no such guard. This looks divergent but the observable behavior is
identical: both no-op and return the target.

The guard is required, not incidental. JVM `Files/copy` has a built-in same-file
no-op; Node `copyFileSync` does not. Without the guard the cljs walk would call
`copyFileSync` with the exclusive flag onto an existing file and throw `EEXIST`,
diverging from the JVM no-op. Removing the guard to "match the JVM shape" would
introduce the divergence it appears to remove.

## same-file? on a missing path - accepted

The cljs branch catches and returns `false` when a path does not exist; the JVM
`isSameFile` throws. This follows the cljs predicate convention across the port
(`exists?`, `directory?`, `regular-file?`, `sym-link?` all catch and return
`false` rather than throw), so `same-file?` is consistent with its neighbors.

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

## Consequences

- These items are settled. New reviews should consult this ADR before flagging
  them.
- Where a divergence is real but accepted (`same-file?`, write precision), it is
  documented and pragmatic, not an oversight.
