(ns oizys.parser.fact
  (:require
   [oizys.zip   :as ozip]
   [clojure.zip :as zip]))

(defn fact? [node]
  (= 'fact node))

(defn formatted? [description]
  (and (map? description)
       (:description description)
       (:nesting description)))

(defn- description->map [form]
  (let [position (zip/right form)
        description (zip/node position)]
    (if-not (formatted? description)
      (zip/replace position {:description description :nesting []})
      form)))

(defn format-description [form]
  (ozip/traverse form
                 fact?
                 description->map))
