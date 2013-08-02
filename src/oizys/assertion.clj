(ns oizys.assertion)

(declare expected-assertion)

(def assertions {'=> #'expected-assertion})

(defn confirm [actual expected assertion expression]
  (let [assertion-fn (assertions assertion)
        metadata (meta assertion)]
    (when-not (assertion-fn actual expected)
      {:actual actual
       :expected expected
       :expression expression
       :assertion assertion
       :line (:line metadata)
       :namespace (ns-name *ns*)})))

(defn expected-assertion [actual expected]
  (= actual expected))
