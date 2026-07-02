# 3. The npm package exports both camelCase wrappers and munged names

Status: Accepted

## Context

`index.mjs` wraps every function in a camelCase export whose option maps accept
camelCase (or dashed) keys - the JS-idiomatic surface. The squint-compiled
module underneath exports munged names (`copy_tree`, `exists_QMARK_`), which is
exactly what squint emits for `(fs/copy-tree ...)` and `(fs/exists? ...)` when a
squint user requires the npm package as a JS library:
`(:require ["@babashka/fs" :as fs])`.

Without re-exporting the munged names, squint users cannot consume the npm
package at all (it ships only compiled JS, no `.cljc` source) short of a git
dep.

## Decision

`index.mjs` re-exports the raw module (`export * from './lib/babashka/fs.mjs'`)
in addition to the named camelCase wrappers.

- ESM resolves name collisions in our favor: explicit named exports shadow
  `export *`, so overlapping names (`cwd`, `path`, ...) resolve to the
  kebabizing wrapper.
- Option maps line up per audience: squint compiles `{:replace-existing true}`
  to dashed string keys, which the raw functions read natively; JS users use the
  camelCase wrappers, which translate camelCase keys.
- The squint-interop surface is locked by a smoke test (munged name + dashed
  option keys) in `test-js/smoke.test.mjs`.

## Consequences

- squint users get the full function API from npm. Only the `with-temp-dir`
  macro needs the source (or the `withTempDir` JS function).
- Known footgun: a JS user calling a munged function directly
  (`fs.copy_tree(src, dst, {replaceExisting: true})`) gets silent
  option-ignoring, since raw functions do not translate keys. The README
  documents camelCase as the JS surface and munged names as squint interop.
- Two names per function is permanent API surface; removing either later is
  breaking.
