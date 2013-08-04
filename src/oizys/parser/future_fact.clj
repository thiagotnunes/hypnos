(ns oizys.parser.future-fact
  (:require
   [oizys.parser.description :as description]
   [oizys.result             :as result]))

(defn warn [form]
  (-> form
      description/description
      result/print-pending))
