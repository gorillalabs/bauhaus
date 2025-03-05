(ns gorillalabs.bauhaus.setup.shutdown-test
  (:require [clojure.test :refer :all]
            [gorillalabs.bauhaus.setup.shutdown :as setup.shutdown]
            [expectations.clojure.test
             :refer [defexpect expect expecting
                     approximately between between' functionally
                     side-effects]]))

(defn- clear-hooks []
  (doseq [hook (setup.shutdown/registered-hooks)]
    (setup.shutdown/remove-hook! hook)))

#_;
        (clear-hooks)

(deftest hook!-test
  ;; Fail if we're not starting with an empty hook list
  ;; Do not run in production-like REPL
  ;; If hooks are not empty (e.g., from prior tests),
  ;; use (clear-hooks)
  (expect nil? (setup.shutdown/registered-hooks))

  (setup.shutdown/add-hook! :myhook-1 (fn []))
  (expect [:myhook-1] (setup.shutdown/registered-hooks))

  (setup.shutdown/add-hook! :myhook-2 (fn []))
  (expect #{:myhook-1 :myhook-2} (set (setup.shutdown/registered-hooks)))

  (setup.shutdown/hook-order! [:myhook-2 :myhook-1])
  (expect [:myhook-2 :myhook-1] (setup.shutdown/registered-hooks))

  (setup.shutdown/remove-hook! :myhook-2)
  (setup.shutdown/remove-hook! :myhook-1)
  )