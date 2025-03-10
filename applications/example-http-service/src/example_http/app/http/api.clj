(ns example-http.app.http.api
  (:require [muuntaja.middleware :as mm]
            [reitit.ring :as ring]
            [buddy.auth.middleware]
            [buddy.auth.accessrules :as accessrules]
            [ring.middleware.content-type :as ct]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [integrant.core :as ig]
            [example-http.config :as config]
            [example-http.app.http.routes :as routes]
            [muuntaja.middleware]
            [taoensso.timbre :as log]
            [muuntaja.core :as m]
            [muuntaja.format.json :as json-format]
            [ring.util.response :as response]
            [ring.middleware.gzip :as middleware.gzip]))

(def m
  "The `muuntaja.core/Muuntaja` instance we use to decode http responses."
  (m/create
    (-> m/default-options
        (assoc-in [:formats "application/problem+json"] json-format/format)
        )))

(defn wrap-logging [handler]
  (fn [req]
    (let [request-id (str (random-uuid))
          req (assoc req :request-id request-id)
          response (log/with-context {:request-id request-id}
                                     (handler req))]
      (response/header response "X-Request-ID" request-id))))

(defn middleware []
  ;; TODO: Use secure-api-defaults
  [#(wrap-logging %)
   #(wrap-defaults % api-defaults)
   #(muuntaja.middleware/wrap-format % m)
   ])

(defn app [core]
  (ring/ring-handler
    (ring/router
      (routes/routes core))
    (ring/routes
      (ring/create-default-handler))
    {:middleware (middleware)}))


(defmethod ig/init-key :gorillalabs.bauhaus.http-example/api [_ {:keys [core auth-backend]}]
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