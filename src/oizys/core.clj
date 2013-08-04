(ns oizys.core
  (:require
   [oizys.parser.assertion   :as assertion]
   [oizys.parser.meta        :as meta]
   [oizys.parser.fact        :as fact]
   [oizys.parser.facts       :as facts]
   [oizys.parser.future-fact :as future-fact]))

(defn- body [form]
  (drop 2 form))

(defmacro fact [& _]
  (let [formatted-form (-> &form
                           meta/annotate
                           fact/format-description)
        error-handling-fn (assertion/error-handling-fn formatted-form)]
    (-> formatted-form
        body
        assertion/assertions->functions
        error-handling-fn)))

(defmacro facts [& _]
  `(do ~@(-> &form
             fact/format-description
             facts/add-description-to-nested-fact
             body)))

(defmacro future-fact [& _]
  (-> &form
      future-fact/format-description
      future-fact/report))
