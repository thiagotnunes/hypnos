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
