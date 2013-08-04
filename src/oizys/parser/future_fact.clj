(ns oizys.parser.future-fact
  (:require
   [oizys.parser.description :as description]
   [oizys.result             :as result]))

(defn future-fact? [node]
  (= 'future-fact node))

(defn warn [form]
  (-> form
      description/description
      result/print-pending))
