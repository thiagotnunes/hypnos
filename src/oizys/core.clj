(ns oizys.core)

(declare equality-checker
         parse-head
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
   (third parsed-expression)
   (checker-symbol-for (second parsed-expression))])

(defn- parse-head [body]
  (let [head (first body)
        tail (rest body)]
    (if (list? head)
      (concat [(parse-expressions head)] tail)
      body)))

(defn- parse-expressions [body]
  (if (seq body)
    (let [parsed-body (parse-head body)
          head (first parsed-body)
          checker (second parsed-body)]
      (if (= checker '=>)
        (let [expression (take 3 parsed-body)
              context (context expression)
              check-fn (->symbol (var check))]
          (concat [(list 'apply check-fn context)] (parse-expressions (drop 3 parsed-body))))
        (concat [head] (parse-expressions (rest parsed-body)))))
    []))

(defn- parse-fact [body]
  (conj (parse-expressions body) 'do))

(defmacro fact [description & body]
  (parse-fact body))
