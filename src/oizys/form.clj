(ns oizys.form
  (:require
   [oizys.assertion :as assertion]
   [oizys.zip       :as ozip]
   [clojure.zip     :as zip]))

(defn- add-name [name form]
  (let [fact-description (ozip/right-node form)
        description-position (zip/right form)]
    (if (map? fact-description)
      (zip/replace description-position (update-in fact-description [:nesting] conj name))
      (zip/replace description-position {:name fact-description :nesting [name]}))))

(defn- assertion->function [form]
  (let [actual (-> form zip/left zip/node)
        assertion-symbol (zip/node form)
        expected (-> form zip/right zip/node)]
    (-> form
        ozip/remove-left
        zip/next
        ozip/remove-right
        (zip/insert-left (with-meta `(apply ~#'assertion/confirm [~actual
                                                                  ~expected
                                                                  '~assertion-symbol
                                                                  '(~actual ~assertion-symbol ~expected)])
                           {:oizys-assertion true}))
        zip/remove)))

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

(defn add-name-to-nested-facts [name form]
  (ozip/traverse form
                 #{'fact}
                 (partial add-name name)))
