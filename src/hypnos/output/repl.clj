(ns hypnos.output.repl
  (:require
   [colorize.core  :as color]
   [clojure.string :as string]
   [velcro.core    :refer :all]

   [hypnos.parser.description :as description]))

(defn- format-description [{description :description nesting :nesting}]
  (string/join " - " (conj nesting description)))

(defmulti print-failure :type)

(defmethod print-failure :confirm
  [{namespace :namespace
    line :line
    expression :expression}]
  (printf (color/white "\tExpected: %s\n") expression)
  (printf (color/white "\tat %s:%d\n")
          namespace
          line))

(defmethod print-failure :refute
  [{namespace :namespace
    line :line
    expression :expression}]
  (printf (color/white "\tExpected not: %s\n") expression)
  (printf (color/white "\tat %s:%d\n")
          namespace
          line))

(defn pendings [description]
  (printf (color/yellow "PENDING: \"%s\"\n")
          (format-description description)))

(defn print-output [description assertions-result]
  (when-let [errors (->> assertions-result
                         deref
                         (remove nil?)
                         seq)]
    (printf (color/red "FAIL: \t\"%s\"\n")
            (format-description description))
    (doseq [error errors]
      (print-failure error))
    (println "")))

(defn with-printing [errors description form]
  (concat form
          `((print-output ~description ~errors))))

(defn add-printing-fn [errors]
  (fn [form]
    (let [description (description/description form)]
      (replace-in form
                  [current-node]
                  (by (partial with-printing errors description))
                  (where #(-> % current-node meta :hypnos-errors))))))
