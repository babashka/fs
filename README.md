# babashka.fs

File system utilities for babashka. This library isn't part of babashka itself
yet, but will be. Also can be used from the JVM.

## Status

Working towards an initial release. The API might still change. Awaiting user feedback.

## Why

Babashka is a scripting utility. It's convenient to have cross platform file
system utilities available for scripting. The namespace `clojure.java.io`
already offers a bunch of useful features, but isn't up to date with `java.nio`.

The main inspiration for this library is
[clj-commons/fs](https://github.com/clj-commons/fs) but some of its functions
can be optimized by leveraging `java.nio` and others might need revision. We do
not guarantee any compatibility with the `clj-commons` library.

## API docs

API docs are available at [babashka.org/fs](https://babashka.org/fs).  Most
functions take a string, `java.io.File` or `java.nio.file.Path` as input and
return a `java.nio.file.Path`. Coercion into a `File` or `Path` can be done
using `fs/file` and `fs/path`.

## Usage

Until this library is included in babashka, you can use this library as a git lib:

``` clojure
#!/usr/bin/env bb

(require '[babashka.deps :as deps])
(deps/add-deps '{:deps {babashka/fs
                        {:git/url "https://github.com/babashka/fs"
                         :sha "b008b2d6ea64e49bf76066b7f057ae4638534e35"}}})
```

or use any later SHA.

On the JVM, add it to `deps.edn`.

``` clojure
(require '[babashka.fs :as fs])
```

### glob

The `glob` function takes a root path and a pattern. The pattern is interpreted
as documented
[here](https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)).

``` clojure
(map str (fs/glob "." "**{.clj,cljc}"))
```

Output:

``` clojure
("project.clj" "test/babashka/fs_test.clj" "src/babashka/fs.cljc")
```

## Test

``` clojure
$ clojure -M:test
```

## Prior art

- [nate/fs](https://github.com/nate/fs/blob/master/src/nate/fs.clj)
- [hara.io](https://github.com/zcaudate/hara/blob/master/src/hara/io/file.clj)
- [clj-commons/fs](https://github.com/clj-commons/fs)
- [Python pathlib](https://docs.python.org/3/library/pathlib.html)

## Codox

Static files including compiled JS are hosted on Github. This is set up like
described
[here](https://medium.com/linagora-engineering/deploying-your-js-app-to-github-pages-the-easy-way-or-not-1ef8c48424b7):

All the commands below assume that you already have a git project initialized and that you are in its root folder.

```
# Create an orphan branch named gh-pages
git checkout --orphan gh-pages
# Remove all files from staging
git rm -rf .
# Create an empty commit so that you will be able to push on the branch next
git commit --allow-empty -m "Init empty branch"
# Push the branch
git push origin gh-pages
```

Now that the branch is created and pushed to origin, let’s configure the worktree correctly:

```
# Come back to master
git checkout master
# Add gh-pages to .gitignore
echo "gh-pages/" >> .gitignore
git worktree add gh-pages gh-pages
```

After cloning this repo to a new dir:

```
git fetch origin gh-pages
git worktree add gh-pages gh-pages
```

To deploy to Github Pages:

```
script/release
```

## License

Copyright © 2020-2021 Michiel Borkent

Distributed under the EPL License, same as Clojure. See LICENSE.
