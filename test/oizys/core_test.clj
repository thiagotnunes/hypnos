(ns oizys.core-test
  (:require
   [oizys.core :refer :all]))

(fact "this should pass"
      1 => 1)

(fact "this should fail"
      1 => 2)
