# Dev-tooling / Build

Support building your app.

## Install

Add this project to your deps.edn (typcially in a `:build` alias).
Currently, this is possible from the local repository structure using

```clojure
:build     {:deps       {gorillalabs.bauhaus.dev-tooling/build {:local/root "../../modules/dev-tooling/build"}
            ...
            }
```

or from the git repository using

```clojure
{       ...
 gorillalabs.bauhaus.dev-tooling/build {:git/url   "git@github.com:gorillalabs/bauhaus.git"
                                        :git/sha   "AE...<commit sha>"
                                        :deps/root "modules/dev-tooling/build"}
 ...}
```

## Usage

Require `[gorillalabs.bauhaus.build.version :as version]` and use
`(version/application-build-string)` to construct a version string in Bauhaus style
from your git information. Use git tags `v1.2.3` to name a version.