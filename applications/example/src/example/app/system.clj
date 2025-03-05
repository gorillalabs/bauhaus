(ns example.app.system
  (:require [integrant.core :as ig]
            [example.app.config :as config]
            [taoensso.timbre :as log]))

(defmethod ig/init-key :example/hello-en [_ {:keys [who]}]
  (log/error "Hello" {:to who}))

(defn system-config [config]
  {:example/hello-en {:who (config/who config)}})