(ns oizys.parser.assertion
  (:require
   [oizys.assertion :as assertion]
   [oizys.zip       :as ozip]
   [clojure.zip     :as zip]))

(defn- remove-actual [form]
  (-> form
      ozip/remove-left
      zip/next))

(defn- remove-expected [form]
  (-> form
      ozip/remove-right))

(defn- assertion-function [actual expected assertion-symbol]
  (with-meta
    `(apply ~#'assertion/confirm [~actual
                                  ~expected
                                  '~assertion-symbol
                                  '(~actual ~assertion-symbol ~expected)])
    {:oizys-assertion true}))

(defn- assertion->function [form]
  (let [actual (ozip/left-node form)
        assertion-symbol (zip/node form)
        expected (ozip/right-node form)]
    (-> form
        remove-actual
        remove-expected
        (zip/replace (assertion-function actual
                                         expected
                                         assertion-symbol)))))

(defn- assertion->with-error-handling [form results]
  (let [assertion (zip/node form)]
    (-> form
        (zip/insert-left (with-meta `(swap! ~results conj ~assertion)
                           {:oizys-assertion-error-handling true}))
        zip/remove)))

(defn assertions->with-error-handling [description result-fn form]
  (let [assertion-results (gensym "assertion_results__")]
    `(let [~assertion-results (atom [])]
       ~@(ozip/traverse form
                        #(-> % meta :oizys-assertion)
                        #(assertion->with-error-handling % assertion-results))
       (~result-fn ~description ~assertion-results))))

(defn assertions->functions [form]
  (ozip/traverse form
                 assertion/assertions
                 assertion->function))
