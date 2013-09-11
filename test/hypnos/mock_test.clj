(ns hypnos.mock.core-test
  (:require
   [hypnos.core :refer :all]))

(defn- one [] 1)

(fact "mocks argumentless function within the scope"
  (provided [(one) 2]
    (one) => 2
    (one) => 2)
  (one) => 1)
