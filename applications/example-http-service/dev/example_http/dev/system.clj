(ns example-http.dev.system
  (:require
    [gorillalabs.collections :as c]
    [integrant.core :as ig]
    [taoensso.timbre :as log]
    [aero.core :as aero]
    [example-http.config]
    [example-http.system]
    [example-http.dev.core]                                 ;; To alter the system map with a new core.
    ))

(defmethod ig/init-key :example/hello-de [_ {:keys [who]}]
  (log/info "Hallo" {:an who}))

(defn dev-config []
  (log/debug "Read dev config")
  (c/deep-merge
    (when-let [config (clojure.java.io/resource "config.edn")]
      (aero/read-config config))
    (when-let [config (clojure.java.io/resource "dev-config.edn")]
      (aero/read-config config))))


(defn repl-system-override [system-map config]
  (c/deep-merge (dissoc system-map :gorillalabs.bauhaus.http-example/core)
                {:gorillalabs.bauhaus.http-example/api {:core         (ig/ref :http-example.dev/core)
                                                        :auth-backend (ig/ref :gorillalabs.bauhaus.http-server/auth-backend)}
                 :http-example.dev/core {}}))

(defn dev-system []
  (-> (example-http.system/system-config (dev-config))
      (dissoc :example/hello-en)
      (repl-system-override (dev-config))))

#_;
        (dev-system)