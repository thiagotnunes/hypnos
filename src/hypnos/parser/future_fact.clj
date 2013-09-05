(ns hypnos.parser.future-fact
  (:require
   [hypnos.parser.description :as description]
   [hypnos.result             :as result]))

(defn warn [form]
  (-> form
      description/description
      result/print-pending))
