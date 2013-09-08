(ns hypnos.parser.description
  (:require
   [hypnos.zip :refer :all]))

(def has-description #{'fact 'future-fact 'failing-fact})

(defn description [form]
  (second form))

(defn- normalized? [description]
  (and (map? description)
       (:description description)
       (:nesting description)))

(defn- normalize-description [description]
  (if (normalized? description)
    description
    {:description description :nesting []}))

(defn normalize [form]
  (replace-in form
              [right-node]
              (by normalize-description)
              (where #(has-description (current-node %)))))


(defn- nest-description [description current-description]
  (update-in current-description
             [:nesting]
             conj
             description))

(defn nest [form]
  (let [description (description form)
        nest-description-fn (partial nest-description description)]
    (replace-in form
                [right-node]
                (by nest-description-fn)
                (where #(has-description (current-node %))))))
