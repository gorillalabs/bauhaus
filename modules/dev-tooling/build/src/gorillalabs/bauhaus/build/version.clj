(ns gorillalabs.bauhaus.build.version
  (:require [garamond.git :as git]
            [cuddlefish.core :as cf]
            [gorillalabs.bauhaus.build.ksuid :as ksuid]
            [taoensso.timbre :as log]))


(defn- format-application-build
  "Starts with ksuid to keep sortablity.
  Followed by branch and short ref to identify commit
  (and branch context).
  Information from Version-Tag (e.g., use [garamond](https://github.com/workframers/garamond)
  to push the version tag), together with information on number of commits ahead
  (just for information, short-ref is enough to identify).
  'dirty' signals a dirty git status on the build machine, can easily happen on
  developer local machine. Should be avoided with production code."
  [{version                                 :version
    {:keys [ahead ahead? dirty? ref-short]} :git}
   branch
   ksuid]
  (str ksuid
       "-" branch
       "-" ref-short
       "-"
       version
       (if ahead? (str "+" ahead) "")
       (if dirty? "-dirty" "")))

(defn application-build-string []
  (let [v (try (git/current-status)
               (catch Exception e
                 (log/warn e "Error: Unable to retrieve git version information.")
                 {:version "no-named-version" :git nil :current nil :prefix nil}
                 ))
        branch (cf/current-branch git/cf-config)
        id (ksuid/new)]
    (format-application-build v branch id)))


#_;
        ;; No tag: will log a warning
        (application-build-string)


