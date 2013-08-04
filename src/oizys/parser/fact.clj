(ns oizys.parser.fact
  (:require
   [oizys.parser.description :as description]
   [oizys.zip                :as ozip]))

(defn fact? [node]
  (= 'fact node))

(defn format-description [form]
  (ozip/traverse form
                 fact?
                 description/description->map))
