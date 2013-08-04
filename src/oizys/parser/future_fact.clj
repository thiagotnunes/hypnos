(ns oizys.parser.future-fact
  (:require
   [oizys.parser.description :as description]
   [oizys.result             :as result]
   [oizys.zip                :as ozip]))

(defn- future-fact? [node]
  (= 'future-fact node))

(defn format-description [form]
  (ozip/traverse form
                 future-fact?
                 description/description->map))

(defn report [form]
  (-> form
      description/description
      result/print-pending))
