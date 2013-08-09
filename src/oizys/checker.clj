(ns oizys.checker)

(defn #^{:checker-fn true} equal [actual expected]
  (fn []
    (= actual expected)))

(defn #^{:checker-fn true} truthy [actual]
  (fn []
    actual))
