(ns oizys.core
  (:require
   [clojure.zip         :as zip]
   [clojure.algo.monads :refer [domonad maybe-m]]
   [slingshot.slingshot :refer [throw+]]))

(declare parse-expressions)
(declare expected-assertion)

(def assertions {'=> #'expected-assertion})

(defn confirm [actual expected assertion-fn line]
  (let [evaluated-actual (eval actual)
        evaluated-expected (eval expected)]
    (when-not (assertion-fn evaluated-actual evaluated-expected)
      {:actual actual
       :expected expected
       :line line})))

(defn expected-assertion [actual expected]
  (= actual expected))

(defn line-for [assertion]
  (-> assertion
      meta
      :line))

(defn- remove-left [form]
  (-> form zip/left zip/remove))

(defn- remove-right [form]
  (-> form zip/right zip/remove))

(defn- parse-assertion [form]
  (let [actual (-> form zip/left zip/node)
        assertion-symbol (zip/node form)
        expected (-> form zip/right zip/node)]
    (-> form
        remove-left
        zip/next
        remove-right
        (zip/insert-left `(apply ~#'confirm ~[actual
                                              expected
                                              (assertions assertion-symbol)
                                              (line-for assertion-symbol)]))
        zip/remove)))

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
