(ns hypnos.output.repl
  (:require
   [colorize.core  :as color]
   [clojure.string :as string]))

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

(defmethod print-failure :mock-args
  [{func :func
    unexpected-args :unexpected-args}]
  (doseq [args unexpected-args]
    (printf (color/white "\tYou never said " func " would be called with args " args "\n"))))

(defmethod print-failure :mock-number-of-calls
  [{func :func}]
  (printf (color/white "\tExpected " func " to be called at least once\n")))

(defn pending [description]
  (printf (color/yellow "PENDING: \"%s\"\n")
          (format-description description)))

(defn result [description assertions-result]
  (when-let [errors (->> assertions-result
                         deref
                         (remove nil?)
                         seq)]
    (printf (color/red "FAIL: \t\"%s\"\n")
            (format-description description))
    (doseq [error errors]
      (print-failure error))
    (println "")))
