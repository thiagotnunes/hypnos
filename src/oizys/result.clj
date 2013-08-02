(ns oizys.result
  (:require
   [colorize.core  :as color]
   [clojure.string :as string]))

(defn- format-description [description]
  (if (map? description)
    (string/join " - " (conj (:scoping description) (:description description)))
    description))

(defn- print-failure [description {namespace :namespace
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
    (printf "%s\t\"%s\"\n"
            (color/red "FAIL: ")
            (format-description description))
    (doseq [error errors]
      (print-failure description error))
    (println "")))
