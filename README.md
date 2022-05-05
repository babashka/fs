# babashka.fs

[![Clojars Project](https://img.shields.io/clojars/v/babashka/fs.svg)](https://clojars.org/babashka/fs)

File system utilities. This library can be used on the JVM and is also included
in [babashka](https://github.com/babashka/babashka) (>= 0.2.9).

## Why

Babashka is a scripting utility. It's convenient to have cross platform file
system utilities available for scripting. The namespace `clojure.java.io`
already offers a bunch of useful features, but it predates `java.nio`. The nio
package isn't that nice to use from Clojure and this library should help with
that.

The main inspirations for this library are
[clojure.java.io](https://clojure.github.io/clojure/clojure.java.io-api.html),
[clj-commons/fs](https://github.com/clj-commons/fs) and
[corasaurus-hex/fs](https://github.com/corasaurus-hex/fs/blob/master/src/corasaurus_hex/fs.clj).

## API docs

API docs are available at [babashka.org/fs/codox](https://babashka.org/fs/codox).
Most functions take a string, `java.io.File` or `java.nio.file.Path` as input and
return a `java.nio.file.Path`. Coercion into a `File` or `Path` can be done
using `fs/file` and `fs/path`.

## Usage

``` clojure
(require '[babashka.fs :as fs])
(fs/directory? ".") ;;=> true
```

## Examples

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

The function `exec-paths` returns all entries from `PATH` as `Path`s. To search
all these directories for an executable, e.g. `java`, you can combine it with
`list-dirs` which searches files directly in the directories using an (optional)
glob pattern:

``` clojure
(str (first (filter fs/executable? (fs/list-dirs (filter fs/exists? (fs/exec-paths)) "java"))))
"/Users/borkdude/.jenv/versions/11.0/bin/java"
```

For convenience, the above use case is also supported using the `which` function:

``` clojure
(str (fs/which "java"))
"/Users/borkdude/.jenv/versions/11.0/bin/java"
```

## Test

``` clojure
$ clojure -M:test
```

<!-- ## Codox -->

<!-- Static files including compiled JS are hosted on Github. This is set up like -->
<!-- described -->
<!-- [here](https://medium.com/linagora-engineering/deploying-your-js-app-to-github-pages-the-easy-way-or-not-1ef8c48424b7): -->

<!-- All the commands below assume that you already have a git project initialized and that you are in its root folder. -->

<!-- ``` -->
<!-- # Create an orphan branch named gh-pages -->
<!-- git checkout --orphan gh-pages -->
<!-- # Remove all files from staging -->
<!-- git rm -rf . -->
<!-- # Create an empty commit so that you will be able to push on the branch next -->
<!-- git commit --allow-empty -m "Init empty branch" -->
<!-- # Push the branch -->
<!-- git push origin gh-pages -->
<!-- ``` -->

<!-- Now that the branch is created and pushed to origin, let’s configure the worktree correctly: -->

<!-- ``` -->
<!-- # Come back to master -->
<!-- git checkout master -->
<!-- # Add gh-pages to .gitignore -->
<!-- echo "gh-pages/" >> .gitignore -->
<!-- git worktree add gh-pages gh-pages -->
<!-- ``` -->

<!-- After cloning this repo to a new dir: -->

<!-- ``` -->
<!-- git fetch origin gh-pages -->
<!-- git worktree add gh-pages gh-pages -->
<!-- ``` -->

<!-- To deploy to Github Pages: -->

<!-- ``` -->
<!-- script/release -->
<!-- ``` -->

## License

Copyright © 2020-2021 Michiel Borkent

Distributed under the EPL License, same as Clojure. See LICENSE.
