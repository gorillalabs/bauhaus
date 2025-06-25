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

## Monorepo Development (Recommended)

At least for starting, going with the monorepo approach is the way to go.
It becomes more complicated if you do not have 10s of applications, but 100s or 1000s of deployment artefacts,
but for moving fast within a scoped context, a monorepo approach is favourable.

Clone the entire repository (or your own fork) for development:

```bash
git clone https://github.com/gorillalabs/bauhaus.git
```

### Creating New Projects

####  Option 1: Quick Start by Copying an Example

```bash
# Copy the basic example
cp -r applications/example applications/my-new-project
cd applications/my-new-project

# Update project namespace in src/example/ → src/my-project/
# Update deps.edn and configuration as needed
```

#### Option 2: Start Fresh

1. **Create project structure:**
```bash
mkdir -p applications/my-project && cd applications/my-project
mkdir -p {src,resources,dev,dev-resources,test}
```

2. **Create deps.edn:**
```clojure
{:paths ["src" "resources"]
 
 :deps {org.clojure/clojure {:mvn/version "1.12.0"}
        integrant/integrant {:mvn/version "0.13.1"}
        aero/aero {:mvn/version "1.1.6"}
        
        ;; Add Bauhaus modules as needed
        org.gorillalabs.bauhaus/setup-logging 
        {:git/url "https://github.com/gorillalabs/bauhaus.git"
         :git/sha "LATEST_SHA"
         :deps/root "modules/setup/logging"}}
         
 :aliases
 {:dev {:extra-paths ["dev" "dev-resources"]
        :extra-deps {integrant/repl {:mvn/version "0.4.0"}
                     org.gorillalabs.bauhaus/dev-config 
                     {:git/url "https://github.com/gorillalabs/bauhaus.git"
                      :git/sha "LATEST_SHA"
                      :deps/root "modules/dev-tooling/config"}}}
                      
  :build {:deps {org.gorillalabs.bauhaus/build 
                 {:git/url "https://github.com/gorillalabs/bauhaus.git"
                  :git/sha "LATEST_SHA"  
                  :deps/root "modules/dev-tooling/build"}}
          :paths ["build"]
          :ns-default build}}}
```

3. **Set up development workflow:**
```clojure
;; dev/user.clj
(ns user
  (:require [integrant.repl :as ig-repl]
            [gorillalabs.bauhaus.dev-tooling.config :as dev-config]))

(ig-repl/set-prep! (constantly (dev-config/ig-config "dev-resources/dev-config.edn")))

(def go ig-repl/go)
(def halt ig-repl/halt)  
(def reset ig-repl/reset)
```

4. **Create your system configuration:**
```clojure
;; src/my_project/system.clj
(ns my-project.system
  (:require [integrant.core :as ig]))

(defn system-config [config]
  {:my-project/core {:message (:message config)}})

(defmethod ig/init-key :my-project/core [_ config]
  (println "Starting with:" (:message config))
  config)

(defmethod ig/halt-key! :my-project/core [_ component]
  (println "Stopping"))
```

### Build System Integration

To use Bauhaus build tools in your project:

1. **Add build configuration:**
```clojure
;; build/build.clj
(ns build
  (:require [gorillalabs.bauhaus.build.version :as version]))

(defn uber [opts]
  (let [version-string (version/application-build-string)]
    (println "Building version:" version-string)
    ;; Your build logic here
    ))
```

2. **Use git tags for versioning:**
```bash
git tag v1.0.0  # Bauhaus build tools will use this
clj -T:build uber
```

### Integration Patterns

**Recommended module combinations:**

- **Basic CLI app**: setup/logging + setup/cli + setup/shutdown
- **Web service**: + http-server
- **Development**: + dev-tooling/config + dev-tooling/build

## Individual Module Dependencies

You can, of course, reference specific modules even if you do not use all of Bauhaus or the monorepo structure:

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
| [Logging](modules/setup/logging/README.md) | [modules/setup/logging](modules/setup/logging) | Setup proper logging infrastructure, fighting the JVM logging chaos. |
| [Shutdown](modules/setup/shutdown/README.md) | [modules/setup/shutdown](modules/setup/shutdown) | Provide ordered shutdown hooks as proposed in [Killing me softly: Graceful shutdowns in Clojure](https://medium.com/helpshift-engineering/achieving-graceful-restarts-of-clojure-services-b3a3b9c1d60d) |
| [CLI](modules/setup/cli/README.md) | [modules/setup/cli](modules/setup/cli) | Setup a CLI for your application. |

### Dev-Tooling
*Enhanced developer experience*

| Module | Path | Description |
|--------|------|-------------|
| [Config](modules/dev-tooling/config/README.md) | [modules/dev-tooling/config](modules/dev-tooling/config) | Handle development config and ease Integrant REPL integration. |
| [Build](modules/dev-tooling/build/README.md) | [modules/dev-tooling/build](modules/dev-tooling/build) | Support building your app with version management. |

### Clojure Contrib
*Enhanced standard library*

| Module | Path | Description |
|--------|------|-------------|
| [Collection](modules/clojure-contrib/collection/README.md) | [modules/clojure-contrib/collection](modules/clojure-contrib/collection) | Collection utilities including deep-merge and more. |


## Learning Path

### 🚀 New to Bauhaus? Start here:

1. **📖 Read the Philosophy**: [Design Choices](docs/design-choices.md) - Understand the "why"
2. **⚡ Quick Win**: Run the [Basic Example](applications/example) in 2 minutes
   ```bash
   cd applications/example
   clj -M:dev
   # In REPL: (go)
   ```
3. **🌐 Real Application**: Explore the [HTTP Service Example](applications/example-http-service)
4. **🔧 Build Your Own**: Follow [Creating New Projects](#creating-new-projects)
5. **📚 Deep Dive**: [Comprehensive Introduction](docs/intro.md)

### 🎯 By Use Case:

- **Building CLI tools**: Start with setup/logging + setup/cli
- **Web applications**: Try example-http-service first
- **Learning Integrant**: Both examples show different patterns
- **Production deployment**: Check build tooling and shutdown hooks

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

## Troubleshooting

### Common Issues

**"Module not found" errors:**
- Verify the `:git/sha` points to a valid commit
- Check that `:deps/root` path matches the actual module location
- Ensure module dependencies are compatible

**REPL development issues:**
- Run `(reset)` if code changes aren't reflecting
- Check that `dev-resources/dev-config.edn` exists
- Verify Integrant system configuration is valid

**Build problems:**
- Check that build namespace is properly configured in `:build` alias

**Getting Help:**
- Check individual module READMEs for specific guidance
- Review the [Introduction Guide](docs/intro.md) for detailed examples
- Look at working examples in `applications/` directory

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
