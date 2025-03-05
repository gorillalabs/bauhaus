(ns user
  (:require [gorillalabs.bauhaus.setup.cli :as setup.cli]
            [taoensso.timbre :as log]))

(defn -main [& args]
  (let [{config-path :config} (apply setup.cli/init
                                     setup.cli/default-cli-options
                                     args)]
    (log/info "Config-path" config-path)))


#_; This will spit out help information and exit (if not inside a REPL)
        (-main "--help")

#_; This will read the config-path from the command-line option "-c / --config"
        (-main "--config" "dev-resources/dev-config.edn")

