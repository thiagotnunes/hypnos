(ns oizys.core)

(declare equality-checker)

(def checkers {'=> (var equality-checker)})

(defn check [actual expected checker-fn]
  (let [evaluated-actual (eval actual)
        evaluated-expected (eval expected)]
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

(defn- checker-symbol-for [checker]
  (-> checker
      checkers
      ->symbol))

(defn- context [parsed-expression]
  [(first parsed-expression)
   (nth parsed-expression 2)
   (checker-symbol-for (second parsed-expression))])

(defn- resolve-fact [body]
  (if (not (empty? body))
    (let [head (first body)
          checker (second body)]
      (if (= checker '=>)
        (let [expression (take 3 body)
              context (context expression)
              check-fn (->symbol (var check))]
          (concat [(list 'apply check-fn context)] (resolve-fact (drop 3 body))))
        (concat [head] (resolve-fact (rest body)))))
    []))

(defmacro fact [description & body]
  (conj (resolve-fact body) 'and))
