(ns gorillalabs.bauhaus.auth.entra.identity.config
  "Helper functions to access configuration from configuration maps.
  With this, you could
  "
  )


(defn config-fn [& path]
  (fn [config]
    (get-in config path)))

(def authority-fn config-fn)
(def app-id-fn config-fn)
(def secret-value-fn config-fn)