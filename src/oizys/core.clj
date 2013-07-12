(ns oizys.core)

(declare equality-checker)

(def checkers {'=> (var equality-checker)})

(defn- present? [element coll]
  (when (coll? coll)
        (some #{element} coll)))

(defn check [actual expected checker-fn]
  (let [evaluated-actual (evaluate actual)
        evaluated-expected (evaluate expected)]
    (if (checker-fn evaluated-actual evaluated-expected)
      true
      (throw (AssertionError. (str "Expected " actual " = " expected))))))

(defn equality-checker [actual expected]
  (= actual expected))

(defn ->symbol [fn-var]
  (let [fn-meta (meta fn-var)
        namespace-name (-> fn-meta :ns ns-name)
        function-name (:name fn-meta)]
    (symbol (str namespace-name "/" function-name))))

(defn- parse-expression [body]
  (let [index-of-checker (.indexOf body '=>)
        check-start-position (- index-of-checker 1)]
    (split-at check-start-position body)))

(defn- statements [parsed-expression]
  (->> parsed-expression
       second
       (remove #{'=>})
       (into [])))

(defn- checker [parsed-expression]
  (->> parsed-expression
       second
       second))

(defn- context [parsed-expression]
  (->> parsed-expression
       first))

(defn- checker-symbol-for [checker]
  (-> checker
      checkers
      ->symbol))

(defn- resolve-expression [expression]
  (if (present? '=> expression)
    (let [parsed-expression (parse-expression expression)
          context (context parsed-expression)
          checker (checker parsed-expression)
          statements (statements parsed-expression)
          checker-args (conj statements (checker-symbol-for checker))]
      (concat context [(list 'apply (->symbol (var check)) checker-args)]))
    expression))

(defn- resolve-fact [body]
  (let [result (resolve-expression body)]
    (map resolve-expression result)))

(defmacro fact [description & body]
  (first (resolve-fact body)))
