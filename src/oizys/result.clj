(ns oizys.result
  (:require
   [colorize.core :as color]))

(defn- print-failure [description {namespace :namespace line :line message :message}]
  (printf "%s \"%s\" at (%s:%d)\n%s\n\n"
          (color/red "FAIL:")
          description
          namespace
          line
          message))

(defn- print-success [description]
  (printf "%s \"%s\"\n"
          (color/green "SUCCESS:")
          description))

(defn to-stdout [description assertions-result]
  (let [errors (->> assertions-result
                    deref
                    (remove nil?)
                    seq)]
    (if errors
      (doseq [error errors] (print-failure description error))
      (print-success description))))
