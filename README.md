# babashka.fs

[![Clojars Project](https://img.shields.io/clojars/v/babashka/fs.svg)](https://clojars.org/babashka/fs) [![ci](https://github.com/babashka/fs/actions/workflows/ci.yml/badge.svg)](https://github.com/babashka/fs/actions/workflows/ci.yml)
[![bb built-in](https://raw.githubusercontent.com/babashka/babashka/master/logo/built-in-badge.svg)](https://book.babashka.org#badges)

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

See [API.md](API.md).

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

## Notes 

The underlying JDK file APIs (and, by extension, babashka.fs) typically consider an empty-string path `""` to be the current working directory. This means that `(fs/list-dir "")` is functionally equivalent to `(fs/list-dir ".")`.

## Test

``` clojure
$ bb test-all
```

## License

Copyright © 2020-2025 Michiel Borkent

Distributed under the EPL License, same as Clojure. See LICENSE.
