(ns hypnos.parser.assertion
  (:require
   [hypnos.parser.checker :as checker]
   [hypnos.assertion      :as assertion]
   [hypnos.result         :as result]

   [velcro.core :refer :all]
   [potemkin    :as potemkin]))

(defn- assertion->function [assertion-fn actual expected assertion-symbol]
  (with-meta
    `(apply ~assertion-fn [~(checker/checker->function actual expected)
                           '~assertion-symbol
                           '(~actual ~assertion-symbol ~expected)])
    {:hypnos-assertion true}))

(def assertion->confirm (partial assertion->function #'assertion/confirm))
(def assertion->refute (partial assertion->function #'assertion/refute))

(defn- with-error-handling [assertion results]
  (with-meta
    `(swap! ~results conj ~assertion)
    {:hypnos-assertion-error-handling true}))

(defn assertion? [node]
  (-> node meta :hypnos-assertion))

(defn error-handling [form]
  (let [name (first form)
        description (second form)
        body (drop 2 form)]
    (potemkin/unify-gensyms
     `(~name ~description
       (let [assertion-results## (atom [])]
         ~@(replace-in body
                       [current-node]
                       (by #(with-error-handling % `assertion-results##))
                       (where #(assertion? (current-node %))))
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
