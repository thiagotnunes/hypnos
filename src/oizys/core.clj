(ns oizys.core
  (:require
   [oizys.meta   :as meta]
   [oizys.form   :as form]
   [oizys.result :as result]
   [oizys.zip    :as ozip]
   [clojure.zip  :as zip]))

(defn- description-from [form]
  (second form))

(defn- body-from [form]
  (drop 2 form))

(defmacro fact [& _]
  (let [description (description-from &form)
        fact-body (body-from &form)]
    (->> fact-body
         meta/annotate
         form/assertions->functions
         (form/assertions->with-error-handling description result/to-stdout))))

(defn- add-description [description form]
  (let [original-description (-> form zip/right zip/node)]
    (if (map? original-description)
        (let [scope (:scoping original-description)]
          (-> form zip/next zip/remove (zip/insert-right (assoc original-description :scoping (conj scope description)))))
        (-> form zip/next zip/remove (zip/insert-right {:description original-description
                                                        :scoping [description]})))))

(defmacro facts [& _]
  (let [description (description-from &form)
        body (body-from &form)]
    `(do ~@(-> body
               (ozip/traverse #{'fact}
                              (partial add-description description))))))
