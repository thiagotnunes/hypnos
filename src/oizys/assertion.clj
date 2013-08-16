(ns oizys.assertion)

(declare expected-assertion)

(def assertions {'=> #'expected-assertion})

(defn confirm [checker-fn assertion expression]
  (let [assertion-fn (assertions assertion)
        metadata (meta assertion)]
    (when-not (assertion-fn checker-fn)
      {:expression expression
       :assertion assertion
       :line (:line metadata)
       :namespace (ns-name *ns*)})))

(defn refute [checker-fn assertion expression]
  (let [assertion-fn (assertions assertion)
        metadata (meta assertion)]
    (when (assertion-fn checker-fn)
      {:expression expression
       :assertion assertion
       :line (:line metadata)
       :namespace (ns-name *ns*)})))

(defn expected-assertion [checker-fn]
  (checker-fn))
