(ns gorillalabs.bauhaus.build.ksuid
  (:import (com.github.ksuid KsuidGenerator Ksuid)
           (java.security SecureRandom)))

(def generator (KsuidGenerator. (SecureRandom.)))

(defn new []
  (str (.newKsuid generator)))

(defn inspect [ksuid-str]
  (.toInspectString (Ksuid/fromString ksuid-str)))