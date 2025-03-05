(ns build
  "Example build script."
  (:require [gorillalabs.bauhaus.setup.logging :as setup.logging]
            [taoensso.timbre :as log]
            [clojure.tools.build.api :as b]
            [gorillalabs.bauhaus.build.version :as version]))

(setup.logging/init-dev-logging {:min-level
                                 [[#{"org.apache.*"} :warn]
                                  [#{"org.eclipse.*"} :warn]
                                  [#{"*"} :info]]})

(def build-conf
  (let [target-name 'gorillalabs.bauhaus.example/app
        target "target"]
    {:name      target-name
     :build     (version/application-build-string)
     :target    target
     :prep-dir  (str target "/prep")
     :class-dir (str target "/classes")
     :basis     (b/create-basis {:project "deps.edn"})
     :main      'example.app
     :jar-file  (format "%s/%s-%s.jar" target (name target-name) build)
     :uber-file (format "%s/%s-%s-standalone.jar" target (name target-name) build)
     }))

(comment
  "What I want to happen

  I want to be able to deploy each service (here: a single deployable artefact)
  to an EC2 instance, a lambda, a docker container, and thus build an uberjar.

  - Deployment should be easy (i.e. only very few steps) and automatable.
    -> Build a deployable artefact (e.g., an uberjar).
  - It should be easy to distinguish two different versions of one artefact.
    -> Name each result of the build step (include a timestamp-based id, in our
       case a [KSUID](https://segment.com/blog/a-brief-history-of-the-uuid/)).
  - It should be possible (to a certain extend of course) to relate a deployed
    artefact to the version-controlled source code, so the build info string
    should also contain git version information.
  ")


(comment
  (def target-name 'gorillalabs.bauhaus.example/app)
  (def build (version/application-build-string))
  (def prep-dir "target/prep")
  (def class-dir "target/classes")
  (def basis (b/create-basis {:project "deps.edn"}))
  (def jar-file (format "target/%s-%s.jar" (name target-name) build))
  (def uber-file (format "target/%s-%s-standalone.jar" (name target-name) build))
  )


(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (b/copy-file {:src    "deps.edn"
                :target (str (:prep-dir build-conf) "/deps.edn")})
  (b/copy-dir {:src-dirs   ["src"]
               :target-dir (str (:prep-dir build-conf) "/src")})
  (b/copy-dir {:src-dirs   ["resources"]
               :target-dir (str (:prep-dir build-conf) "/resources")})
  (b/jar {:class-dir (:prep-dir build-conf)
          :jar-file  (:jar-file build-conf)}))

(defn uber [_]
  (log/info "Hey, happy to build things for you.")
  (log/info "Let me clean stuff first.")
  (clean nil)
  (log/info "All fresh and clean. Start moving things.")
  (b/copy-dir {:src-dirs   ["src" "resources"]
               :target-dir (:class-dir build-conf)})
  (log/info "I'm ready compile for you. Will take some time (approx. 1 min) to finish.")
  (b/compile-clj {:basis     (:basis build-conf)
                  :src-dirs  ["src"]
                  :class-dir (:class-dir build-conf)})
  (log/info "Now I'm packaging your dependencies in an uberjar. This might take a while (approx. 3 mins).")
  (b/uber {:class-dir (:class-dir build-conf)
           :uber-file (:uber-file build-conf)
           :exclude   ["META-INF/license/LICENSE.*\\.txt"]
           :basis     (:basis build-conf)
           :main      (:main build-conf)})
  (log/info (str "I'm done. Feel free to work with the result you can find at " (:uber-file build-conf))))

#_;
        (clean nil)
#_;
        (uber nil)