(ns hypnos.assertion)

(declare expected-assertion)

(def assertions {'=> #'expected-assertion})

(defn confirm [errors checker-fn assertion expression]
  (let [assertion-fn (assertions assertion)
        metadata (meta assertion)]
    (when-not (assertion-fn checker-fn)
      (swap! errors conj {:type :confirm
                          :expression expression
                          :assertion assertion
                          :line (:line metadata)
                          :namespace (ns-name *ns*)}))))

(defn refute [errors checker-fn assertion expression]
  (let [assertion-fn (assertions assertion)
        metadata (meta assertion)]
    (when (assertion-fn checker-fn)
      (swap! errors conj {:type :refute
                          :expression expression
                          :assertion assertion
                          :line (:line metadata)
                          :namespace (ns-name *ns*)}))))

(defn expected-assertion [checker-fn]
  (checker-fn))
