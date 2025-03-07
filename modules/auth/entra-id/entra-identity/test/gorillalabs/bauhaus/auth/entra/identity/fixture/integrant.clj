(ns gorillalabs.bauhaus.auth.entra.identity.fixture.integrant
  (:require [integrant.core :as ig]
            [gorillalabs.bauhaus.dev.config :as dev.config]
            [gorillalabs.bauhaus.auth.entra.identity.config :as config]))

(def authority (config/authority-fn :auth :authority))
(def app-id (config/app-id-fn :auth :app-id))
(def secret-value (config/secret-value-fn :auth :secret-value))

(defn test-config [config]
  {:auth.entra/provider {:authority    (authority config)
                 :app-id       (app-id config)
                 :secret-value (secret-value config)}})

(def ^:dynamic system {})

(defn with-system [f]
  (binding [system (ig/init (test-config (dev.config/config)))]
    (f)
    (ig/halt! system)))
