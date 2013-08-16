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
  (+ 1 2) => 3)

(fact "nested expressions test"
  (let [a 1
        b 2]
    (+ a a) => b))

(fact "namespace resolution on actual"
  (calculator/plus 1 1) => 2)

(let [a 1
      c 3]
  (let [b 2]
    (fact "testing about scoping"
      a => (- c b)
      1 => 1)))

(facts "about first level nesting"
  (let [x 1]
    (facts "about second level nesting"
      (let [y 2]                 
        (fact "the actual test"
          (let [z 3]
            (+ x y) => z))))))

(facts "about first level nesting"
  (let [x 1]
    (facts "about second level nesting"
      (let [y 2]                 
        (failing-fact "the actual failing test"
          (let [z 3]
            (+ x z) => y))))))

(facts "nested future facts"
  (future-fact "this should not be evaluated"
               (let [x 1]
                 (+ x 3) => 2
                 (throw Exception. "ERROR"))))

(facts "about truthy checker"
  (fact "some success cases"
    1 => truthy
    () => truthy
    true => truthy)
  
  (failing-fact "truthy checker failing cases"
    nil => truthy
    false => truthy))

(facts "about falsey checker"
  (fact "success cases"
    nil => falsey
    false => falsey)

  (failing-fact "some failing cases"
    1 => falsey
    () => falsey
    true => falsey))
