# Bauhaus

Bauhaus is an [opinionated](docs/design-choices.md) set of foundational modules to optimize DevOps experience with Clojure-based services.

## Overall design

Bauhaus is a set of modules that are designed to be used together.
The modules are designed to be used with [Integrant](https://github.com/weavejester/integrant),
but can be used without it.

It is the distillation of the experience of running Clojure services in production in several different organizations.

## Why "Bauhaus"?

Named after the influential design movement, Bauhaus emphasizes **functional design**, **modular composition**, and **practical utility**. Just as Bauhaus architecture focused on essential functionality without unnecessary ornamentation, this framework provides essential infrastructure without bloat.

*"Form follows function"* - applied to Clojure application architecture.

## Repository Structure

This is a **monorepo** containing multiple modules and example applications:

```
bauhaus/
├── modules/                    # Reusable Bauhaus modules
│   ├── setup/                 # Application setup utilities
│   │   ├── logging/           # Logging infrastructure
│   │   ├── shutdown/          # Graceful shutdown hooks
│   │   └── cli/               # Command-line interface
│   ├── dev-tooling/           # Development utilities
│   │   ├── config/            # Configuration management
│   │   └── build/             # Build and deployment
│   └── clojure-contrib/       # Clojure utility extensions
│       └── collection/        # Collection utilities
├── applications/              # Example applications
│   ├── example/              # Basic Bauhaus application
│   └── example-http-service/ # HTTP service example
└── docs/                     # Documentation
```

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

### Monorepo Development (Recommended)

Clone the entire repository for development:
```bash
git clone https://github.com/gorillalabs/bauhaus.git
cd bauhaus/applications/example
```

### Individual Module Dependencies

For production applications, reference specific modules:

```clojure
{:deps {;; Core dependencies
        org.clojure/clojure {:mvn/version "1.12.0"}
        integrant/integrant {:mvn/version "0.13.1"}
        
        ;; Bauhaus modules (pick what you need)
        org.gorillalabs.bauhaus/setup-logging 
        {:git/url "https://github.com/gorillalabs/bauhaus.git"
         :git/sha "COMMIT_SHA"  ; Pin to specific commit
         :deps/root "modules/setup/logging"}
         
        org.gorillalabs.bauhaus/setup-shutdown
        {:git/url "https://github.com/gorillalabs/bauhaus.git"
         :git/sha "COMMIT_SHA"
         :deps/root "modules/setup/shutdown"}}}
```

> **💡 Tip**: Check [latest commits](https://github.com/gorillalabs/bauhaus/commits/main/) for the most recent SHA

## Modules

### Setup
*Foundation for application infrastructure*

| Module | Path | Description |
|--------|------|-------------|
| [Logging](modules/setup/logging/README.md) | [modules/setup/logging](modules/setup/logging) | setup proper logging infrastructure, fighting the JVM logging chaos. |
| [Shutdown](modules/setup/shutdown/README.md) | [modules/setup/shutdown](modules/setup/shutdown) | provide ordered shutdown hooks as proposed in [Killing me softly: Graceful shutdowns in Clojure](https://medium.com/helpshift-engineering/achieving-graceful-restarts-of-clojure-services-b3a3b9c1d60d) |
| [CLI](modules/setup/cli/README.md) | [modules/setup/cli](modules/setup/cli) | setup a CLI for your application. |


### Dev-Tooling
*Enhanced developer experience*

| Module                                         | Path | Description                                                |
|------------------------------------------------|------|------------------------------------------------------------|
| [Config](modules/dev-tooling/config/README.md) | [modules/dev-tooling/config](modules/dev-tooling/config) | Handle development config and ease Integrant REPL integration. |
| [Build](modules/dev-tooling/build/README.md)   | [modules/dev-tooling/build](modules/dev-tooling/build) | Support building your app.                                 |


### Clojure Contrib
*Enhanced standard library*

| Module                                        | Path | Description                                                |
|-----------------------------------------------|------|------------------------------------------------------------|
| [Collection](modules/clojure-contrib/collection/README.md) | [modules/clojure-contrib/collection](modules/clojure-contrib/collection) | Collection utilities. |


## Learning Path

1. **🚀 Start Here**: [Introduction Guide](docs/intro.md) - Comprehensive walkthrough
2. **👀 See It Work**: [Basic Example](applications/example) - Simple application
3. **🌐 Real World**: [HTTP Service Example](applications/example-http-service) - Production patterns
4. **📚 Deep Dive**: Individual module documentation
5. **🔧 Customize**: [Design Choices](docs/design-choices.md) - Understanding the philosophy

## Development Environment

### Nix Development Environment

This project includes a [Nix flake](https://wiki.nixos.org/wiki/Flakes) to provide a reproducible development environment.

#### Prerequisites

You must have Nix installed with flake support enabled. You can find installation instructions on the [official NixOS website](https://nixos.org/download.html).

#### Activating the Environment

Navigate to the project's root directory and run the following command:

```sh
nix develop
```

This will download all the required dependencies and drop you into a `nushell` with the following tools available:

* `jdk23`
* `clojure`
* `git`

Upon activation, it will confirm the Java and Clojure versions available in the shell.

## Contributing

We welcome contributions! Please:

1. **Check existing issues** before creating new ones
2. **Fork the repository** for your own modules, applications and/or pull requests.
3. **Follow the established patterns** in existing modules
4. **Add tests** for new functionality
5. **Update documentation** as needed

## Migration from Other Frameworks

### From Mount
- Replace Mount states with Integrant components
- Use Bauhaus setup modules for logging and shutdown
- Leverage enhanced REPL workflow

### From Component
- Integrant provides similar lifecycle management
- Bauhaus adds production-ready operational modules
- Configuration management is enhanced with Aero

### From Plain Clojure
- Gradual adoption - start with one or two modules
- Enhanced REPL development experience
- Production operational capabilities
