# Changelog

For a list of breaking changes, check [here](#breaking-changes).

Babashka [fs](https://github.com/babashka/fs): file system utility library for Clojure

## v0.4.18 (2023-05-11)

- [#48](https://github.com/babashka/fs/issues/48): support input-stream in `fs/copy`
- [#91](https://github.com/babashka/fs/issues/91): add 1-arity to `xdg-*-home` to get subfolder of base directory ([@eval](https://github.com/eval))
- [#94](https://github.com/babashka/fs/issues/94): updates to `which`: add `:paths` `opt`, allow absolute paths for `program` ([@lread](https://github.com/lread))

## v0.3.17 (2023-02-28)

- [#67](https://github.com/babashka/fs/issues/67): add `:root` and `:path-fn` options to `fs/zip`

## v0.2.16 (2023-02-08)

- [#89](https://github.com/babashka/fs/issues/89): change default in `walk-file-tree` from throwing to continue-ing, which
  works better for `glob`, `match`. You can still throw by providing your own
  `visit-file-failed` function.

## v0.2.15

- [#85](https://github.com/babashka/fs/issues/85): add helpers to generate common XDG paths ([@eval](https://github.com/eval))

## v0.2.14 (2023-01-16)

- [#81](https://github.com/babashka/fs/issues/81): do not process directories in `strip-ext` and `split-ext`
- Correct documentation for match/glob functions return value [@thenonameguy](https://github.com/thenonameguy)
- Add `unixify`: returns path as string with Unix-style file separators (`/`) on Windows systems.

## v0.2.12 (2022-11-16)

- [#73](https://github.com/babashka/fs/issues/73): add [`write-bytes`](https://github.com/babashka/fs/blob/master/API.md#babashka.fs/write-bytes) and [`write-lines`](https://github.com/babashka/fs/blob/master/API.md#babashka.fs/write-lines)
- [#77](https://github.com/babashka/fs/issues/77): add [`update-file`](https://github.com/babashka/fs/blob/master/API.md#babashka.fs/update-file)

## v0.1.11

- [#65](https://github.com/babashka/fs/issues/65): Explicitly support `:win-exts` option on `which` function ([@lread](https://github.com/lread))
- [clj-kondo#1782](https://github.com/clj-kondo/clj-kondo/issues/1782): `exists?` should never throw on illegal input path.
- Remove `^:const` to not cache OS-specific constants, so AOT-ed code can be
  re-used in multiple OS-es.

## v0.1.6

- Add `cwd`

## v0.1.5

- Support glob patterns on Windows with forward slashes

- [#51](https://github.com/babashka/fs/issues/51): Update `which` function to more closely mimic unix `which`:
  - do not identify directories as matches
  - if the argument is a relative path (more than just a file/command name), then don't search path entries

## v0.1.4

- Add `windows?` predicate

## v0.1.3

- Compatibility improvements for `com.google.cloud/google-cloud-nio`: convert
  `URI` to `Path` directly without going through `URL`
- Add `create-temp-file`
- Add initial version of `zip`
- Add `read-attributes*` without conversion into map
- Run `create-dirs` for the `dest` directory in `copy-tree` to ensure it exists ([@duzunov](https://github.com/duzunov))
- Document `list-dir` `glob-or-accept` argument ([@holyjak](https://github.com/holyjak))

## v0.1.2

- Add `with-temp-dir` macro [#37](https://github.com/babashka/fs/issues/37) ([@hugoduncan](https://github.com/hugoduncan))
- Add `fs/home` and `fs/expand-home` [#12](https://github.com/babashka/fs/issues/12) [#13](https://github.com/babashka/fs/issues/13) ([@Kineolyan](https://github.com/Kineolyan))
- Improve `which` on Windows: take into account `.com`, `.exe`, `.bat.`, `.cmd`
  when searching for program

## v0.1.1

- Allow raw pattern to be passed to `fs/match` [#32](https://github.com/babashka/fs/issues/32)
- `unzip`: entry in dir can come before dir in zip entries [#35](https://github.com/babashka/fs/issues/35)

## v0.1.0

- Add `strip-ext` and change `split-ext` and `strip-ext` to return full paths [#29](https://github.com/babashka/fs/issues/29) ([@corasaurus-hex](https://github.com/corasaurus-hex))
- Add `unzip`

## v0.0.5

- Allow dir as dest in `move` [#25](https://github.com/babashka/fs/issues/25)

## v0.0.4

- `delete-tree` must not crash when tree doesn't exist (making it idempotent)
- Add `modified-since`
- Allow dir as dest in `copy` [#24](https://github.com/babashka/fs/issues/24)

## v0.0.3

- Create target dir in `copy-tree`

## v0.0.2

- Add `create-link` and `split-paths` ([@eamonnsullivan](https://github.com/eamonnsullivan))
- Add `split-ext` and `extension` ([@kiramclean](https://github.com/kiramclean))
- Add `regular-file?`([@tekacs](https://github.com/tekacs))
- Globbing is always recursive but should not be [#18](https://github.com/babashka/fs/issues/18)
- Fix globbing on Windows
- Fix Windows tests

## 0.0.1

Initial release.

## Breaking changes

None yet.
