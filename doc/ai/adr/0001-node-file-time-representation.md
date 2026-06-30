# 1. Node.js file time representation

Status: accepted

## Context

`babashka.fs` runs from one `.cljc` source on the JVM, ClojureScript and squint
(Node.js). On the JVM, file times are `java.nio.file.attribute.FileTime`, which
carries nanosecond precision. The Node port needs a return type for
`last-modified-time`, `creation-time` and the time fields of `read-attributes`.

The first port used `js/Date`. A `js/Date` is millisecond precision by the JS
spec and cannot hold nanoseconds. This reintroduced issue #145 on the cljs side:
`modified-since` compares relative newness of files, and millisecond truncation
makes two files modified within the same millisecond compare as equal, so a
newer file is missed on filesystems with sub-millisecond timestamps.

Options considered for the Node time type:

- `js/Date`: ergonomic and idiomatic for Node, but millisecond only. Wrong
  precision for the requirement.
- plain number: a millisecond number loses precision (same #145 bug); a
  nanosecond number overflows `Number.MAX_SAFE_INTEGER` (~9.0e15) since epoch
  nanoseconds are ~1.7e18.
- `Temporal.Instant`: nanosecond precision, but Stage 3, needs the
  `@js-temporal/polyfill` dependency in current Node. Too heavy for a dep-free
  library.
- BigInt nanoseconds since epoch: Node's native `statSync(path, {bigint:true})`
  exposes `mtimeNs`/`atimeNs`/`ctimeNs`/`birthtimeNs` as BigInt nanoseconds.
  `Temporal.Instant#epochNanoseconds` is also a BigInt. Both the native API and
  the future standard converge on this.

## Decision

On Node.js, file times are **BigInt nanoseconds since the Unix epoch**, read
from `statSync(path, {bigint:true})`. The JVM keeps `FileTime`. The conversion
functions bridge the two: `file-time->millis` divides by 1e6 to a number,
`millis->file-time` multiplies a millisecond number by 1e6 to a BigInt.

`modified-since` reads nanosecond BigInt times and compares them directly,
honoring issue #145 on Node.

Writes stay millisecond precision: Node's `utimesSync` accepts only a Date or a
seconds-float, neither of which can carry nanoseconds. A `set-last-modified-time`
then `last-modified-time` round-trip therefore truncates to milliseconds on
write. This is accepted. Reads carry full filesystem precision, which is what
relative newness comparison needs.

## Consequences

- Node file times are BigInt, not a date object. Code formats and converts
  through the existing conversion functions (`file-time->millis`,
  `file-time->instant`, `millis->file-time`, `instant->file-time`) rather than
  assuming a concrete type.
- No new dependency. No custom time type. The representation matches Node's
  native bigint stat and `Temporal.Instant#epochNanoseconds`.
- The public read precision (nanosecond) exceeds the write precision
  (millisecond) on Node, because Node provides no nanosecond set API.
- A BigInt is detected with `(identical? js/BigInt (.-constructor x))`, portable
  across ClojureScript and squint. `(js* "typeof ...")` was rejected because
  squint cannot resolve `js*` when the form expands inside a test macro.
