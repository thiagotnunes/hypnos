(ns hypnos.parser.output
  (:require
   [velcro.core    :refer :all]

   [hypnos.parser.description :as description]))

(defn with-printing [errors description output-fn form]
  (concat form
          `((~output-fn ~description ~errors))))

(defn printing-fn [errors output-fn]
  (fn [form]
    (let [description (description/description form)
          with-printing-fn (partial with-printing errors description output-fn)]
      (replace-in form
                  [current-node]
                  (by with-printing-fn)
                  (where #(-> % current-node meta :hypnos-errors))))))
