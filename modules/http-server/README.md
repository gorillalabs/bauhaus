# HTTP Server


Module to provide an [http-kit](https://github.com/http-kit/http-kit)-backed http server as an [Integrant](https://github.com/weavejester/integrant) component, together with some additional functionality:

* Throwable rfc7807 problem documents, also to be used as http responses.
* Entra-ID backed authentication and authorization (for app registrations).

## Install

Add this project to your `deps.edn`. In the Bauhaus project itself, this is possible from the local repository structure using

```clojure
{:deps {...
        org.gorillalabs.bauhaus/http-server {:local/root "../../modules/http-server"}
        ...
        }}
```

or using git coordinates like this, but with the latest :git/sha

```clojure
{:deps {...
        org.gorillalabs.bauhaus/http-server {:git/url   "git@github.com:gorillalabs/bauhaus.git"
                                                        :git/sha   "AE...<commit sha>"
                                                        :deps/root "modules/http-server"}
        ...}}
```

## Usage

### HTTP Server
Require `gorillalabs.bauhaus.http-server.server` and configure `:gorillalabs.bauhaus.http/server` in your Integrant system map with `port` and `app` keys.

### Problem Documents
To indicate a problem, throw a `gorillalabs.bauhaus.http.problem/problem` like this (with a exception handling middleware) or return it wrapped in a `gorillalabs.bauhaus.http.problem/problem-response`.