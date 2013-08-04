(ns oizys.parser.assertion
  (:require
   [oizys.assertion :as assertion]
   [oizys.result    :as result]
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

(defn- with-error-handling [form results]
  (let [assertion (zip/node form)]
    (-> form
        (zip/replace (with-meta
                       `(swap! ~results conj ~assertion)
                       {:oizys-assertion-error-handling true}))
        zip/down
        zip/rightmost)))

(defn error-handling-fn [description-form]
  (let [description (second description-form)]
    (fn [form]
      (let [assertion-results (gensym "assertion_results__")]
        `(let [~assertion-results (atom [])]
           ~@(ozip/traverse form
                            #(-> % meta :oizys-assertion)
                            #(with-error-handling % assertion-results))
           (result/to-stdout ~description ~assertion-results))))))

(defn assertions->functions [form]
  (ozip/traverse form
                 assertion/assertions
                 assertion->function))