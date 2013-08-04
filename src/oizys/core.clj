(ns oizys.core
  (:require
   [oizys.meta   :as meta]
   [oizys.form   :as form]
   [oizys.fact   :as fact]
   [oizys.facts   :as facts]
   [oizys.result :as result]))

(defn- description-from [form]
  (second form))

(defn- body [form]
  (drop 2 form))

(defmacro fact [& _]
  (let [form (->> &form
                  meta/annotate
                  fact/format-description)
        description (description-from form)]
    (->> form
         body
         form/assertions->functions
         (form/assertions->with-error-handling description result/to-stdout))))

(defmacro facts [& _]
  (let [form (->> &form
                  fact/format-description
                  facts/add-description-to-nested-fact)]
    `(do ~@(body form))))
