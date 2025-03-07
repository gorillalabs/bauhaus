(ns gorillalabs.bauhaus.http.problem
  "Helpers to provide rfc7807 type problem documents.

 You can throw a problem (as it is an ex-info or you can wrap it into an http response."
  (:require [jsonista.core]
            [ring.util.response :as response]
            [taoensso.timbre :as log]))


(defn assoc-if
  ([map key val] (if val
                   (assoc map key val)
                   map))
  ([map key val & kvs]
   (let [ret (assoc-if map key val)]
     (if kvs
       (if (next kvs)
         (recur ret (first kvs) (second kvs) (nnext kvs))
         (throw (IllegalArgumentException.
                  "assoc-if expects even number of arguments after map/vector, found odd number")))
       ret))))

(defn response
  ;; TODO: Make this a middleware if possible...
  "Returns a skeletal rfc7807 compliant Ring response with the given body, status of 200, and no
  headers."
  [p]
  (let [p* (or (ex-data p) p)]
    (log/spy (->
               {:status                (:status p*)
                :muuntaja/content-type "application/problem+json"
                :headers               {}
                :body                  p* #_(jsonista.core/write-value-as-bytes p*)}
               #_(response/content-type "application/problem+json")))))

(defn problem [type & {:keys [status title detail] :or {status 500} :as details}]
  (ex-info type
           (merge (dissoc details :type :status :title :detail)
                  (assoc-if {:type type}
                            :status status
                            :title title
                            :detail detail))))

(defn forbidden []
  (problem "FORBIDDEN" :status 403))

(defn not-found [resource]
  (problem "NOT FOUND"
           :status 404
           :resource resource
           ))