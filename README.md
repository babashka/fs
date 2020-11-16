# babashka.fs

File system utilities for babashka. This library isn't part of babashka itself
yet, but will be. Also can be used from the JVM.

## Why

Babashka is a scripting utility. It's convenient to have cross platform file
system utilities available for scripting.

The main inspiration for this library is
[clj-commons/fs](https://github.com/clj-commons/fs) but some of its functions
can be optimized by leveraging `java.nio` and others might need revision. We do
not guarantee any compatibility with the `clj-commons` library.

## Status

Check `CHANGES.md` before upgrading as the API may still undergo some
changes. Contributions welcome.

## Usage

``` clojure
(require '[babashka.fs :as fs])
```

## Test

``` clojure
$ clojure -M:test
```

## License

Copyright © 2020 Michiel Borkent

Distributed under the EPL License, same as Clojure. See LICENSE.
