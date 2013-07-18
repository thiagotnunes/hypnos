(ns oizys.core-test
  (:require
   [oizys.core :refer :all]))

(fact "simple equality test"
      1 => 1)

(fact "simple expression test"
      (+ 1 1) => 2)

(fact "multiple assertions test"
      (+ 1 1) => 2
      2 => (+ 1 1))

(fact "nested expressions test"
      (let [a 1
            b 2]
        (+ a a) => b))

; ((let [a 1 b 2] (+ a a) => b))
; (eval (let [a 1 b 2] (check (+ a a) b))
