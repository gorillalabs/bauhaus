(ns gorillalabs.bauhaus.http-server.server
  (:require [org.httpkit.server :as httpkit]
            [gorillalabs.collections :as c]
            [integrant.core :as ig]
            [taoensso.timbre :as log]))


(defn start-server [port app]
  (let [server (httpkit/run-server app {:port                 port
                                        :legacy-return-value? false})]
    (log/debug "Server started!"
               :server? (not (nil? server))
               :status (when server (httpkit/server-status server))
               :port (when server (httpkit/server-port server)))
    server))



(defmethod ig/init-key :gorillalabs.bauhaus.http/server [_ {:keys [port app] :as opts}]
  (log/debug "Starting http server ...")
  (try {:server (start-server port app)
        :opts   opts}
       (catch Throwable t
         (log/error t "Unable to start http server"))))

(defmethod ig/halt-key! :gorillalabs.bauhaus.http/server [_ {:keys [server opts]}]
  (log/debug "Stopping http server ... "
             :server? (not (nil? server))
             :status (when server (httpkit/server-status server))
             :timeout (:timeout opts))
  (when server
    (let [timeout (:timeout opts)
          opts (c/assoc-if {} :timeout timeout)
          stop (httpkit/server-stop! server opts)]
      (log/debug "Stopping http server"
                 :stopping (nil? stop)
                 :status (httpkit/server-status server))
      ;; return from ig/halt-key! only if server is stopped (or timeout is reached)!
      (log/info "Stop"))))