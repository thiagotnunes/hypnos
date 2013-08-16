(ns oizys.checker)

(defn #^{:oizys-checker-fn true} equal [actual expected]
  (fn []
    (= actual expected)))

(defn #^{:oizys-checker-fn true} truthy [actual]
  (fn []
    actual))

(defn #^{:oizys-checker-fn true} falsey [actual]
  (fn []
    (not actual)))

(defmacro defchecker [name params & form]
  `(defn ~(with-meta name (assoc (meta name) :oizys-checker-fn true)) ~params
     (fn []
       ~@form)))
