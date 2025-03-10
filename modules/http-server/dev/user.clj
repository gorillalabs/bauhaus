(ns user
  (:require
    [config]
    [integrant.core :as ig]
    [gorillalabs.bauhaus.setup.logging :as setup.logging]
    [gorillalabs.bauhaus.dev.config :as dev.config]
    [gorillalabs.bauhaus.http-server.server]
    [gorillalabs.bauhaus.http.auth]
    [dev-example]
    [integrant.repl :refer [clear halt prep init reset reset-all]]
    [taoensso.timbre :as log]))

(setup.logging/init-dev-logging)

(defn go []
  (dev.config/init)
  (integrant.repl/go))



(defn repl-config [config]
  {:gorillalabs.bauhaus.http/server              {:port    (config/http-port config)
                                                  :timeout (config/stop-timeout config)
                                                  :app     (ig/ref :gorillalabs.bauhaus.http.dev/api)}
   :gorillalabs.bauhaus.http.dev/api             {:auth-backend (ig/ref :gorillalabs.bauhaus.http-server/auth-backend)
                                                  :core         (ig/ref :gorillalabs.bauhaus.http.dev/core)}
   :gorillalabs.bauhaus.http.dev/core            {}
   :gorillalabs.bauhaus.http-server/auth-backend {:valid-audiences (config/http-auth-valid-audiences config)}
   })

(integrant.repl/set-prep!
  (fn []
    (repl-config (dev.config/config))))


;-----------------------------------------------------------------------
; Use
#_;
        (go)

; to prep the configuration
#_;
        integrant.repl.state/config
; and turn it into a running system, which is stored in
#_;
        integrant.repl.state/system

;-----------------------------------------------------------------------

;; Try your dev config using
;;   curl -vv -H "Accept: application/json" -H "accept-charset: iso-8859-1" http://localhost:8000/bar/
;;   curl -vv -H "Accept: application/edn" http://localhost:8000/bar/
;;   curl -vv -H "Accept: application/edn" http://localhost:8000/foo/
;;   curl -vv -H "Authorization: Bearer <Your Bearer Token>" -H "Accept: application/edn" http://localhost:8000/foo/
;-----------------------------------------------------------------------


#_; Once the system is running, we can stop it at any time:
        (halt)

; If we want to reload our source files and restart the system, we can use:
#_;
        (reset)
;-----------------------------------------------------------------------
