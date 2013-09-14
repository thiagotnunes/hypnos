(ns hypnos.mock.core-test
  (:require
   [hypnos.core        :refer :all]
   [support.calculator :as calc]))

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

(defn with-args [& args]
  (concat [:with-args] args))

(fact "mocks function with the given arguments"
  (provided [(with-args 1) [:mocked-with-1]
             (with-args 2) [:mocked-with-2]]
    (with-args 1) => [:mocked-with-1]
    (with-args 2) => [:mocked-with-2]))

(fact "mocks function with the given arguments"
  (provided [(with-args 1) [:mocked-with-1]
             (with-args 2) [:mocked-with-2]]
    (with-args 1) => [:mocked-with-1]
    (with-args 2) => [:mocked-with-2])
  (with-args 2) => [:with-args 2])

(fact "mock function in another namespace"
  (provided [(calc/plus 2 2) 5]
    (calc/plus 2 2) => 5))
