(ns oizys.core
  (:require
   [clojure.zip :as zip]
   [clojure.algo.monads :refer [domonad maybe-m]]))

(declare parse-expressions)
(declare expected-assertion)

(defn- fn->symbol [fn-var]
  (let [fn-meta (meta fn-var)
        namespace-name (-> fn-meta :ns ns-name)
        function-name (:name fn-meta)]
    (symbol (str namespace-name "/" function-name))))

(def assertions {'=> #'expected-assertion})

(defn assertion [actual expected assertion-fn]
  (let [evaluated-actual (eval actual)
        evaluated-expected (eval expected)]
    (if (assertion-fn evaluated-actual evaluated-expected)
      true
      (throw (AssertionError. (str "Expected " actual " = " expected))))))

(defn expected-assertion [actual expected]
  (= actual expected))

(defn- symbol-for [assertion]
  (-> assertion
      assertions
      fn->symbol))

(defn- has-assertion? [expressions]
  (assertions (second expressions)))

(defn- parse-assertion [[actual assertion expected]]
  (let [assertion-fn (fn->symbol #'assertion)]
    (list 'apply assertion-fn [actual expected (symbol-for assertion)])))

(defn- parse-head [[head & _]]
  (if (seq? head)
    (parse-expressions head)
    head))

(defn- parse-tail [[_ & tail]]
  (parse-expressions tail))

(defn- parse-expressions [body]
  (if (seq body)
    (let [head (parse-head body)
          tail (rest body)
          expressions (cons head tail)]
      (if (has-assertion? expressions)
        (cons (parse-assertion (take 3 expressions))
              (parse-expressions (drop 3 expressions)))
        (cons head (parse-tail expressions))))
    ()))

(defn- parse-fact [body]
  (cons 'do (parse-expressions body)))



(defn- left-line [position]
  (-> position zip/left zip/node meta :line))

(defn- right-line [position]
  (-> position zip/right zip/node meta :line))

(defn- previous-line [position]
  (domonad maybe-m
           [prev (zip/prev position)
            left (zip/left prev)
            node (zip/node left)]
           (-> node meta :line)))

(defn guess-line [assertion-position base-line]
  (or (left-line assertion-position)
      (right-line assertion-position)
      (previous-line assertion-position)
      base-line))

(defn annotate-assertion [form base-line]
  (if (zip/end? form)
    (zip/root form)
    (do
      (let [node (zip/node form)]
        (if (assertions node)
          (let [assertion (with-meta node {:line (guess-line form base-line)})]
            (recur (-> form (zip/replace assertion) zip/next) (inc base-line)))
          (recur (zip/next form) base-line))))))

(defn annotate-assertions [form]
  (let [base-line (or (-> form meta :line) 1)
        form-zipper (zip/seq-zip form)]
    (annotate-assertion (zip/next form-zipper) base-line)))

(defmacro fact [& _]
  (->> &form
       annotate-assertions
       (drop 2)
       parse-fact))
