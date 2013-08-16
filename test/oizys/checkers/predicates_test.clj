(ns oizys.checkers.predicates
  (:require
   [oizys.core :refer :all]))

(future-fact "verifies all elements of the list"
  [1 3 5] => (has every? odd?))

(future-fact "verifies not any element of the list"
  [1 3 5] => (has not-any? even?))

(future-fact "verifies some elements of the list"
  [1 2 3] => (has some? odd?))

(future-fact "verifies not every element of the list"
  [1 2 3] => (has not-every? odd?))
