(ns oizys.parser.assertion
  (:require
   [oizys.assertion :as assertion]
   [oizys.checker   :as checker]
   [oizys.result    :as result]
   [oizys.zip       :as ozip]
   [clojure.zip     :as zip]
   [potemkin        :as potemkin]))

(defn- remove-actual [form]
  (-> form
      ozip/remove-left
      zip/next))

(defn- remove-expected [form]
  (-> form
      ozip/remove-right))

(defn- has-checker-fn? [expected]
  (and (list? expected)
       (-> `~expected first resolve meta :checker-fn)))

(defn- checker->function [actual expected]
  (if (has-checker-fn? expected)
    (if-let [args (seq (rest expected))]
      `(~(first expected) ~actual ~args)
      `(~(first expected) ~actual))
    `(~#'checker/equal ~expected ~actual)))

(defn- assertion-function [actual expected assertion-symbol]
  (with-meta
    `(apply ~#'assertion/confirm [~(checker->function actual expected)
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
      (potemkin/unify-gensyms
       `(let [assertion-results## (atom [])]
          ~@(ozip/traverse form
                           #(-> % meta :oizys-assertion)
                           #(with-error-handling % `assertion-results##))
          (result/to-stdout ~description assertion-results##))))))

(defn assertions->functions [form]
  (ozip/traverse form
                 assertion/assertions
                 assertion->function))
