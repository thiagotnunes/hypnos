(ns oizys.core
  (:require
   [oizys.meta   :as meta]
   [oizys.form   :as form]
   [oizys.result :as result]
   [oizys.zip    :as ozip]
   [clojure.zip  :as zip]))

(defn- name-from [form]
  (second form))

(defn- body-from [form]
  (drop 2 form))

(defmacro fact [& _]
  (let [description (name-from &form)
        fact-body (body-from &form)]
    (->> fact-body
         meta/annotate
         form/assertions->functions
         (form/assertions->with-error-handling description result/to-stdout))))

(defmacro facts [& _]
  (let [name (name-from &form)
        body (body-from &form)]
    `(do ~@(form/add-name-to-nested-facts name body))))
