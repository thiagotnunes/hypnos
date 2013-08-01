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

(defn- assertion->function [form]
  (let [actual (-> form zip/left zip/node)
        assertion-symbol (zip/node form)
        expected (-> form zip/right zip/node)]
    (-> form
        remove-left
        zip/next
        remove-right
        (zip/insert-left (with-meta `(apply ~#'assertion/confirm ~[actual
                                                                   expected
                                                                   (assertion/assertions assertion-symbol)
                                                                   (line-for assertion-symbol)])
                           {:oizys-assertion :converted-to-function}))
        zip/remove)))

(defn- parse-assertions [form]
  (if (zip/end? form)
    form
    (if (assertion/assertions (zip/node form))
      (recur (assertion->function form))
      (recur (zip/next form)))))

(defn- assertions->functions [body]
  (-> body
      zip/seq-zip
      parse-assertions
      zip/root))

(defn- remove-description [body]
  (drop 2 body))

(defn- wrap [body]
  `(do ~@body))

(defmacro fact [& _]
  (-> &form
      remove-description
      position/add-line-number-to-assertions
      assertions->functions
      wrap))

(defmacro facts [& body]
  `(do ~@(drop 2 &form)))
