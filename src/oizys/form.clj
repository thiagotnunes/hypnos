(ns oizys.form
  (:require
   [oizys.assertion :as assertion]
   [oizys.result    :as result]
   [clojure.zip     :as zip]))

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
                                                                   (meta assertion-symbol)])
                           {:oizys-assertion true}))
        zip/remove)))

(defn- assertion->with-error-handling [form results]
  (let [assertion (zip/node form)]
    (-> form
        (zip/insert-left (with-meta `(swap! ~results conj ~assertion)
                           {:oizys-assertion-error-handling true}))
        zip/remove)))

(defn assertions->with-error-handling [description form]
  (let [assertion-results (gensym "assertion_results__")]
    `(let [~assertion-results (atom [])]
       ~@(traverse form
                   #(-> % meta :oizys-assertion)
                   #(assertion->with-error-handling % assertion-results))
       (result/to-stdout ~description ~assertion-results))))

(defn assertions->functions [form]
  (traverse form
            assertion/assertions
            assertion->function))
