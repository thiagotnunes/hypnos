(ns hypnos.parser.errors
  (:require
   [hypnos.result :as result]))

(defn errors-var! []
  (gensym "errors_"))

(defn error-handling-fn [errors]
  (fn [form]
    (let [name (first form)
          description (second form)
          body (drop 2 form)]
      `(~name ~description
              (let [~errors (atom [])]
                ~@body
                (result/to-stdout ~description ~errors))))))
