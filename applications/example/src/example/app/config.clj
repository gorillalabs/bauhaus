(ns example.app.config)

(defn who [config]
  (:who config))

(defn repl-port [config]
  (:port (:repl config)))