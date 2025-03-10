(ns example-http.config
  (:require [gorillalabs.bauhaus.http.auth :as auth]
            [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helper functions to read config
(defn http-port [config]
  (int (:port (:http config))))

(defn stop-timeout [config]
  (when-let [timeout (:stop-timeout (:http config))]
    (int timeout)))

(defn http-auth-valid-audiences [config]
  (let [valid-aud-fn (:valid-audiences (:http config))]
    (if (ifn? valid-aud-fn)
      valid-aud-fn
      (throw (ex-info "Valid audiences must be a function, but is not." {:valid-audiences valid-aud-fn})))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Access rules

(defn rules []
  [{:pattern #"^/foo/.*"
    :handler {:and [auth/authenticated]}}
   {:pattern #"^/bar/.*"
    :handler {:and [auth/allow-unauthenticated]}}
   {:pattern #"^/^(foo|bar).*"
    :handler {:and [(fn [request]
                      (log/info "Custom rule" request)
                      (= (:remote-addr request) "0:0:0:0:0:0:0:1")
                      )]}}
   ])

(defn repl-port [config]
  (when-let [port (:port (:repl config))]
    (int port)))

