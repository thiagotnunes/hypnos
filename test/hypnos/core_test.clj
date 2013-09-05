(ns hypnos.core-test
  (:require
   [hypnos.core        :refer :all]
   [support.calculator :as calculator]))

(fact "1 is equal to 1"
  1 => 1)

(fact "1 plus 1 is equal to 2"
  (+ 1 1) => 2)

(fact "adding two numbers is equal to the sum of them"
  (+ 1 1) => 2
  (+ 1 2) => 3)

(fact "two collections are equal when they have the same elements"
  '(1 2 3) => '(1 2 3)
  [1 2 3]  => [1 2 3]
  #{1 2 3} => #{1 2 3}
  {1 2}    => {1 2})

(fact "facts accept scoping"
  (let [a 1
        b 2]
    (+ a a) => b))

(fact "facts correctly resolve the namespace on it's body"
  (calculator/plus 1 1) => 2)

(let [a 1
      c 3]
  (let [b 2]
    (fact "facts correctly resolve multiple scoping"
      a => (- c b)
      1 => 1)))

(facts "about first level nesting"
  (let [x 1]
    (facts "about second level nesting"
      (let [y 2]                 
        (fact "facts can be nested within several facts"
          (let [z 3]
            (+ x y) => z))))))

(facts "about first level nesting"
  (let [x 1]
    (facts "about second level nesting"
      (let [y 2]                 
        (failing-fact "adding two numbers do not output the expected result"
          (let [z 3]
            (+ x z) => y))))))

(facts "nested future facts"
  (future-fact "this is not evaluated"
    (let [x 1]
      (+ x 3) => 2
      (throw Exception. "ERROR"))))
