(ns oizys.core)

(declare equality-checker
         parse-expressions)

(def checkers {'=> (var equality-checker)})

(defn- third [coll]
  (nth coll 2))

(defn check [actual expected checker-fn]
  (let [evaluated-actual (eval actual)
        evaluated-expected (eval expected)]
    (if (checker-fn evaluated-actual evaluated-expected)
      true
      (throw (AssertionError. (str "Expected " actual " = " expected))))))

(defn equality-checker [actual expected]
  (= actual expected))

(defn fn->symbol [fn-var]
  (let [fn-meta (meta fn-var)
        namespace-name (-> fn-meta :ns ns-name)
        function-name (:name fn-meta)]
    (symbol (str namespace-name "/" function-name))))

(defn- checker-symbol-for [checker]
  (-> checker
      checkers
      fn->symbol))

(defn- parse-checker-expression [[head checker tail]]
  (let [check-fn (fn->symbol (var check))]
    (list 'apply check-fn [head tail (checker-symbol-for checker)])))

(defn- parse-head [[head & _]]
  (if (list? head)
    (parse-expressions head)
    head))

(defn- parse-tail [[_ & tail]]
  (parse-expressions tail))

(defn- parse-expressions [body]
  (if (seq body)
    (let [head (parse-head body)
          tail (rest body)
          expressions (cons head tail)
          checker (second expressions)]
      (if (= checker '=>)
        (cons (parse-checker-expression (take 3 expressions))
                (parse-expressions (drop 3 expressions)))
        (cons head (parse-tail expressions))))
    []))

(defn- parse-fact [body]
  (cons 'do (parse-expressions body)))

(defmacro fact [description & body]
  (parse-fact body))
