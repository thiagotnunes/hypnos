(ns oizys.core
  (:require
   [oizys.line-position :as position]
   [oizys.assertion     :as assertion]
   [clojure.zip         :as zip]))

(declare parse-expressions)

(defn- traverse [form pred func]
  (letfn [(traverse-form [form]
            (if (zip/end? form)
              form
              (if (pred (zip/node form))
                (recur (func form))
                (recur (zip/next form)))))]
    (-> form
        zip/seq-zip
        traverse-form
        zip/root)))

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
                           {:oizys-assertion true}))
        zip/remove)))

(defn- assertions->functions [form]
  (traverse form
            assertion/assertions
            assertion->function))

(defn- remove-description [body]
  (drop 2 body))

(defn- assertion->with-error-handling [form errors]
  (let [assertion (zip/node form)]
    (-> form
        (zip/insert-left (with-meta `(swap! ~errors conj ~assertion)
                           {:oizys-assertion-error-handling true}))
        zip/remove)))

(defn- assertions->with-error-handling [form]
  (let [errors (gensym "errors__")]
    `(let [~errors (atom [])]
       ~@(traverse form
                   #(-> % meta :oizys-assertion)
                   #(assertion->with-error-handling % errors)))))

(defmacro fact [& _]
  (-> &form
      remove-description
      position/add-line-number-to-assertions
      assertions->functions
      assertions->with-error-handling))

(defmacro facts [& body]
  `(do ~@(drop 2 &form)))
