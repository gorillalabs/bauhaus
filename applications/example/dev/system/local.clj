(ns system.local
  (:require
    [gorillalabs.collections :as c]
    [integrant.core :as ig]
    [taoensso.timbre :as log]
    [aero.core :as aero]
    [example.app.config :as config]
    [example.app.system :as system]
    ))

(defmethod ig/init-key :example/hello-de [_ {:keys [who]}]
  (log/info "Hallo" {:an who}))

(defn local-dev-config []
  (log/debug "Read dev config")
  (c/deep-merge
    (when-let [config (clojure.java.io/resource "config.edn")]
      (aero/read-config config))
    (when-let [config (clojure.java.io/resource "local-dev-config.edn")]
      (aero/read-config config))))


(defn local-repl-system-override [system-map config]
  (let [profile (-> config :aws :profile)]
    (-> system-map
        (c/deep-merge
          {:example/hello-de {:who (config/who config)}
           }))))

(defn local-dev-system []
  (-> (example.app.system/system-config (local-dev-config))
      (dissoc :example/hello-en)
      (local-repl-system-override (local-dev-config))))

#_;
        (local-dev-system)