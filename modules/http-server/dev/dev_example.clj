(ns dev-example
  (:require [buddy.auth.middleware]
            [config]
            [muuntaja.middleware :as mm]
            [buddy.auth.accessrules :as accessrules]
            [ring.middleware.content-type :as ct]
            [integrant.core :as ig]
            [taoensso.timbre :as log]
            [ring.util.response :as response]))

(defmethod ig/init-key :gorillalabs.bauhaus.http.dev/core [_ _]
  ;; this integrant component is a minimal functional core (as in hexagonal architecture)
  ;; to be connected to the HTTP api (see below, :gorillalabs.bauhaus.http.dev/api).
  ;;
  (fn functional-core []
    ; (Thread/sleep 10000) ;; Use this to test the shutdown behaviour.
    {:msg    "Hello World 🌍"
     :status "green"}))

(defmethod ig/init-key :gorillalabs.bauhaus.http.dev/api [_ {:keys [core auth-backend]}]
  ;; this integrant component is the HTTP API port to the (sample) functional core
  ;; (see above, :gorillalabs.bauhaus.http.dev/core),
  ;; i.e. it provides a request handler to access the functions provided by the functional core.
  ;;
  ;; It also allows for a minimum auth handling, using the auth-backend injected.
  (-> (fn [req]
        (log/info "Request" req)
        (ring.util.response/response (core)))
      (accessrules/wrap-access-rules {:rules    (config/rules)
                                      :on-error gorillalabs.bauhaus.http.auth/authorization-problem-handler})
      (buddy.auth.middleware/wrap-authentication auth-backend)
      (buddy.auth.middleware/wrap-authorization auth-backend)
      mm/wrap-format
      ct/wrap-content-type))