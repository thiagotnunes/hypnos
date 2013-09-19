(ns hypnos.parser.assertion
  (:require
   [hypnos.parser.checker :as checker]
   [hypnos.assertion      :as assertion]
   [hypnos.result         :as result]

   [velcro.core :refer :all]
   [potemkin    :as potemkin]))

(defn- assertion->function [assertion-fn
                            errors
                            actual
                            expected
                            assertion-symbol]
  `(~assertion-fn ~errors
                  ~(checker/checker->function actual expected)
                  '~assertion-symbol
                  '(~actual ~assertion-symbol ~expected)))

(defn assertion->confirm [errors]
  (partial assertion->function #'assertion/confirm errors))

(defn assertion->refute [errors]
  (partial assertion->function #'assertion/refute errors))

(defn assertions->confirms [errors]
  (fn [form]
    (replace-in form
                [left-node right-node current-node]
                (by (assertion->confirm errors))
                (where #(assertion/assertions (current-node %))))))

(defn assertions->refutes [errors]
  (fn [form]
    (replace-in form
                [left-node right-node current-node]
                (by (assertion->refute errors))
                (where #(assertion/assertions (current-node %))))))
