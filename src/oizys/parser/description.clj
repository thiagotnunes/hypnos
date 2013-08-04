(ns oizys.parser.description
  (:require
   [clojure.zip :as zip]
   [oizys.zip   :as ozip])
  (:refer-clojure
   :exclude [format]))

(defn description [form]
  (second form))

(defn- formatted? [description]
  (and (map? description)
       (:description description)
       (:nesting description)))

(defn- description->map [form]
  (let [position (zip/right form)
        description (zip/node position)]
    (if (formatted? description)
      form
      (zip/replace position {:description description :nesting []}))))

(defn format [form pred]
  (ozip/traverse form
                 pred
                 description->map))
