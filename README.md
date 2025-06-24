# Bauhaus

Bauhaus is an [opinionated](docs/design-choices.md) set of foundational modules to optimize DevOps experience with Clojure-based services.

## Overall design

Bauhaus is a set of modules that are designed to be used together.
The modules are designed to be used with [Integrant](https://github.com/weavejester/integrant),
but can be used without it.

It is the distillation of the experience of running Clojure services in production in several different organizations.

## Usage
There is an example application showing usage in [applications/example](applications/example) and an example http
service showing more elaborate usage in [application/example-http-service](applications/example-http-service).

See [Documentation: Intro](docs/intro.md) for a more detailed introduction utilizing the two example applications.

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

## Nix Development Environment

This project includes a [Nix flake](https://wiki.nixos.org/wiki/Flakes) to provide a reproducible development environment.

### Prerequisites

You must have Nix installed with flake support enabled. You can find installation instructions on the [official NixOS website](https://nixos.org/download.html).

### Activating the Environment

Navigate to the project's root directory and run the following command:

```sh
nix develop
```

This will download all the required dependencies and drop you into a `nushell` with the following tools available:

  * `jdk23`
  * `clojure`
  * `git`

Upon activation, it will confirm the Java and Clojure versions available in the shell.

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

