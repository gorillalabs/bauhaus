# Intro to Bauhaus

## First steps

Fire up a REPL for the [example application](../applications/example) to follow along the Bauhaus introduction, using
`dev` alias. Use your favorite toolset for this (mine is [Cursive](https://cursive-ide.com/)), or just use the CLI:

```bash
cd applications/example
clj -A:dev
```

Bauhaus projects (as the example project) are Integrant-based. The example project has extensive
dev support (see the [`dev`](dev/) and [`dev-resources`](dev-resources/) folders): Dev integrates the
Integrant dev tooling into the user namespace, so from the `user` namespace you can just run

```clojure
(go)
```

to startup the application.

This will
- read the (developer) configuration from `dev-resources/config.edn`
- setup the Integrant system map
- start the system

You will see the log output greeting with a message like this:

```text
user=> (go)
2025-03-10T14:29:57.285Z Mac.fritz.box DEBUG [dev.system:15] - Read dev config
2025-03-10T14:29:57.316Z Mac.fritz.box DEBUG [dev.system:15] - Read dev config
2025-03-10T14:29:57.320Z Mac.fritz.box INFO [dev.system:12] - Hallo {:an "christianbetz"}
:initiated
```

Logging is initialized using the [../modules/setup/logging](../modules/setup/logging) module, so you can just use
timbre for logging and configuring the logging, no matter what any of the libraries you require decided to use. The most
common Java logging frameworks are supported via SLF4J.
For clojure.tools.logging support, you need to setup the JVM properly `-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory` (see [deps.edn](../applications/example/deps.edn) `dev` alias `:jvm-opts`).

To reload the system (see [Integrant REPL](https://github.com/weavejester/integrant-repl)), just run `(reset)` in the user namespace. This will reload your develoment configuration.

## A sample http service

...

