(ns example-http.app
  (:require
    [clojure.java.io :as io]
    [gorillalabs.bauhaus.setup.exit :as exit]
    [example-http.system :as system]
    [clojure.core.server]
    [aero.core :as aero]
    [integrant.core :as ig]
    [example-http.config :as config]
    [gorillalabs.bauhaus.setup.logging :as setup.logging]
    [gorillalabs.bauhaus.setup.shutdown :as setup.shutdown]
    [gorillalabs.bauhaus.setup.cli :as cli]
    [taoensso.timbre :as log]
    [taoensso.timbre.tools.logging])
  (:gen-class))

(declare system)

(defn read-config [config-path]
  (let [f (io/as-file config-path)]
    (aero/read-config
      (if (.exists f)
        f
        (throw (ex-info "Sorry, couldn't find the config file you provided."
                        {::config-path config-path
                         ::current-dir (.getParent (.getAbsoluteFile (io/as-file ".")))
                         ::full-path   (str (.getCanonicalFile f))}))))))


(defn shutdown []
  (when system
    (log/warn "Shutting down system...")
    (ig/halt! system)
    (log/warn "System shut down.")))



(defn -main [& args]
  (try
    (let [{config-path :config} (apply cli/init cli/default-cli-options args)
          config (read-config config-path)]
      (setup.logging/init-logging (setup.logging/log-config "../example-http-service.logs/app.log"))

      (setup.shutdown/add-hook! :clojure.core/shutdown-agents setup.shutdown/agents)
      (setup.shutdown/hook-order! [:clojure.core/shutdown-agents])


      (when (config/repl-port config)
        (let [socket (clojure.core.server/start-server {:port   (config/repl-port config)
                                                        :name   "socket-repl"
                                                        :accept 'clojure.core.server/repl})]
          (log/info "Started REPL socket server" {:socket socket})))

      (def system (ig/init (system/system-config config)))
      (setup.shutdown/add-hook! :app/shutdown-system shutdown)
      (setup.shutdown/hook-order! [:app/shutdown-system :clojure.core/shutdown-agents])
      (log/debug "System started"))
    (catch Throwable t
      (log/error t "Unable to initiate system" {:status :error})
      (exit/exit -1))))


;; The application will write a log file to ../example-http-service.logs/app.log.
#_;
        (example-http.app/-main "-c" "resources/config.edn")