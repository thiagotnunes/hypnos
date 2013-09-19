(ns hypnos.parser.errors)

(defn errors-var! []
  (gensym "errors_"))

(defn error-handling-fn [errors result-fn]
  (fn [form]
    (let [name (first form)
          description (second form)
          body (drop 2 form)]
      `(~name ~description
              (let [~errors (atom [])]
                ~@body
                (~result-fn ~description ~errors))))))
