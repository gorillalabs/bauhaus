(ns gorillalabs.bauhaus.auth.entra.identity.authenticate
  (:require [integrant.core :as ig]
            [taoensso.timbre :as log]
            [gorillalabs.bauhaus.auth.identity.authenticate :as auth]
            )
  (:import [com.microsoft.aad.msal4j
            ClientCredentialFactory
            ConfidentialClientApplication
            ClientCredentialParameters]))

(defn builder [client-id credential]
  (ConfidentialClientApplication/builder client-id credential))


;; From https://docs.microsoft.com/en-us/azure/active-directory/develop/scenario-daemon-acquire-token?tabs=java

;; This is the Application ID URI (of an exposed API, here within the Gorillalabs TestProvider App Registration) + /.default
;; see https://portal.azure.com/#view/Microsoft_AAD_RegisteredApps/ApplicationMenuBlade/~/ProtectAnAPI/appId/839f7bc7-e291-4842-b18a-4d1c4c57690d/isMSAApp~/false
;; and Client credential flows must have a scope value with /.default suffixed to the resource identifier (application ID URI).

(defn client [authority client-id client-secret]
  (let [credential (ClientCredentialFactory/createFromSecret client-secret)]
    (-> (builder client-id credential)
        (.authority authority)
        (.build))))

(defn acquire-token [client scopes]
  (-> (.join (.acquireToken client
                            (.build (ClientCredentialParameters/builder scopes))))

      (.accessToken)))



;; Use https://jwt.io Debugger to decode the token:
;;The application ID will be included in the audience claim.
;; see https://docs.microsoft.com/en-us/azure/active-directory/develop/access-tokens for list of claims


(defmethod ig/init-key :auth.entra/provider [_ {:keys [authority app-id secret-value] :as opts}]
  (let [entra-client (client authority
                             app-id
                             secret-value)]
    (reify auth/IdentityProvider
      (auth/acquire-token [_ scopes]
        (acquire-token entra-client scopes)))))

#_;
        (do
          (require '[gorillalabs.bauhaus.dev.config :as dev.config])
          (let [cfg (dev.config/config)
                authority (:authority (:auth cfg))
                app-id (:app-id (:auth cfg))
                secret-value (:secret-value (:auth cfg))
                client (client authority
                               app-id
                               secret-value)]
            (acquire-token client
                           #{"api://gorillalabs.bauhaus/auth/entra/DemoProvider/.default"})))

#_;
        (do

          ;; setup a REPL system using integrant, see https://github.com/weavejester/integrant-repl.
          ;; the system configuration is defined in the user namespace (see [project root]/dev/user.clj):
          ;; see
          #_user/repl-config
          ;; The started system map is stored in integrant.repl.state/system
          (require '[integrant.repl])
          (integrant.repl/reset)

          (def demo-api
            {;; See https://portal.azure.com/#view/Microsoft_AAD_RegisteredApps/ApplicationMenuBlade/~/Overview/appId/f392d268-0523-40e3-8eed-02910e7935d7/isMSAApp/true
             :app-id-url "api://gorillalabs.bauhaus/auth/entra/DemoProvider"})


          (auth/acquire-token (:auth.entra/provider integrant.repl.state/system)
                              #{(str (:app-id-url demo-api) "/.default")}))

#_;
        (auth/acquire-token (:auth.entra/provider integrant.repl.state/system)
                            #{"api://gorillalabs.bauhaus/auth/entra/DemoProvider/.default"})