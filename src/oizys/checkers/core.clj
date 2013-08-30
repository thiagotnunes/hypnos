(ns oizys.checkers.core)

(defmacro defchecker [name params & form]
  `(defn ~(with-meta name (assoc (meta name) :oizys-checker-fn true)) ~params
     (fn []
       ~@form)))

(defchecker equal [actual expected]
  (= actual expected))

(defchecker truthy [actual]
  actual)

(defchecker falsey [actual]
  (not actual))

(defmacro ^{:oizys-checker-fn true} throws [actual expected]
  `(fn []
     (try
       ~actual
       false
       (catch Throwable e#
         (instance? ~expected e#)))))
