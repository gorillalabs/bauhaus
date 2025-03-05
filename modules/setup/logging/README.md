# Bauhaus Setup Logging

Module to setup proper logging infrastructure.

We log using [timbre](https://github.com/ptaoussanis/timbre).

We use structured logging writing [JSON log messages](https://github.com/viesti/timbre-json-appender)
to a log file which then can be tailed by a log agent and sent to your logging infrastructure.

Also, it sets up collecting messages from  different logging libraries using
[SLF4J](http://www.slf4j.org/), so no matter what logging library a third party
is using, you only configure logging in one place.

Supported logging libraries:

* [clojure/tools.logging](https://github.com/clojure/tools.logging)
* [SLF4J](http://www.slf4j.org/)
* [Apache Commons Logging](https://commons.apache.org/logging)
* [Log4J 2](https://logging.apache.org/log4j/2.x/)
* [Log4J](http://logging.apache.org/log4j/1.2/)
* [java.util.logging](https://docs.oracle.com/en/java/javase/13/docs/api/java.logging/java/util/logging/package-summary.html)

_Setup logging_ sets up handling of uncaught exceptions, so no exception goes by unnoticed anymore. Also, it captures elements not serializable to JSON.

**⚠️ Attention ⚠️**: _Setup logging_ **does not** care for you logging secrets or personal identifyable information. So make sure you your logs are safe from an information security perspective and DSGVO-wise (personal identifyable information).
Never log API secrets, user credentials or tokens, usernames without consent or even user IP addresses without consent.



## Usage in a library

In a library, you may choose whichever logging library you want to use, as long as it's supported by _setup logging_ in the application using that library.

We strongly advise to use timbre.

However, as you want to configure logging for the REPL used to develop the library or the tests running, you can use _setup logging_ as follows:

### Install

Add `com.taoensso/timbre {:mvn/version "5.2.1"}` to your deps.edn `:deps` map.

Also, add this project to your deps.edn as `dev` or `test` `:extra-deps`. In Bauhaus itself, this is possible from the local repository structure using

```clojure
:aliases {:dev {:extra-deps  {org.gorillalabs.bauhaus/setup-logging {:local/root "../../modules/setup/logging"}}}}
```

or using git coordinates like this, but with the latest :git/sha

```clojure
{       ...
 org.gorillalabs.bauhaus/setup-logging {:git/url   "git@github.com:gorillalabs/bauhaus.git"
                                          :git/sha   "AE...<commit sha>"
                                          :deps/root "modules/setup/logging"}
        ...}
```

### Initialize

After REPL startup (or before running tests), call `gorillalabs.bauhaus.setup.log/init-dev-logging` which will setup simple message logging to the console.


## Usage in an application


### Install

Add this project to your deps.edn. In the Bauhaus project itself, this is possible from the local repository structure using

```clojure
{:deps {...
        org.gorillalabs.bauhaus/setup-logging {:local/root "../../modules/setup/logging"}
        ...
        }}
```

or using git coordinates like this, but with the latest :git/sha

```clojure
{       ...
 org.gorillalabs.bauhaus/setup-logging {:git/url   "git@github.com:gorillalabs/bauhaus.git"
                                        :git/sha   "AE...<commit sha>"
                                        :deps/root "modules/setup/logging"}
 ...}
```

### Configure JVM

To configure [clojure/tools.logging](https://github.com/clojure/tools.logging), you need to run your code with the System property `-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory` set (see [Selecting a logging implementation](https://github.com/clojure/tools.logging#selecting-a-logging-implementation)).


### Initialize logging

During system startup, call `gorillalabs.bauhaus.setup.log/init-logging` with the location of your (rotating) logfile.

Alternatively, there's `gorillalabs.bauhaus.setup.log/init-dev-logging` which will use simple message logging to the console.

### Start logging

Require `[taoensso.timbre :as log]` and use timbre just as it's supposed to be used.

You might also use any other logging API, but we recommend using timbre for our code.

### Change logging configuration

You can change logging configuration at any point in time using timbre's `(log/merge-config! new-config)`.