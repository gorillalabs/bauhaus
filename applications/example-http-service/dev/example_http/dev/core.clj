(ns example-http.dev.core
  (:require [integrant.core :as ig]))


(defmethod ig/init-key :http-example.dev/core [_ _]
  ;; this integrant component is a minimal functional core (as in hexagonal architecture)
  ;; to be connected to the HTTP api (see below, :gorillalabs.bauhaus.http.dev/api).
  ;;
  (fn functional-core []
    ; (Thread/sleep 10000) ;; Use this to test the shutdown behaviour.
    {:msg    "Hello Developer 👩🏻‍💻"
     :status "purple"}))