(ns oizys.result
  (:require
   [colorize.core  :as color]
   [clojure.string :as string]))

(defn- format-description [description]
  (if (map? description)
    (string/join " - " (conj (:nesting description) (:name description)))
    description))

(defn- print-failure [{namespace :namespace
                       line :line
                       expression :expression}]
  (printf (color/white "\tExpected: %s\n")
          expression)
  (printf (color/white "\tat %s:%d\n")
          namespace
          line))

(defn to-stdout [description assertions-result]
  (when-let [errors (->> assertions-result
                    deref
                    (remove nil?)
                    seq)]
    (printf (color/red "FAIL: \t\"%s\"\n")
            (format-description description))
    (doseq [error errors]
      (print-failure error))
    (println "")))
