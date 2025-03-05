(ns gorillalabs.collections-test
  (:require [clojure.test :refer :all]
            [expectations.clojure.test
             :refer [defexpect expect expecting
                     approximately between between' functionally
                     side-effects]]
            [gorillalabs.collections :refer :all]))

(deftest assoc-if-adds-key-value-when-value-not-nil
  (expect {:a 1} (assoc-if {} :a 1)))

(deftest assoc-if-does-not-add-key-when-value-nil
  (expect {} (assoc-if {} :a nil)))

(deftest assoc-if-adds-multiple-key-values
  (expect {:a 1 :b 2} (assoc-if {} :a 1 :b 2)))

(deftest assoc-if-throws-when-odd-number-of-arguments
  (expect IllegalArgumentException (assoc-if {} :a 1 :b)))

(deftest keys-in-returns-all-key-paths
  (expect [[:a] [:a :b] [:a :c] [:d]] (keys-in {:a {:b 1 :c 2} :d 3})))

(deftest deep-merge-merges-maps-deeply
  (expect {:a {:b 2 :c 3} :d 4} (deep-merge {:a {:b 1 :c 3} :d 4} {:a {:b 2}})))

(deftest deep-merge-overwrites-non-map-values
  (expect {:a 2} (deep-merge {:a 1} {:a 2})))