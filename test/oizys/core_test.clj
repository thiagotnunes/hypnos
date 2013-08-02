(ns oizys.core-test
  (:require
   [oizys.core         :refer :all]
   [support.calculator :as calculator]))

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

(fact "namespace resolution on actual"
      (calculator/plus 1 1) => 2)

(fact "namespace resolution on expected"
      2 => (calculator/plus 1 1))


(let [a 1
      c 3]
  (let [b 2]
    (fact "testing about scoping"
          a => (- c b)
          1 => 1)))
