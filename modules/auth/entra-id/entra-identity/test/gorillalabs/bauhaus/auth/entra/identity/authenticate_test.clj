(ns gorillalabs.bauhaus.auth.entra.identity.authenticate-test
  (:require [gorillalabs.bauhaus.setup.logging :as setup.logging]
            [clojure.test :refer :all]
            [gorillalabs.bauhaus.auth.entra.identity.authenticate :as auth]
            [gorillalabs.bauhaus.auth.entra.identity.fixture.integrant :as fixture.integrant]))

(setup.logging/init-dev-logging)

(use-fixtures :once fixture.integrant/with-system)

(deftest test-using-azure
  (let [demo-provider {:app-id-url "api://gorillalabs.bauhaus/auth/entra/DemoProvider"}

        token (auth/acquire-token (:auth.entra/provider fixture.integrant/system)
                                  #{(str (:app-id-url demo-provider) "/.default")})]
    (is (string? token))))