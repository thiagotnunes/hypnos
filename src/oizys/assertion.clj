(ns oizys.assertion)

(declare expected-assertion)

(def assertions {'=> #'expected-assertion})

(defn confirm [actual expected assertion-fn line]
  (let [evaluated-actual (eval actual)
        evaluated-expected (eval expected)]
    (when-not (assertion-fn evaluated-actual evaluated-expected)
      {:actual actual
       :expected expected
       :line line})))

(defn expected-assertion [actual expected]
  (= actual expected))
