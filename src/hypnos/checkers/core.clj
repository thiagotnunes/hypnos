(ns hypnos.checkers.core)

(defmacro defchecker [name params & form]
  `(defn ~(with-meta name (assoc (meta name) :hypnos-checker-fn true)) ~params
     (fn []
       ~@form)))

(defchecker equal [actual expected]
  (= actual expected))

(defchecker truthy [actual]
  actual)

(defchecker falsey [actual]
  (not actual))

(defmacro ^{:hypnos-checker-fn true} throws [actual expected]
  `(fn []
     (try
       ~actual
       false
       (catch Throwable e#
         (instance? ~expected e#)))))

(defchecker roughly [actual expected & [expected-error]]
  (let [error (or expected-error 0.1)]
    (and (>= actual (- expected error))
         (<= actual (+ expected error)))))
