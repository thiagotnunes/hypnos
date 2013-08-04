(ns oizys.parser.facts
  (:require
   [oizys.parser.description :as description]
   [oizys.parser.fact        :as fact]
   [oizys.zip                :as ozip]
   [clojure.zip              :as zip]))

(defn- add-nesting-description [form description]
  (let [position (zip/right form)
        current-description (zip/node position)]
    (zip/replace position (update-in current-description
                                     [:nesting]
                                     conj
                                     description))))

(defn add-description-to-nested-fact [form]
  (let [description (description/description form)]
    (ozip/traverse form
                   fact/fact?
                   #(add-nesting-description % description))))
