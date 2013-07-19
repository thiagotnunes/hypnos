(ns oizys.checker
  (:require
   [oizys.function :as function]))

(declare equality-checker)

(def checkers {'=> (var equality-checker)})

(defn check [actual expected checker-fn]
  (let [evaluated-actual (eval actual)
        evaluated-expected (eval expected)]
    (if (checker-fn evaluated-actual evaluated-expected)
      true
      (throw (AssertionError. (str "Expected " actual " = " expected))))))

(defn equality-checker [actual expected]
  (= actual expected))

(defn symbol-for [checker]
  (-> checker
      checkers
      function/fn->symbol))
