(ns user
  (:require [gorillalabs.bauhaus.dev.config :as dev.config]
            [gorillalabs.bauhaus.setup.exit]
            [gorillalabs.bauhaus.setup.logging :as setup.logging]
            [integrant.repl :refer [clear halt prep init reset reset-all]]
            [dev.system]
            [taoensso.timbre :as log]))

(setup.logging/init-dev-logging
  {:min-level [["*" :debug]]})


(defn go []
  (dev.config/init)
  (integrant.repl/go))


(integrant.repl/set-prep!
  dev.system/dev-system)

;-----------------------------------------------------------------------
; Use
#_;
        (go)

#_(integrant.repl/reset)


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
