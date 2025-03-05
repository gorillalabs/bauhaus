(ns gorillalabs.bauhaus.setup.shutdown
  (:require [taoensso.timbre :as log]))
;; This is from https://github.com/zonotope/shutdown/blob/master/src/shutdown/core.clj
;; combined with https://medium.com/helpshift-engineering/achieving-graceful-restarts-of-clojure-services-b3a3b9c1d60d
;;

;; TODO: Split between "regular" hooks running in any order
;; and the "ordered" hooks running on a single thread

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helper functions for an ordered collection of shutdown hooks
;; ===
;;
;; We want to run shutdown hooks in a given order, e.g. to first shut down the
;; http API, then the consumers of queues, finally the workers processing items
;; from that queue and finally the clojure agents.
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn safe-index-of
  "Given a vector and a (potential) element on that vector,
  returns the index of that element in the vector.

  If the element is not in the vector, returns (count v), if default
  is :end, -1 otherwise.

  Thus, you can determine whether by default unknown elements are identified
  at the start (-1) or the end (count) of the vector."
  [v k default]
  (try
    (let [idx (.indexOf v k)]
      (if (and (neg? idx) (= :end default))
        (count v)
        idx))
    (catch NullPointerException e
      (if (= :end default)
        0
        -1))))

(defn compare-by-vector "Compares two keys based on the order given in the
vector. If a key is not in the order, it will be at start by default, but you
can set use (compare-by-vector order key1 key2 :default :end)."
  [order key1 key2 & {default :default
                      :or     {default :start}}]
  (compare (safe-index-of order key1 default) (safe-index-of order key2 default)))

(defn sorted-map-by-vector
  "Creates a sorted map, with order defined. Elements not definied in the order
  will be added at the beginning of the sorted map."
  ([order]
   (sorted-map-by (partial compare-by-vector order)))
  ([order & keyvals]
   (apply sorted-map-by (partial compare-by-vector order) keyvals)))

(defn reorder-map
  "Given a map and an order, returns a map sorted by the order given in that
  vector."
  [m order]
  (into (sorted-map-by-vector order) m))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Shutdown hook management
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defonce ^:private hooks (atom {}))

(defn- run-hooks []
  (doseq [hook (vals @hooks)]
    (hook)))

(defonce ^:private bind-hooks!
         (delay (let [runtime (Runtime/getRuntime)]
                  (.addShutdownHook runtime (Thread. #'run-hooks))
                  :bound)))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; public api                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn add-hook!
  "Register a function to be called with no arguments when the
  application shuts down. `f` must be a function of no arguments, and
  `key` must be a unique."
  [key f]
  (force bind-hooks!)
  (when (contains? @hooks key)
    (throw (IllegalArgumentException. (str "Key " key " is already registered "
                                           "to another shutdown hook."))))
  (swap! hooks assoc key f)
  true)

(defn remove-hook!
  "De-registers the shutdown hook associated with `key`"
  [key]
  (if (contains? @hooks key)
    (let [hook (get @hooks key)]
      (swap! hooks dissoc key)
      true)
    false))



(defn hook-order!
  "Re-order the shutdown hooks."
  [order]
  (swap! hooks reorder-map order))

(defn registered-hooks
  "Lists all the currently registered shutdown hooks"
  []
  (keys @hooks))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; default shutdown hook
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn agents []
  (log/warn "Shutting down clojure agents.")
  (shutdown-agents))