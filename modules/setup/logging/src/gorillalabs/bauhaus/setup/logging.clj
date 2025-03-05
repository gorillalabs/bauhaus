(ns gorillalabs.bauhaus.setup.logging
  (:require [clojure.core.server]
            [taoensso.timbre :as log]
            [taoensso.timbre.tools.logging]
            [taoensso.timbre.appenders.community.rotor :as log.appenders.rotor]
            [timbre-json-appender.core :as tas]))

(defn log-config [rotating-logfile]
  {:min-level :info
   :ns-filter {:deny #{"org.eclipse.jetty.util.*"
                       "org.eclipse.jetty.io.*"
                       #_"org.apache.http.*"}
               #_#_:allow #{"*"}}
   :appenders {:json (dissoc (log.appenders.rotor/rotor-appender {:path     rotating-logfile
                                                                  :max-size (* 10 1024 1024)
                                                                  :backlog  5})
                             :output-fn
                             :min-level)}})

#_;
        (log/merge-config! {:min-level :warn
                            :ns-filter {:deny #{"org.apache.http.*"}
                                        #_#_:allow #{"*"}}
                            })

(defn uncaught-exception-handler
  "This function handles uncaught exceptions in the JVM.
   It logs the exception with an error level log message,
   including the name of the thread where the exception occurred.
   After logging, it exits the application with a non-zero exit code (-1)."
  [throwable ^Thread thread]
  (log/errorf throwable "Uncaught exception on thread: %s"
              (.getName thread))
  (System/exit -1))

(def dev-config {:min-level :debug
                 :ns-filter {:deny #{"org.eclipse.jetty.*"
                                     #_"org.apache.http.*"}
                             #_#_:allow #{"*"}}
                 :appenders {:println (log/println-appender {:stream :auto})
                             :json    nil}})

(defn init-logging [config]
  (tas/install {:should-log-field-fn (constantly true)
                :ex-data-field-fn    (fn [f]
                                       (if (instance? java.io.Serializable f)
                                         f
                                         {::unserializable (type f)}))})
  (log/merge-config! config)
  (log/handle-uncaught-jvm-exceptions! uncaught-exception-handler)
  (taoensso.timbre.tools.logging/use-timbre))



(defn init-dev-logging
  ([]
   (init-dev-logging {})
   )
  ([config]
   (log/merge-config! dev-config)
   (log/merge-config! config)
   (log/handle-uncaught-jvm-exceptions!)
   (taoensso.timbre.tools.logging/use-timbre)))

#_;
        (init-logging (log-config "../pricelist-consumer.logs/pricelist-consumer.log"))