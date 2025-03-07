# Authenticate Using Microsoft Entra ID App Registrations


Module to authenticate a client application using [Azure Active Directory / Entra ID Applications](https://entra.microsoft.com/#blade/Microsoft_AAD_RegisteredApps/ApplicationsListBlade/quickStartType//sourceType/Microsoft_AAD_IAM).

This is basically a thin wrapper around the [MSAL4J](https://github.com/AzureAD/microsoft-authentication-library-for-java) library, easing clojure access and integrating it into the Bauhaus Integrant environment.

## Install

Add this project to your `deps.edn`. In the Bauhaus project itself, this is possible from the local repository structure using

```clojure
{:deps {...
        org.gorillalabs.bauhaus/auth.entra-id.identity {:local/root "../../modules/auth/entra-id/identity"}
        ...
        }}
```

or using git coordinates like this, but with the latest :git/sha

```clojure
{:deps {...
        org.gorillalabs.bauhaus/auth.entra-id.identity {:git/url   "git@github.com:gorillalabs/bauhaus.git"
                                                        :git/sha   "AE...<commit sha>"
                                                        :deps/root "modules/auth/entra-id/identity"}
        ...}}
```

## Usage

### Setting up an Entra ID app registration

You will need a client application in the Azure Active Directory / Entra ID Applications as the subject you want to identify.

Once you create a new "App Registration", you will also need to configure Secrets (in the "Certificates & Secrets" tab).

### Configure

Configure your application's "Application (client) ID", "Directory (tenant) ID" and the "Value" of the Client Secret, e.g. by keeping
them in '~/.gorillalabs/.secrets/entra/identity.edn' in a format similar to the following:

```edn
{:authority    "https://login.microsoftonline.com/<Directory (tenant) ID>/"
 :app-id       "<Application (client) ID>"
 :secret-value "<Value of Secret>"}
```

`authority` is the Azure Tenant URL like this `"https://login.microsoftonline.com/<your tenant id>/"`.
`app-id` and `secret-values` depend on your (client) application you want to authenticate, it's basically the identity. Make sure to never ever version control your secret-value.


Use this in your shell to create that file:

```bash
mkdir -p ~/.gorillalabs/.secrets/entra/
touch ~/.gorillalabs/.secrets/entra/identity.edn
open ~/.gorillalabs/.secrets/entra/identity.edn
```

Those values will be picked up in your [development configuration](dev-resources/dev-config.edn), as you can see using
`(repl-config (dev.config/config))` (in the `user` namespace), providing you with the proper Integrant system configuration.

### Direct use

Without Integrant, you can directly create a provider using the `client` function with the `authority`, `app-id` and `secret-value` as arguments:
Require the `gorillalabs.bauhaus.auth.entra.identity.authenticate` namespace (e.g., as `entra.auth`) and create a new MSAL client:

```clojure
(do (require '[gorillalabs.bauhaus.auth.entra.identity.authenticate :as entra.auth])
    (let [cfg (dev.config/config)
          authority (:authority (:auth cfg))
          app-id (:app-id (:auth cfg))
          secret-value (:secret-value (:auth cfg))]
      (def client (entra.auth/client authority
                                     app-id
                                     secret-value))
      client))
```

To acquire a token, require the `gorillalabs.bauhaus.auth.entra.identity.authenticate` namespace and use the `acquire-token` function:
```clojure
(do (require '[gorillalabs.bauhaus.auth.entra.identity.authenticate :as entra.auth])
    (entra.auth/acquire-token client
                              #{"api://gorillalabs.bauhaus/auth/entra/DemoProvider/.default"}))
```
with a scope URL for the application you want to authenticate to.
`api://gorillalabs.bauhaus/auth/entra/DemoProvider` is an app registered just for demonstration purposes.

### Usage in the Bauhaus Integrant environment

Configure your Integrant system to provide the `:auth.entra/provider` with the apropriate config (`:authority`, `:app-id`, and `:secret-value`):

```clojure
{:auth.entra/provider {:authority    (authority config)
                       :app-id       (app-id config)
                       :secret-value (secret-value config)}}
```

Once your system is started, you can use that element to obtain a JWT. In the dev-profile REPL this looks like this:

```clojure
(require '[gorillalabs.bauhaus.auth.identity.authenticate :as auth])
(auth/acquire-token (:auth.entra/provider integrant.repl.state/system)
                    #{"api://gorillalabs.bauhaus/auth/entra/DemoProvider/.default"})
```