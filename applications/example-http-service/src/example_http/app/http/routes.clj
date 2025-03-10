(ns example-http.app.http.routes
  (:require [gorillalabs.bauhaus.http.auth :as jack.auth]
            [ring.util.response :as response]
            [taoensso.timbre :as log]
            [buddy.auth.middleware :as auth.middleware]
            [buddy.auth.accessrules :as accessrules]

            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]))

(defn test-connectivity [req]
  (log/info "Testing connectivity"
            :event {:type     :gorillalabs.bauhaus.http-server.dev/inbound-connectivity-test
                    :category #{:gorillalabs.bauhaus.http-server :dev}
                    :outcome  :success}
            :user {:identity (:identity req)}
            :request {:uri (:uri req)})
  (response/response {:status "ok"}))

(defn function-1-thing [function-1-core req]
  (log/info "Doing function-1-thing"
            :event {:type     :gorillalabs.bauhaus.http-server.dev/function-1-thing
                    :category #{:gorillalabs.bauhaus.http-server :dev}
                    :outcome  :success}
            :user {:identity (:identity req)}
            :request {:uri (:uri req)})
  (response/response {:status "ok"}))

(defn function-1-api [function-1-core]
  ["/api/v1" {:middleware []
              :swagger    {:tags ["dev"]}}
   ["/test" {:get {:handler test-connectivity}}]
   ["/function/v1"
    ["/doit" {:post    {:handler (partial function-1-thing function-1-core)}
              :swagger {:tags ["function-1"]}
              }]]])


(defn swagger []
  ["" {:no-doc true}
   ["/swagger.json" {:get (swagger/create-swagger-handler)}]
   ["/api-docs/*" {:get (swagger-ui/create-swagger-ui-handler)}]])

(defn routes [function-1-core]
  (conj [""]
        (function-1-api function-1-core)
        (swagger)
        ))


;; make these into tests:
#_(reitit.core/match-by-path (reitit.core/router (routes nil nil nil nil)) "/jack/v1/dataset/v1/published")
#_(reitit.core/match-by-path (reitit.core/router (routes nil nil nil nil)) "/pricehub/v1/test")
