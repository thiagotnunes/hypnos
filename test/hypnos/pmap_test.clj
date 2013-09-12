(ns hypnos.pmap-test
  (:require
   [hypnos.core :refer :all]))

(defn my-func [x]
  (+ x 2))

(defn my-func-caller [data]
  (pmap my-func data))

(defn do-something []
  (my-func-caller [1 2 3]))

(future-fact "Test some pcalls"
  (provided [(my-func 1) 2
             (my-func 2) 4
             (my-func 3) 6]
    (do-something) => [2 4 6]))

(future-fact "Test some pcalls 2"
  (provided [(my-func 1) 2
             (my-func 2) 4
             (my-func 3) 6]
    (do-something) => [2 4 6]))

(future-fact "Test some pcalls 3"
  (provided [(my-func 1) 2
             (my-func 2) 4
             (my-func 3) 6]
    (do-something) => [2 4 6]))



