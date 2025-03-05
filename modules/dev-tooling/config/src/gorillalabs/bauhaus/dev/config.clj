(ns gorillalabs.bauhaus.dev.config
  (:require [clojure.tools.namespace.repl :as ns.repl]
            [aero.core :as aero]
            [taoensso.timbre :as log]))

; Behind the scenes, Integrant-REPL uses tools.namespace. You can set the
; directories that are monitored for changed files by using the refresh-dirs
; function:
(defn init []
  (ns.repl/set-refresh-dirs "src" "dev" "test"))

(defn deep-merge
      "2-arity deep merge, see https://gist.github.com/danielpcox/c70a8aa2c36766200a95?permalink_comment_id=2759497#gistcomment-2759497"
      [a b]
      (if (map? a)
        (merge-with deep-merge a b)
        b))

(defn config []
      (log/debug "Read dev config")
      (deep-merge
        (when-let [config (clojure.java.io/resource "config.edn")]
                  (aero/read-config config))
        (when-let [config (clojure.java.io/resource "dev-config.edn")]
                  (aero/read-config config))))

