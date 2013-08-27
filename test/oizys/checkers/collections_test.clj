(ns oizys.checkers.collections-test
  (:require
   [oizys.core :refer :all]))

(facts "about contains"
  (fact "for vectors"
    [1 2 3] => (matches [1 2 3])
    [1 2 3] => (matches [1 _ 3])
    [1 2 3] => (matches [1 _ _])
    [1 2 3] => (matches [_ _ _]))

  (lie "for vectors"
    [1 2 3] => (matches [1 2 3 4])
    [1 2 3] => (matches [1 2 4])
    [1 2 3] => (matches [1 _ 4])
    [1 2 3] => (matches [_ _ 4])))
