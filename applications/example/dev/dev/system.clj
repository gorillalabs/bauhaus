(ns dev.system
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

(defn dev-config []
  (log/debug "Read dev config")
  (c/deep-merge
    (when-let [config (clojure.java.io/resource "config.edn")]
      (aero/read-config config))
    (when-let [config (clojure.java.io/resource "dev-config.edn")]
      (aero/read-config config))))


(defn repl-system-override [system-map config]
  (let [profile (-> config :aws :profile)]
    (-> system-map
        (c/deep-merge
          {:example/hello-de {:who (config/who config)}
           }))))

(defn dev-system []
  (-> (example.app.system/system-config (dev-config))
      (dissoc :example/hello-en)
      (repl-system-override (dev-config))))

#_;
        (dev-system)