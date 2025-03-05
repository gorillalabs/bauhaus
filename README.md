# Bauhaus

Bauhaus is an opinionated set of foundational modules to optimize DevOps experience with Clojure-based services.

## Overall design

Bauhaus is a set of modules that are designed to be used together.
The modules are designed to be used with [Integrant](https://github.com/weavejester/integrant),
but can be used without it.

It is the distillation of the experience of running Clojure services in production in several different organizations.

## Usage
There is an example application showing usage in [applications/example](applications/example).

And while we keep bauhaus in a monorepo, this does not mean that you have to follow our path here.
You can use the modules independently, even within your git-based deps.edn dependencies.

```clojure
{:deps {...
        org.gorillalabs.bauhaus/setup-logging {:git/url   "git@github.com:gorillalabs/bauhaus.git"
                                               :git/sha   "AE...<commit sha>" ;; see https://github.com/gorillalabs/bauhaus/commits/main/
                                               :deps/root "modules/setup/logging"}
        ...
        }}
```


## Modules

### Setup

| Module | Path | Description |
|--------|------|-------------|
| [Logging](modules/setup/logging/README.md) | [modules/setup/logging](modules/setup/logging) | setup proper logging infrastructure, fighting the JVM logging chaos. |
| [Shutdown](modules/setup/shutdown/README.md) | [modules/setup/shutdown](modules/setup/shutdown) | provide ordered shutdown hooks as proposed in [Killing me softly: Graceful shutdowns in Clojure](https://medium.com/helpshift-engineering/achieving-graceful-restarts-of-clojure-services-b3a3b9c1d60d) |
| [CLI](modules/setup/cli/README.md) | [modules/setup/cli](modules/setup/cli) | setup a CLI for your application. |


### Dev-Tooling

| Module                                         | Path | Description                                                |
|------------------------------------------------|------|------------------------------------------------------------|
| [Config](modules/dev-tooling/config/README.md) | [modules/dev-tooling/config](modules/dev-tooling/config) | Handle development config and ease Integrant REPL integration. |
| [Build](modules/dev-tooling/build/README.md)   | [modules/dev-tooling/build](modules/dev-tooling/build) | Support building your app.                                 |


### Clojure Contrib

| Module                                        | Path | Description                                                |
|-----------------------------------------------|------|------------------------------------------------------------|
| [Collection](modules/clojure-contrib/collection/README.md) | [modules/clojure-contrib/collection](modules/clojure-contrib/collection) | Collection utilities. |

