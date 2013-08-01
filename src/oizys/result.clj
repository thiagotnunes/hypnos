(ns oizys.result
  (:require
   [colorize.core :as color]))

(defn to-stdout [description assertions-result]
  (let [errors (->> assertions-result
                    deref
                    (remove nil?)
                    seq)]
    (if errors
      (doseq [error errors]
        (printf "%s \"%s\" at (file:%d)\n%s\n\n"
                (color/red "FAIL:")
                description
                (:line error)
                (:message error)))
      (printf "%s \"%s\"\n"
              (color/green "SUCCESS:")
              description))))
