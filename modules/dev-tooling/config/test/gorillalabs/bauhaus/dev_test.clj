(ns gorillalabs.bauhaus.dev-test
  (:require [clojure.test :refer :all]
            [gorillalabs.bauhaus.dev.config :refer :all]))


(deftest deep-merge-test
  (testing "nil is a neutral element"
    (are [a] (= (deep-merge a nil) a)
             nil
             {}
             {:a 1}
             {:a {:b 1}})

    ;; that goes both ways
    (are [a] (= (deep-merge nil a) a)
             nil
             {}
             {:a 1}
             {:a {:b 1}}))

  (testing "empty map is a neutral element (except for nil)"
    (are [a] (= (deep-merge a {}) a)
             {}
             {:a 1}
             {:a {:b 1}})

    ;; that goes both ways
    (are [a] (= (deep-merge {} a) a)
             {}
             {:a 1}
             {:a {:b 1}})))