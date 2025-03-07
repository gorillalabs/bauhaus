(ns gorillalabs.bauhaus.http.problem-test
  (:require [clojure.test :refer :all]
            [gorillalabs.bauhaus.http.problem :as p]
            [muuntaja.middleware :as mm]
            [ring.middleware.content-type :as ct]))


(deftest a-test
  (are [e k] (= e (k ((ct/wrap-content-type (mm/wrap-format-response (constantly (p/response (p/forbidden))))) {})))
             403 :status
             "application/json; charset=utf-8" (comp #(get % "Content-Type") :headers)))