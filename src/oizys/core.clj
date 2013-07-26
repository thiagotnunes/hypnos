(ns oizys.core
  (:require
   [clojure.zip         :as zip]
   [clojure.algo.monads :refer [domonad maybe-m]]
   [slingshot.slingshot :refer [throw+]]))

(declare parse-expressions)
(declare expected-assertion)

(defn- fn->symbol [fn-var]
  (let [fn-meta (meta fn-var)
        namespace-name (-> fn-meta :ns ns-name)
        function-name (:name fn-meta)]
    (symbol (str namespace-name "/" function-name))))

(def assertions {'=> #'expected-assertion})

(defn assertion [actual expected assertion-fn line]
  (let [evaluated-actual (eval actual)
        evaluated-expected (eval expected)]
    (when-not (assertion-fn evaluated-actual evaluated-expected)
      {:actual actual
       :expected expected
       :line line})))

(defn expected-assertion [actual expected]
  (= actual expected))

(defn- symbol-for [assertion]
  (-> assertion
      assertions
      fn->symbol))

(defn line-for [assertion]
  (-> assertion
      meta
      :line))

(defn- parse-assertion [form]
  (let [assertion-fn (fn->symbol #'assertion)
        actual (-> form zip/left zip/node)
        assertion (zip/node form)
        expected (-> form zip/right zip/node)
        form-without-actual-expected (-> form
                                         zip/left
                                         zip/remove
                                         zip/next
                                         zip/right
                                         zip/remove)]
    (zip/remove
     (zip/insert-left form-without-actual-expected `(apply ~assertion-fn ~[actual
                                                                           expected
                                                                           (symbol-for assertion)
                                                                           (line-for assertion)])))))

(defn- parse-expressions [form]
  (if (zip/end? form)
    form
    (if (assertions (zip/node form))
      (recur (parse-assertion form))
      (recur (zip/next form)))))

(defn- parse-fact [body]
  (-> body
      zip/seq-zip
      parse-expressions
      zip/root
      (conj 'do)))



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

(defn add-line-to-assertion [form base-line]
  (let [node (zip/node form)]
    (with-meta node {:line (guess-line form base-line)})))

(defn annotate-assertion [form base-line]
  (if (zip/end? form)
    (zip/root form)
    (if (assertions (zip/node form))
      (recur (-> form (zip/replace (add-line-to-assertion form base-line)) zip/next) (inc base-line))
      (recur (zip/next form) base-line))))

(defn annotate-assertions [form]
  (let [base-line (or (-> form meta :line) 1)
        form-zipper (zip/seq-zip form)]
    (annotate-assertion (zip/next form-zipper) base-line)))



(defmacro fact [& _]
  (->> &form
       annotate-assertions
       (drop 2)
       parse-fact))
