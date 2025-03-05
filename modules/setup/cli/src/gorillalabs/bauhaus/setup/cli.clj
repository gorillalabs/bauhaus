(ns gorillalabs.bauhaus.setup.cli
  (:require [gorillalabs.bauhaus.setup.exit :as exit]
            [clojure.tools.cli :as tools.cli]
            [clojure.java.io :as io]))

(defn check-and-stop! [{options   :options
                        errors    :errors
                        arguments :arguments
                        summary   :summary
                        :as       opts}
                       out-fn]
  (when (or errors (:help options))
    (out-fn errors)
    (out-fn summary)
    (exit/exit 0)))

(def default-cli-options
  ;; An option with a required argument
  [["-c" "--config CONFIG-FILE" "Path to the config file"
    :default "config.edn"
    ; :parse-fn #(Integer/parseInt %)
    :validate [#(.canRead (io/as-file %)) "Cannot read config file"]]
   ;; A non-idempotent option (:default is applied first)
   #_["-v" nil "Verbosity level"
      :id :verbosity
      :default 0
      :update-fn inc]                                       ; Prior to 0.4.1, you would have to use:
   ;; :assoc-fn (fn [m k _] (update-in m [k] inc))
   ;; A boolean option defaulting to nil
   ["-h" "--help"]])

(defn init [cli-options & args]
  (let [{options :options
         arguments                              :arguments
         :as                                    opts}
        (tools.cli/parse-opts args cli-options)]
    (check-and-stop! opts println)
    options))

#_; Will print help and exit
        (init default-cli-options "--help")

#_; Will return a config-file
        (init default-cli-options "--config" "dev-resources/dev-config.edn")

#_; Will fail due to missing config-file
        (init default-cli-options "--config" "dev-resources/missing-config.edn")