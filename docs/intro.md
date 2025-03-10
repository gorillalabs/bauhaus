# Intro to Bauhaus

### First steps: Running the app

To run the example app, use the following command:

```bash
cd applications/example
clj -J-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory -M -m example.app -c resources/config.edn
```

This will start the example app as defined by the Integrant system map in [`example.app.system` namespace](../applications/example/src/example/app/system.clj)
with the configuration from `resources/config.edn` (thanks to functionality provided by
the module [setup/CLI](../modules/setup/CLI/)).

The sample app will log to a file `app.log` in a sibling directory of example named `example.logs`.

```bash
$ cat ../example.logs/app.log                                                                                                                                           system
{"ns":"example.app.system","file":"example/app/system.clj","msg":"Hello","hostname":"<hostname>","level":"error","line":7,"thread":"main","timestamp":"2025-03-10T15:49:27Z","to":"World"}
{"msg":"Shutting down system...","timestamp":"2025-03-10T15:49:27Z","level":"warn","thread":"Thread-1","file":"example/app.clj","line":32,"ns":"example.app","hostname":"<hostname>"}
{"msg":"System shut down.","timestamp":"2025-03-10T15:49:27Z","level":"warn","thread":"Thread-1","file":"example/app.clj","line":34,"ns":"example.app","hostname":"<hostname>"}
{"msg":"Shutting down clojure agents.","timestamp":"2025-03-10T15:49:27Z","level":"warn","thread":"Thread-1","file":"gorillalabs/bauhaus/setup/shutdown.clj","line":123,"ns":"gorillalabs.bauhaus.setup.shutdown","hostname":"<hostname>"}
```

Logging is initialized using the [../modules/setup/logging](../modules/setup/logging) module, so you can just use
timbre for logging and configuring the logging, no matter what any of the libraries you require decided to use. The most
common Java logging frameworks are supported via SLF4J.
For clojure.tools.logging support, you need to setup the JVM properly `-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory` (see [deps.edn](../applications/example/deps.edn) `dev` alias `:jvm-opts`). Above, we were
using the `-J` option to pass JVM options to the JVM (see [Clojure CLI Reference](https://clojure.org/reference/clojure_cli#opt_j)).

Logging is setup in [example.app/-main](../applications/example/src/example/app.clj) using

```clojure
(require '[[gorillalabs.bauhaus.setup.logging :as setup.logging]])
(setup.logging/init-logging (setup.logging/log-config "../example.logs/app.log"))
```

As you can see from the logging output, Bauhaus cares for a clean shutdown of the system (using module [setup-shutdown](../modules/setup/shutdown/)), and logs this to the log file.
This is realized in the `example.app/-main` function using

```clojure
(require '[gorillalabs.bauhaus.setup.shutdown :as setup.shutdown])
(setup.shutdown/add-hook! :clojure.core/shutdown-agents setup.shutdown/agents)
...
(setup.shutdown/add-hook! :app/shutdown-system shutdown)
(setup.shutdown/hook-order! [:app/shutdown-system :clojure.core/shutdown-agents])      
```

The example app is setup to open a Socket REPL if a repl port is configured (see `example.app/-main`).
Here, this is rather pointless (as the app will shutdown immediately after starting), but it is a good example to show how to setup a Socket REPL:

```clojure
(when (config/repl-port config)
        (let [socket (clojure.core.server/start-server {:port   (config/repl-port config)
                                                        :name   "socket-repl"
                                                        :accept 'clojure.core.server/repl})]
          (log/info "Started REPL socket server" {:socket socket})))
```

## Developing the example app in the REPL

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
- read the (developer) configuration from `dev-resources/config.edn` (utilizing the [Aero](https://github.com/juxt/aero) library for configuration)
- setup the Integrant system map (merging default configuration and development configuration)
- start the system

You will see the log output greeting with a message like this:

```text
user=> (go)
2025-03-10T14:29:57.285Z <hostname> DEBUG [dev.system:15] - Read dev config
2025-03-10T14:29:57.316Z <hostname> DEBUG [dev.system:15] - Read dev config
2025-03-10T14:29:57.320Z <hostname> INFO [dev.system:12] - Hallo {:an "christianbetz"}
:initiated
```

To reload the system (see [Integrant REPL](https://github.com/weavejester/integrant-repl)), just run `(reset)` in the user namespace. This will reload your develoment configuration.

Now: Feel free to hack away!

## A sample http service

...

