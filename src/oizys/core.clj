(ns oizys.core
  (:require
   [oizys.line-position :as position]
   [oizys.assertion     :as assertion]
   [clojure.zip         :as zip]))

(declare parse-expressions)

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
                                              (assertion/assertions assertion-symbol)
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


(defmacro fact [& _]
  (->> &form
       annotate-assertions
       (drop 2)
       parse-fact))

(defmacro facts [& body]
  `(do ~@(drop 2 &form)))
