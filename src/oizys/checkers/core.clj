(ns oizys.checkers.core)

(defmacro defchecker [name params & form]
  `(defn ~(with-meta name (assoc (meta name) :oizys-checker-fn true)) ~params
     (fn []
       ~@form)))

(def ^{:oizys-matcher :anything} _ (proxy [Object] [] (equals [_] true)))

(defchecker equal [actual expected]
  (= actual expected))

(defchecker truthy [actual]
  actual)

(defchecker falsey [actual]
  (not actual))
