(ns user
  (:require
    [gorillalabs.bauhaus.setup.logging :as setup-logging]
    [taoensso.timbre :as log]))

(setup-logging/init-dev-logging)

(log/info "test")
