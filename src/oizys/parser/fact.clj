(ns oizys.parser.fact
  (:require
   [oizys.parser.description :as description]))

(defn fact? [node]
  (= 'fact node))
