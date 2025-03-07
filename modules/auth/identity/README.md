# Authenticate using Identity Providers

Module to authenticate a client application using an IAM provider like Azure Entra ID Applications.
This is just a Protocol module, the implementation is in the respective module (e.g., `org.gorillalabs.bauhaus/auth.entra-id.identity`).

## Install

Add this project to your `deps.edn`. In the Bauhaus project itself, this is possible from the local repository structure using

```clojure
{:deps {...
        org.gorillalabs.bauhaus/auth.identity {:local/root "../../modules/auth/identity"}
        ...
        }}
```

or using git coordinates like this, but with the latest :git/sha

```clojure
{:deps {...
        org.gorillalabs.bauhaus/auth.identity {:git/url   "git@github.com:gorillalabs/bauhaus.git"
                                                        :git/sha   "AE...<commit sha>"
                                                        :deps/root "modules/auth/identity"}
        ...}}
```

## Usage (in the Bauhaus Integrant environment)

Configure your Integrant system to provide an identity provider, e.g. `:auth.entra/provider`
with the apropriate config (`:authority`, `:app-id`, and `:secret-value`):

```clojure
{:auth.entra/provider {:authority    (authority config)
                     :app-id       (app-id config)
                     :secret-value (secret-value config)}}
```

Once your system is started, you can use that element with the Protocol method `acquire-token` 
to obtain a JWT. In the dev-profile REPL this looks like this:

```clojure
(require '[gorillalabs.bauhaus.auth.identity.authenticate :as auth])
(auth/acquire-token (:auth.entra/provider integrant.repl.state/system)
                    #{"api://gorillalabs.bauhaus/auth/entra/DemoProvider/.default"})
```