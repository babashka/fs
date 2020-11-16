# babashka.io

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

## Usage

``` clojure
(require '[babashka.io :as bio])
```

### glob

``` clojure
(map str (bio/glob "." "**/*.clj"))
```

Output:

``` clojure
("test/babashka/io_test.clj" "src/babashka/io.clj")
```

## Test

``` clojure
$ clojure -M:test
```

## License

Copyright © 2020 Michiel Borkent

Distributed under the EPL License, same as Clojure. See LICENSE.
