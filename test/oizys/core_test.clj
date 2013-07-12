(ns oizys.core-test
  (:require
   [oizys.core :refer :all]))

(fact "1 should be 1"
      1 => 1)

(fact "1 + 1 should be 2"
      (+ 1 1) => 2)

(fact "2 should be 1 + 1"
      2 => (+ 1 1))
