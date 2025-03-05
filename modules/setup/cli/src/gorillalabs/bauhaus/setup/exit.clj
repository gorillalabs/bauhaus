(ns gorillalabs.bauhaus.setup.exit
  "Provides helper functionality to implement a REPL-save exit."
  (:require [taoensso.timbre :as log]))

(defn- current-stack-trace []
  (.getStackTrace (Thread/currentThread)))

(defn- is-repl-stack-element [stack-element]
  (and (= "clojure.main$repl" (.getClassName stack-element))
       (= "doInvoke" (.getMethodName stack-element))))

(def is-in-repl
  "Little helper function return true iff we're operating in a repl."
  (memoize (fn []
             (some is-repl-stack-element (current-stack-trace)))))

(defn exit
  "Replacement for System/exit, not exiting if inside the main REPL thread.
  We usually do not want our REPL to be exited the same way we exit a running service."
  [^Integer status]
  (if-not (is-in-repl)
    (System/exit (if (int? status) status 0))
    (let [t (Error. (str "If not inside a REPL, system would exit here with status " status))]
      (log/fatal t
                 ""
                 {:status :exit
                  :code   status})
      (throw t))))
