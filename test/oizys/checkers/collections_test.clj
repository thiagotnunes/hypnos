(ns oizys.checkers.collections-test
  (:require
   [oizys.core :refer :all]))

(facts "about contains"
  (fact "for vectors"
    [1 2 3] => (contains 2)
    [1 2 3] => (contains [1 _ 3])
    [1 2 3] => (contains [1 _ _])
    [1 2 3] => (contains [_ _ _]))

  (failing-fact "for vectors"
    [1 2 3] => (contains 4)
    [1 2 3] => (contains [1 2])
    [1 2 3] => (contains [_ 2 4])))
