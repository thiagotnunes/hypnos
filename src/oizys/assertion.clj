(ns oizys.assertion
  (:require
   [oizys.function :as function]))

(declare equality-assertion)

(def assertions {'=> (var equality-assertion)})

(defn assert [actual expected checker-fn]
  (let [evaluated-actual (eval actual)
        evaluated-expected (eval expected)]
    (if (checker-fn evaluated-actual evaluated-expected)
      true
      (throw (AssertionError. (str "Expected " actual " = " expected))))))

(defn equality-assertion [actual expected]
  (= actual expected))

(defn symbol-for [assertion]
  (-> assertion
      assertions
      function/fn->symbol))
