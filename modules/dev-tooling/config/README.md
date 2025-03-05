# Dev-tooling / Config

Ease config handling during developing.

## Install

Add this project to your deps.edn. Currently, this is possible from the local repository structure using

```clojure
:dev {:extra-deps {org.gorillalabs.bauhaus.dev-tooling/config
                     {:local/root "../../modules/dev-tooling/config"}}
```

## Usage

Require `[gorillalabs.bauhaus.dev.config :as dev.config]` and use
`dev.config/config` to read config files `config.edn`
and `dev-config.edn` using [aero](https://github.com/juxt/aero), deep-merging both with left-to-right override.


dev-tooling comes with [Integrant REPL](https://github.com/weavejester/integrant-repl) support.
You can easily configure your configuration like this:

```clojure
(integrant.repl/set-prep!
  (fn []
    (repl-config (dev.config/config))
    ))
```
