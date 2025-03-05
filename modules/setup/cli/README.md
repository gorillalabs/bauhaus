# Setup CLI

Module to provide CLI helpers:

* CLI options parsing using [clojure.tools.cli](https://github.com/clojure/tools.cli).
* REPL-friendly `System/exit` wrapper 

## Install

Add this project to your deps.edn. In the Bauhaus project itself, this is possible from the local repository structure using

```clojure
{:deps {...
        org.gorillalabs.bauhaus/setup-cli {:local/root "../../modules/setup/cli"}
        ...
        }}
```

or using git coordinates like this, but with the latest :git/sha

```clojure
{       ...
 org.gorillalabs.bauhaus/setup-cli {:git/url   "git@github.com:gorillalabs/bauhaus.git"
                                        :git/sha   "AE...<commit sha>"
                                        :deps/root "modules/setup/cli"}
 ...}
```

## Usage

### Parsing CLI options

Require `gorillalabs.bauhaus.setup.cli` namespace and parse command line options given to your application (`-main` fn):

```clojure
(defn -main [& args]
  ...
  (let [{config-path :config} (apply gorillalabs.bauhaus.setup.cli/init
                                     gorillalabs.bauhaus.setup.cli/default-cli-options
                                     args)
        ...]
    ))
```

Of course, you can use your own [CLI options](https://github.com/clojure/tools.cli/blob/master/README.md).

The `gorillalabs.bauhaus.setup.cli/default-cli-options` provide options to read a config file location (`-c/--config`) parsed to `:config-path`. It also contains a `-h/--help` printing out the [options summary](https://github.com/clojure/tools.cli/blob/master/README.md#options-summary).


### System exit wrapper

Sometimes, you need to exit your System, e.g. in case of a fatal error. However,
things are more complicated in a REPL during development. Maybe you want to test
error handling and provoke that system, but don't want your REPL to be shut down?

So, `gorillalabs.bauhaus.setup.exit/exit` wraps/replaces `System/exit`, checking if you're inside a REPL - in which case exit would not really exit the System, but instead log and throw an Error instead, so you can move on instead.