(ns oizys.checkers.core-test
  (:require
   [oizys.core :refer :all]))

(facts "about truthy checker"
  (fact "some success cases"
    1 => truthy
    () => truthy
    true => truthy)
  
  (lie "truthy checker failing cases"
    nil => truthy
    false => truthy))

(facts "about falsey checker"
  (fact "success cases"
    nil => falsey
    false => falsey)

  (lie "some failing cases"
    1 => falsey
    () => falsey
    true => falsey))

(defchecker zero? [actual]
  (= actual 0))

(facts "about custom checkers"
  (fact "success case"
    0 => zero?)

  (lie "some failing cases"
    1 => zero?
    10 => zero?))

(fact "parallel execution"
  (letfn [(my-func [x] (+ x 2))
          (my-func-caller [data] (pmap my-func data))
          (do-something [] (my-func-caller [1 2 3]))]
    (do-something) => [3 4 5]))

(facts "about exceptions"
  (fact "is successful when the exception is of the same type"
    (letfn [(bang! [] (throw (RuntimeException. "bang")))]
      (bang!) => (throws RuntimeException)))

  (fact "is sucessful when the exception is instance of given one"
    (letfn [(boom! [] (throw (IllegalStateException. "boom")))]
      (boom!) => (throws Exception)))

  (lie "exceptions mismatching"
    (letfn [(pow! [] (throw (IllegalStateException. "boom")))]
      (pow!) => (throws IllegalAccessError))))

(facts "roughly"
  (fact "checks if value is within the tolerance"
    0.11 => (roughly 0.1)
    0.09 => (roughly 0.1)
    0.3 => (roughly 0.1 0.2))

  (lie "surpasses given tolerance"
    0.21 => (roughly 0.1)
    0.11 => (roughly 0.1 0.001)
    0.09 => (roughly 0.1 0.001)))

(fact "negating checkers"
  nil => (not truthy)
  1 => (not falsey)
  1 => (not zero?))
