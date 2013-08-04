(ns oizys.core
  (:require
   [oizys.parser.assertion :as assertion]
   [oizys.parser.meta      :as meta]
   [oizys.parser.fact      :as fact]
   [oizys.parser.facts     :as facts]
   [oizys.result           :as result]))

(defn- description-from [form]
  (second form))

(defn- body [form]
  (drop 2 form))

(defmacro fact [& _]
  (let [formatted-form (-> &form
                           meta/annotate
                           fact/format-description)
        description (description-from formatted-form)]
    (->> formatted-form
         body
         assertion/assertions->functions
         (assertion/assertions->with-error-handling description result/to-stdout))))

(defmacro facts [& _]
  (let [form (-> &form
                 fact/format-description
                 facts/add-description-to-nested-fact)]
    `(do ~@(body form))))
