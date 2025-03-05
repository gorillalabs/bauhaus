# Setup Shutdown

Module to provide ordered shutdown hooks as proposed
in [Killing me softly: Graceful shutdowns in Clojure](https://medium.com/helpshift-engineering/achieving-graceful-restarts-of-clojure-services-b3a3b9c1d60d)

Install
-------

Add this project to your deps.edn. Currently, this is possible from the local repository structure using

```clojure
{:deps {...
        org.gorillalabs.bauhaus/setup-shutdown             {:local/root "../../modules/setup/shutdown"}
        ...
        }}
```

or using git coordinates like this, but with the latest :git/sha

```clojure
{:deps {...
        org.gorillalabs.bauhaus/setup-shutdown {:git/url   "git@github.com:gorillalabs/bauhaus.git"
                                                :git/sha   "AE...<commit sha>"
                                                :deps/root "modules/setup/shutdown"}
        ...
        }}
```

## Usage

Require the `gorillalabs.bauhaus.setup.shutdown` namespace (here `:as` `setup.shutdown`)

You can add shutdown hooks to an ordered collection of hooks

```clojure
(setup.shutdown/add-hook! :clojure.core/shutdown-agents setup.shutdown/agents)
```

The `gorillalabs.bauhaus.setup.shutdown/agents` function is a helper function to shutdown the agents
using [`shutdown-agents`](https://clojuredocs.org/clojure.core/shutdown-agents) with a log message.

You can reorder the hooks later on

```clojure
(setup.shutdown/hook-order! [:clojure.core/shutdown-agents])
```
