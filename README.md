# babashka.fs

[![Clojars Project](https://img.shields.io/clojars/v/babashka/fs.svg)](https://clojars.org/babashka/fs) [![ci](https://github.com/babashka/fs/actions/workflows/ci.yml/badge.svg)](https://github.com/babashka/fs/actions/workflows/ci.yml)
[![bb built-in](https://raw.githubusercontent.com/babashka/babashka/master/logo/built-in-badge.svg)](https://book.babashka.org#badges)

File system utilities. This library can used from:
- [Clojure on the JVM](https://www.clojure.org/guides/install_clojure) - we support Clojure 1.10.3 and above on Java 11 and above.
- [babashka](https://github.com/babashka/babashka) - it has been a built-in library since babashka v0.2.9.

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

### File Systems & OSes
Behaviour can vary on different file systems and OSes.
If you uncover some interesting nuance, please let us know. 

### Empty String Paths
The underlying JDK file APIs (and, by extension, babashka.fs) typically consider an empty-string path `""` to be the current working directory. This means that `(fs/list-dir "")` is functionally equivalent to `(fs/list-dir ".")`.

<!-- note: linked from docstring -->
### creation-time
Depending on which OS and JDK version you are running, `creation-time` might return unexpected results.
As of this writing, our testing has revealed: 
- Windows - returns creation time as expected
- macOS 
  - after Java 17 returns creation time as expected
  - otherwise returns modified time
- Linux 
  - before Java 17 returns modified time
  - otherwise returns creation time

See [JDK-8316304](https://bugs.openjdk.org/browse/JDK-8316304).

<!-- note: linked from docstring -->
### set-creation-time
Depending on which OS and JDK version you are running, `set-creation-time` might not do what you would expect. 
As of this writing, our testing has revealed:
- Windows - sets creation time as expected
- macOS
  - after Java 17 sets creation time as expected
  - otherwise has no effect
- Linux - seems to have no effect

See [JDK-8151430](https://bugs.openjdk.org/browse/JDK-8151430)

<!-- note: linked from docstring -->
### :nofollow-links
Many babashka.fs functions accept the `:nofollow-links` option.
These functions will follow symbolic links unless you pass in `{:nofollow-links true}`.

<!-- note: linked from docstring -->
### :follow-links
Some babashka.fs functions accept the `:follow-links` option.
These functions will _not_ follow symbolic links unless you pass in `{:follow-links true}`.

## Test & Dev

To run all tests
```
$ bb test
```

You can also use [cognitect test-runner](https://github.com/cognitect-labs/test-runner?tab=readme-ov-file#invoke-with-clojure--m-clojuremain) options, for example, to run a single test:
```
bb test --var babashka.fs-test/walk-test
```

> [!NOTE]
> To allow us to contrive isolated file system scenarios, tests are always run from the scratch current working directory `./target/test-cwd`.

To fire up a REPL when working on these tests, run:

```
bb dev
```

> [!TIP]
> The `.nrepl-port` file will be generated under `./target/test-cwd/`, so you'll have to type the REPL port in manually when connecting.

### API Docs
This project generates API docs with quickdoc, to regenerate `API.md`:
```
shell quickdoc
```

## License

Copyright © 2020-2025 Michiel Borkent

Distributed under the EPL License, same as Clojure. See LICENSE.
