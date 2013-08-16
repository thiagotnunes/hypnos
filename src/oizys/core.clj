(ns oizys.core
  (:require
   [oizys.parser.future-fact :as future-fact]
   [oizys.parser.description :as description]
   [oizys.parser.assertion   :as assertion]
   [oizys.parser.meta        :as meta]
   [oizys.checker            :as checker]
   [potemkin                 :as potemkin]))

(defn- body [form]
  (drop 2 form))

(defmacro failing-fact [& _]
  (let [formatted-form (-> &form
                           meta/annotate
                           description/format)
        error-handling-fn (assertion/error-handling-fn formatted-form)]
    (-> formatted-form
        body
        assertion/assertions->refute-functions
        error-handling-fn)))

(defmacro fact [& _]
  (let [formatted-form (-> &form
                           meta/annotate
                           description/format)
        error-handling-fn (assertion/error-handling-fn formatted-form)]
    (-> formatted-form
        body
        assertion/assertions->confirm-functions
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

(potemkin/import-vars
 [oizys.checker

  equal
  truthy
  falsey
  defchecker])
