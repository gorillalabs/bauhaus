# Clojure Contrib / Collection

A small collection of utilities for working with collections.


## Install

Add this project to your deps.edn. In the Bauhaus project itself, this is possible from the local repository structure using

```clojure
{:deps {...
        gorillalabs.bauhaus.clojure-contrib/collection {:local/root "../../modules/clojure-contrib/collection"}
        ...
        }}
```

or using git coordinates like this, but with the latest :git/sha

```clojure
{...
 gorillalabs.bauhaus.clojure-contrib/collection {:git/url   "git@github.com:gorillalabs/bauhaus.git"
                                                 :git/sha   "AE...<commit sha>"
                                                 :deps/root "modules/clojure-contrib/collection"}
 ...}
```

## Usage

Require `gorillalabs.collections` namespace.