(ns oizys.result
  (:require
   [colorize.core :as color]))

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
            description)
    (doseq [error errors]
      (print-failure description error))
    (println "")))
