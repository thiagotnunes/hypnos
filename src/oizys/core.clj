(ns oizys.core
  (:require
   [oizys.parser.future-fact :as future-fact]
   [oizys.parser.description :as description]
   [oizys.parser.assertion   :as assertion]
   [oizys.parser.meta        :as meta]))

(defn- body [form]
  (drop 2 form))

(defmacro fact [& _]
  (let [formatted-form (-> &form
                           meta/annotate
                           description/format)
        error-handling-fn (assertion/error-handling-fn formatted-form)]
    (-> formatted-form
        body
        assertion/assertions->functions
        error-handling-fn)))

(defmacro facts [& _]
  `(do ~@(-> &form
             description/format
             description/add-nested
             body)))

(defmacro future-fact [& _]
  (-> &form
      description/format
      future-fact/warn))
