# Changelog

For a list of breaking changes, check [here](#breaking-changes).

## Unreleased

- [#65](https://github.com/babashka/fs/issues/65): Explicitly support `:win-exts` option on `which` function ([@lread](https://github.com/lread))

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
