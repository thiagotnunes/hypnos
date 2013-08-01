(ns oizys.result
  (:require
   [colorize.core :as color]))

(defn- print-failure [description {namespace :namespace line :line message :message}]
  (printf "%s \"%s\" at (%s:%d)\n%s\n"
          (color/red "FAIL:")
          description
          namespace
          line
          message))

(defn to-stdout [description assertions-result]
  (let [errors (->> assertions-result
                    deref
                    (remove nil?)
                    seq)]
    (when errors
      (println "")
      (doseq [error errors]
        (print-failure description error))
      (println ""))))
