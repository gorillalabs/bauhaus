(ns config
  (:require [gorillalabs.bauhaus.http.auth :as auth]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helper functions to read config
(defn http-port [config]
  (int (:port (:http config))))

(defn stop-timeout [config]
  (int (:stop-timeout (:http config))))


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
    :handler {:and [auth/allow-unauthenticated]}}])

