(ns hypnos.parser.errors)

(defn errors-var! []
  (gensym "errors_"))

(defn error-handling-fn [errors]
  (fn [form]
    (let [name (first form)
          description (second form)
          body (drop 2 form)]
      `(~name ~description
              ~(with-meta `(let [~errors (atom [])] ~@body)
                 {:hypnos-errors true})))))
