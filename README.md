# babashka.fs

File system utilities for babashka. This library isn't part of babashka itself
yet, but will be. Also can be used from the JVM.

## Status

In the hammock. Very alpha. Not production ready.

## Why

Babashka is a scripting utility. It's convenient to have cross platform file
system utilities available for scripting.

The main inspiration for this library is
[clj-commons/fs](https://github.com/clj-commons/fs) but some of its functions
can be optimized by leveraging `java.nio` and others might need revision. We do
not guarantee any compatibility with the `clj-commons` library. We might give
`Path` a first class treatment instead of casting everything back to
`java.io.File`. As such we might rename this library to `babashka.nio` or
`babashka.path`.

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

## License

Copyright © 2020 Michiel Borkent

Distributed under the EPL License, same as Clojure. See LICENSE.
