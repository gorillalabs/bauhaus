# Bauhaus Framework Example Application

This is an example application demonstrating the Bauhaus framework - a modular Clojure application framework built around Integrant for dependency injection and system lifecycle management.

## What is Bauhaus?

Bauhaus is a collection of reusable Clojure modules that provide common application infrastructure:

- **Setup modules**: logging, CLI parsing, shutdown hooks
- **Development tooling**: configuration management, build tools
- **Collection utilities**: enhanced collection operations

The framework encourages a modular, component-based architecture where applications are composed of small, focused modules.

## Project Structure

```
├── deps.edn                 # Project dependencies and aliases
├── src/                     # Application source code
│   └── example/
│       ├── app.clj          # Main application entry point
│       └── app/
│           ├── config.clj   # Configuration accessors
│           └── system.clj   # Integrant system configuration
├── dev/                     # Development-only code
│   ├── user.clj            # Dev REPL utilities
│   └── dev/
│       └── system.clj      # Development system configuration
├── resources/
│   └── config.edn          # Production configuration
└── dev-resources/
    └── dev-config.edn      # Development configuration overrides
```

## Quick Start

### Prerequisites

- Java 21+
- Clojure CLI tools

### Running the Application

1. **Start the application:**

The application is usually run in production mode as an uberjar.

To build that uberjar, use the following command:
```bash
clj -T:build uber
```

which will create a JAR file in the `target/` directory.

Run the application with:

```bash
   java -jar target/app-2yxdAjuN6gaFYoehTEO687fcXGR-main-4a8a304-1.0.0+12-dirty-standalone.jar -c resources/config.edn
```

(you will have to adjust the JAR name based on the actual build output. The jar name is logged during the build process.)

The application will start and log output to `../example.logs/app.log`:

```
cat ../example.logs/app.log
```

2. **Run with development profile:**
   ```bash
   clj -M:dev
   # Then in the REPL:
   user=> (go)
   ```

### Development Workflow

The project uses Integrant for system lifecycle management with a development-friendly REPL workflow:

1. **Start development environment:**
   ```bash
   clj -M:dev
   ```

2. **In the REPL, start the system:**
   ```clojure
   user=> (go)              ; Start the system
   user=> (reset)           ; Reload code and restart system
   user=> (halt)            ; Stop the system
   ```

3. **The development system provides:**
   - Hot code reloading
   - Enhanced logging
   - Environment variable interpolation in config
   - German language greeting (vs English in production)

## Available Aliases

| Alias | Purpose |
|-------|---------|
| `:start` | Run the production application |
| `:dev` | Start development environment with REPL |
| `:test` | Run tests |
| `:build` | Build application JAR |
| `:nrepl` | Start nREPL server on port 7880 |
| `:container-nrepl` | Start nREPL server bound to all interfaces |

## Configuration

The application uses [Aero](https://github.com/juxt/aero) for configuration management:

- **Production config**: `resources/config.edn`
- **Development overrides**: `dev-resources/dev-config.edn`

Development configuration supports environment variable interpolation:
```edn
{:who #envf ["who:%s, where:%s" USER PWD]}
```

## Bauhaus Modules

This application demonstrates several Bauhaus modules:

### Setup Modules
- **logging**: Configures Timbre logging with file output
- **cli**: Provides command-line argument parsing
- **shutdown**: Manages graceful application shutdown

### Development Tooling
- **config**: Configuration management utilities
- **build**: Build and deployment tools

### Collections
- **collection**: Enhanced collection operations (e.g., `deep-merge`)

## System Architecture

The application uses Integrant for dependency injection and system lifecycle:

### Production System
```clojure
{:example/hello-en {:who "World"}}
```

### Development System
```clojure
{:example/hello-de {:who "user:root, where:/path"}}
```

Key differences:
- Development uses "German" component `:example/hello-de` vs English `:example/hello-en` in production. (Not the best example, do not conflate with I18N. But it produces an output.)
- Development provides additional debugging tools

## Adding New Components

1. **Define the component in `system.clj`:**
   ```clojure
   (defmethod ig/init-key :my-app/new-component [_ config]
     ;; Initialize your component
     )
   ```

2. **Add to system configuration:**
   ```clojure
   (defn system-config [config]
     {:my-app/new-component {:dependency-key value}})
   ```

3. **For development overrides, modify `dev/system.clj`**

## Building and Deployment

```bash
# Build JAR
clj -T:build uber
```

## Logging

Logs are written to `../example.logs/app.log` relative to the working directory.

Development logging outputs to console with debug level enabled.

## REPL Development

For nREPL development:
```bash
clj -M:nrepl  # Local development
clj -M:container-nrepl  # Container/remote development
```

## Next Steps

- Review the Integrant documentation for system lifecycle patterns
- Explore the Bauhaus modules in `../../modules/`
- Add your own components following the established patterns
- Configure logging, CLI options, and shutdown hooks as needed
