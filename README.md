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

### File Systems & OSes & JDK Bugs
Behaviour can vary on different file systems and OSes.

The JDK file APIs have historically had some obscure bugs; we've described known issues that affect babashka fs below.
The JDK team has fixed most of these bugs, so we encourage you to use the latest stable JDK, if you can.

If you uncover some interesting new unexpected behaviour, please let us know.

### Windows & Links
You may have to enable the ability to create soft & hard links on Windows.

### Empty String Paths
The underlying JDK file APIs (and, by extension, babashka.fs) typically consider an empty-string path `""` to be the current working directory. This means that `(fs/list-dir "")` is functionally equivalent to `(fs/list-dir ".")`.

<!-- note: linked from doctrings -->
### umask
On Linux and macOS you can often optionally specify `:posix-file-permissions`.
For newly created files and directories, these permissions are affected by your configured `umask`.

For example, let's say your `umask` masks out the others-write permission:

```shell
$ umask -S
u=rwx,g=rwx,o=rx
```

Notice that others-write, even though specified on creation, is masked out by umask:

```clojure
(fs/create-file "afile" {:posix-file-permissions "rwxrwxrwx"})

;; umask affected resulting permissions of new files
(-> (fs/posix-file-permissions "afile") (fs/posix->str))
;; => "rwxrwxr-x"
```

This applies only to newly created files and directories.
You can explicitly set permissions on existing files and directories:

```clojure
(fs/create-dir "adir" {:posix-file-permissions "rwxrwxrwx"})

;; umask affected resulting permissions of new dirs
(-> (fs/posix-file-permissions "adir") (fs/posix->str))
;; => "rwxrwxr-x"

;; but you can explicitly override permissions on existing file & dirs like so:
(fs/set-posix-file-permissions "adir" "rwxrwxrwx")
(-> (fs/posix-file-permissions "adir") (fs/posix->str))
;; => "rwxrwxrwx"
```

This is the underlying behaviour the JDK file APIs, see [Setting Initial Permissions in JavaDocs](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/attribute/PosixFileAttributeView.html). 
Babashka fs, as a light wrapper, reflects this behaviour.

<!-- note: linked from docstring -->
### creation-time
Depending on which OS and JDK version you are running, `creation-time` might return unexpected results.
As of this writing, our testing has revealed: 

- Windows - returns creation time as expected
- macOS - returns creation time as expected
- Linux - returns modified time before JDK 17, otherwise returns creation time as expected

See [JDK-8316304](https://bugs.openjdk.org/browse/JDK-8316304).

<!-- note: linked from docstring -->
### set-creation-time
Depending on which OS and JDK version you are running, `set-creation-time` might not do what you would expect. 
As of this writing, our testing has revealed:
- Windows - sets creation time as expected
- macOS
  - after Java 17 sets creation time as expected, otherwise seems to have no effect
  - otherwise has no effect
- Linux - seems to have no effect

See [JDK-8151430](https://bugs.openjdk.org/browse/JDK-8151430)

<!-- note: linked from docstring -->
### :nofollow-links
Many babashka.fs functions accept the `:nofollow-links` option.
These functions will follow symbolic links unless you pass in `{:nofollow-links true}`.

On some JDK/OS combinations,  `set-attribute` and `set-last-modified-time` may throw when attempting to set on the link itself. 
Considering latest JDK 11+ LTS releases only, at the time of this writing, we have found this bug on JDK 11 for both macOS and Linux and JDK 21 for Linux. For example, on JDK11 on Linux and macOS:
```clojure
(spit "foo" "foo.txt")
(fs/create-sym-link "foo-link" "foo")
;; this works fine and sets last modified time on "foo":
(fs/set-last-modified-time "foo-link" (java.time.Instant/now))
;; when attempting to set time on link itself, throws on some JDK/OS combinations:
(fs/set-last-modified-time "foo-link" (java.time.Instant/now) {:nofollow-links true})
;; => Execution error (FileSystemException) 
;;    foo-link: Too many levels of symbolic links or unable to access attributes of symbolic link
```

On Windows, for JDK < 24, we have found that `canonicalize` will never follow symbolic links due to this JDK bug [JDK-8003887](https://bugs.openjdk.org/browse/JDK-8003887).

<!-- note: linked from docstring -->
### :follow-links
Some babashka.fs functions accept the `:follow-links` option.
These functions will _not_ follow symbolic links unless you pass in `{:follow-links true}`.

<!-- note: linked from docstring -->
### glob

On macOS, for JDK < 26, functions that match on a glob pattern will omit filenames with Unicode characters that include a variant-selector due to this JDK bug [JDK-8354490](https://bugs.openjdk.org/browse/JDK-8354490).

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

To fire up a REPL when working on babashka fs tests, you must run:

```
bb dev
```

### API Docs
This project generates API docs with quickdoc, to regenerate `API.md`:
```
shell quickdoc
```

## License

Copyright © 2020-2026 Michiel Borkent

Distributed under the EPL License, same as Clojure. See LICENSE.
