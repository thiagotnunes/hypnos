(ns oizys.checkers.core-test
  (:require
   [oizys.core :refer :all]))

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

(defchecker zero? [actual]
  (= actual 0))

(facts "about custom checkers"
  (fact "success case"
    0 => zero?)

  (failing-fact "some failing cases"
    1 => zero?
    10 => zero?))
