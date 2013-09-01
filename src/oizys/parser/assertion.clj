(ns oizys.parser.assertion
  (:require
   [oizys.parser.checker :as checker]
   [oizys.assertion      :as assertion]
   [oizys.result         :as result]
   [oizys.zip            :as ozip]
   
   [clojure.zip          :as zip]
   [potemkin             :as potemkin]))

(defn- remove-actual [form]
  (-> form
      ozip/remove-left
      zip/next))

(defn- remove-expected [form]
  (-> form
      ozip/remove-right))

(defn- assertion-function-from [assertion-fn actual expected assertion-symbol]
  (with-meta
    `(apply ~assertion-fn [~(checker/checker->function actual expected)
                           '~assertion-symbol
                           '(~actual ~assertion-symbol ~expected)])
    {:oizys-assertion true}))

(defn- assertion->function [assertion-fn form]
  (let [actual (ozip/left-node form)
        assertion-symbol (zip/node form)
        expected (ozip/right-node form)]
    (-> form
        remove-actual
        remove-expected
        (zip/replace (assertion-function-from assertion-fn
                                              actual
                                              expected
                                              assertion-symbol)))))
(def assertion->confirm (partial assertion->function #'assertion/confirm))
(def assertion->refute (partial assertion->function #'assertion/refute))


(defn- with-error-handling [form results]
  (let [assertion (zip/node form)]
    (-> form
        (zip/replace (with-meta
                       `(swap! ~results conj ~assertion)
                       {:oizys-assertion-error-handling true}))
        zip/down
        zip/rightmost)))

(defn error-handling [form]
  (let [name (first form)
        description (second form)
        body (drop 2 form)]
    (potemkin/unify-gensyms
     `(~name ~description
       (let [assertion-results## (atom [])]
         ~@(ozip/traverse body
                          #(-> % meta :oizys-assertion)
                          #(with-error-handling % `assertion-results##))
         (result/to-stdout ~description assertion-results##))))))

(defn assertions->confirms [form]
  (ozip/traverse form
                 assertion/assertions
                 assertion->confirm))

(defn assertions->refutes [form]
  (ozip/traverse form
                 assertion/assertions
                 assertion->refute))
