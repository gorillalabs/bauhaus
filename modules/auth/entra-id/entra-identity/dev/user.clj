(ns user
  (:require
    [gorillalabs.bauhaus.setup.logging :as setup.logging]
    [gorillalabs.bauhaus.dev.config :as dev.config]
    [gorillalabs.bauhaus.auth.entra.identity.config :as config]
    [integrant.repl :refer [clear halt prep init reset reset-all]]))

(setup.logging/init-dev-logging)

(defn go []
  (dev.config/init)
  (integrant.repl/go))

(def authority (config/authority-fn :auth :authority))
(def app-id (config/app-id-fn :auth :app-id))
(def secret-value (config/secret-value-fn :auth :secret-value))

(defn repl-config [config]
  {:auth.entra/provider {:authority    (authority config)
                       :app-id       (app-id config)
                       :secret-value (secret-value config)}})

(integrant.repl/set-prep!
  (fn []
    (repl-config (dev.config/config))))


;-----------------------------------------------------------------------
; Use

#_; Make sure to have the Integrant module loaded
        (require 'gorillalabs.bauhaus.auth.entra.identity.authenticate)
#_;
        (go)

; to prep the configuration
#_;
        integrant.repl.state/config
; and turn it into a running system, which is stored in
#_;
        integrant.repl.state/system

;-----------------------------------------------------------------------


#_; Once the system is running, we can stop it at any time:
        (halt)

; If we want to reload our source files and restart the system, we can use:
#_;
        (reset)
;-----------------------------------------------------------------------
