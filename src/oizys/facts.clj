(ns oizys.facts
  (:require
   [oizys.zip   :as ozip]
   [oizys.fact  :as fact]
   [clojure.zip :as zip]))

(defn- add-nesting-description [form description]
  (let [position (zip/right form)
        current-description (zip/node position)]
    (zip/replace position (update-in current-description
                                     [:nesting]
                                     conj
                                     description))))

(defn add-description-to-nested-fact [form]
  (let [description (second form)]
    (ozip/traverse form
                   fact/fact?
                   #(add-nesting-description % description))))
