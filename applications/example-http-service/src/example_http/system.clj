(ns example-http.system
  (:require [integrant.core :as ig]
            [example-http.config :as config]
    ;; For Integrant system configuration:
            [gorillalabs.bauhaus.http-server.server]        ;; HTTP Server
            [gorillalabs.bauhaus.http.auth]                 ;; Auth Backend

            [example-http.app.http.api]
            [example-http.app.core]
            ))


(defn system-config [config]
  {:gorillalabs.bauhaus.http/server              {:port    (config/http-port config)
                                                  :timeout (config/stop-timeout config)
                                                  :app     (ig/ref :gorillalabs.bauhaus.http-example/api)}
   :gorillalabs.bauhaus.http-example/api         {:core         (ig/ref :gorillalabs.bauhaus.http-example/core)
                                                  :auth-backend (ig/ref :gorillalabs.bauhaus.http-server/auth-backend)}
   :gorillalabs.bauhaus.http-example/core        {}
   :gorillalabs.bauhaus.http-server/auth-backend {:valid-audiences (config/http-auth-valid-audiences config)}})