(ns hypnos.checkers.core-test
  (:require
   [hypnos.core :refer :all]))

(facts "about truthy checker"
  (fact "anything but nil and false are truthy"
    1 => truthy
    () => truthy
    true => truthy)

  (failing-fact "nil and false are not truthy"
    nil => truthy
    false => truthy))

(facts "about falsey checker"
  (fact "nil and false are falsey"
    nil => falsey
    false => falsey)

  (failing-fact "anything but nil and false are not falsey"
    1 => falsey
    () => falsey
    true => falsey))

(defchecker zero? [actual]
  (= actual 0))

(facts "about custom checkers"
  (fact "0 is zero"
    0 => zero?)

  (failing-fact "non 0 values are not zero"
    1 => zero?
    10 => zero?))

(fact "parallel mapping works within a fact"
  (letfn [(my-func [x] (+ x 2))
          (my-func-caller [data] (pmap my-func data))
          (do-something [] (my-func-caller [1 2 3]))]
    (do-something) => [3 4 5]))

(facts "about exceptions"
  (fact "is successful when exception is of the same type"
    (letfn [(bang! [] (throw (RuntimeException. "bang")))]
      (bang!) => (throws RuntimeException)))

  (fact "is sucessful when the exception is instance of given one"
    (letfn [(boom! [] (throw (IllegalStateException. "boom")))]
      (boom!) => (throws Exception)))

  (failing-fact "fails when exceptions are not related"
    (letfn [(pow! [] (throw (IllegalStateException. "boom")))]
      (pow!) => (throws IllegalAccessError))))

(facts "roughly"
  (fact "values are within the given deltas from expected"
    0.11 => (roughly 0.1)
    0.09 => (roughly 0.1)
    0.3 => (roughly 0.1 0.2))

  (failing-fact "values are not within the given deltas"
    0.21 => (roughly 0.1)
    0.11 => (roughly 0.1 0.001)
    0.09 => (roughly 0.1 0.001)))

(fact "not checker negates the checker result"
  nil => (not truthy)
  1 => (not falsey)
  1 => (not zero?))
