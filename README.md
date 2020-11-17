# babashka.fs

File system utilities for babashka. This library isn't part of babashka itself
yet, but will be. Also can be used from the JVM.

## Status

In the hammock. Very alpha. Not production ready.

## Why

Babashka is a scripting utility. It's convenient to have cross platform file
system utilities available for scripting. The namespace `clojure.java.io`
already offers a bunch of useful features, but isn't up to date with `java.nio`.

The main inspiration for this library is
[clj-commons/fs](https://github.com/clj-commons/fs) but some of its functions
can be optimized by leveraging `java.nio` and others might need revision. We do
not guarantee any compatibility with the `clj-commons` library. We might give
`Path` a first class treatment instead of casting everything back to
`java.io.File`.

## API docs

API docs are available at [babashka.org/fs](https://babashka.org/fs/babashka.fs.html)

## Usage

``` clojure
(require '[babashka.fs :as fs])
```

### glob

``` clojure
(map str (fs/glob "." "**/*.clj"))
```

Output:

``` clojure
("test/babashka/fs_test.clj" "src/babashka/fs.clj")
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

Copyright © 2020 Michiel Borkent

Distributed under the EPL License, same as Clojure. See LICENSE.
