(ns oizys.function)

(defn fn->symbol [fn-var]
  (let [fn-meta (meta fn-var)
        namespace-name (-> fn-meta :ns ns-name)
        function-name (:name fn-meta)]
    (symbol (str namespace-name "/" function-name))))
