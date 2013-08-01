(ns oizys.assertion)

(declare expected-assertion)

(def assertions {'=> #'expected-assertion})

(defn confirm [actual expected assertion-fn metadata]
  (let [evaluated-actual (eval actual)
        evaluated-expected (eval expected)]
    (let [assertion-result (assertion-fn evaluated-actual evaluated-expected)]
      (when-not (:success assertion-result)
        {:message (:message assertion-result)
         :actual actual
         :expected expected
         :line (:line metadata)
         :namespace (ns-name *ns*)}))))

(defn expected-assertion [actual expected]
  {:success (= actual expected)
   :message (format "  Expected: %s\n  Actual: %s" expected actual)})
