(ns hypnos.result
  (:require
   [colorize.core  :as color]
   [clojure.string :as string]))

(defn- format-description [{description :description nesting :nesting}]
  (string/join " - " (conj nesting description)))

(defn- print-failure [{type :type
                       namespace :namespace
                       line :line
                       expression :expression}]
  (case type
    :confirm (printf (color/white "\tExpected: %s\n")
                     expression)
    :refute (printf (color/white "\tExpected not: %s\n")
                     expression))
  (printf (color/white "\tat %s:%d\n")
          namespace
          line))

(defn print-pending [description]
  (printf (color/yellow "PENDING: \"%s\"\n")
          (format-description description)))

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
