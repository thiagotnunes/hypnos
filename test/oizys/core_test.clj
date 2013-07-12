(ns oizys.core-test
  (:require
   [oizys.core :refer :all]))

(fact "these should pass"
      1 => 1
      (+ 1 1) => 2
      2 => (+ 1 1))
