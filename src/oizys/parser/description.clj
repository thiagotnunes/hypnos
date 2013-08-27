(ns oizys.parser.description
  (:require
   [clojure.zip :as zip]
   [oizys.zip   :as ozip])
  (:refer-clojure
   :exclude [format]))

(defn description [form]
  (second form))

(defn- should-format? [node]
  (#{'fact 'future-fact 'lie} node))

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

(defn format [form]
  (ozip/traverse form
                 should-format?
                 description->map))

(defn- nest-description [description form]
  (let [position (zip/right form)
        current-description (zip/node position)]
    (zip/replace position (update-in current-description
                                     [:nesting]
                                     conj
                                     description))))

(defn add-nested [form]
  (let [description (description form)
        nest-description-fn (partial nest-description description)]
    (ozip/traverse form
                   should-format?
                   nest-description-fn)))
