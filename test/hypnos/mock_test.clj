(ns hypnos.mock.core-test
  (:require
   [hypnos.core :refer :all]))

(defn- one [] 1)
(defn- two [] 2)

(fact "mocks argumentless function within the scope"
  (provided [(one) 2
             (two) 3]
    (one) => 2
    (one) => 2
    (two) => 3)
  (one) => 1
  (two) => 2)
