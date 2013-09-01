(ns oizys.parser.description
  (:require
   [clojure.zip :as zip]
   [oizys.zip   :as ozip]))

(defn description [form]
  (second form))

(defn- should-normalize? [node]
  (#{'fact 'future-fact 'failing-fact} node))

(defn- normalized? [description]
  (and (map? description)
       (:description description)
       (:nesting description)))

(defn- normalize-description [form]
  (let [position (zip/right form)
        description (zip/node position)]
    (if (normalized? description)
      form
      (zip/replace position {:description description :nesting []}))))

(defn normalize [form]
  (ozip/traverse form
                 should-normalize?
                 normalize-description))

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
                   should-normalize?
                   nest-description-fn)))
