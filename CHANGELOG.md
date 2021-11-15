# Changelog

For a list of breaking changes, check [here](#breaking-changes).

## v0.1.1

- Allow raw pattern to be passed to fs/match [#32](https://github.com/babashka/fs/issues/32)
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
