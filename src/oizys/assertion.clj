(ns oizys.assertion)

(declare expected-assertion)

(def assertions {'=> #'expected-assertion})

(defn confirm [actual expected assertion-fn line]
  (let [evaluated-actual (eval actual)
        evaluated-expected (eval expected)]
    (let [assertion-result (assertion-fn evaluated-actual evaluated-expected)]
      (when-not (:success assertion-result)
        {:message (:message assertion-result)
         :actual actual
         :expected expected
         :line line}))))

(defn expected-assertion [actual expected]
  {:success (= actual expected)
   :message (format "  Expected: %s\n  Actual: %s" expected actual)})
