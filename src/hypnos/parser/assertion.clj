(ns hypnos.parser.assertion
  (:require
   [hypnos.parser.checker :as checker]
   [hypnos.assertion      :as assertion]
   [hypnos.result         :as result]
   [hypnos.zip            :as hzip]
   [hypnos.zip            :refer :all]
   
   [clojure.zip :as zip]
   [potemkin    :as potemkin]))

(defn- assertion->function [assertion-fn actual expected assertion-symbol]
  (with-meta
    `(apply ~assertion-fn [~(checker/checker->function actual expected)
                           '~assertion-symbol
                           '(~actual ~assertion-symbol ~expected)])
    {:hypnos-assertion true}))

(def assertion->confirm (partial assertion->function #'assertion/confirm))
(def assertion->refute (partial assertion->function #'assertion/refute))

(defn- with-error-handling [form results]
  (let [assertion (zip/node form)]
    (-> form
        (zip/replace (with-meta
                       `(swap! ~results conj ~assertion)
                       {:hypnos-assertion-error-handling true}))
        zip/down
        zip/rightmost)))

(defn error-handling [form]
  (let [name (first form)
        description (second form)
        body (drop 2 form)]
    (potemkin/unify-gensyms
     `(~name ~description
       (let [assertion-results## (atom [])]
         ~@(hzip/traverse body
                          #(-> % meta :hypnos-assertion)
                          #(with-error-handling % `assertion-results##))
         (result/to-stdout ~description assertion-results##))))))

(defn assertions->confirms [form]
  (replace-in form
              [left-node right-node current-node]
              (by assertion->confirm)
              (where #(assertion/assertions (current-node %)))))

(defn assertions->refutes [form]
  (replace-in form
              [left-node right-node current-node]
              (by assertion->refute)
              (where #(assertion/assertions (current-node %)))))
