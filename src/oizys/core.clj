(ns oizys.core
  (:require
   [oizys.line-position :as position]
   [oizys.form          :as form]
   [clojure.zip         :as zip]))

(defn- remove-description [body]
  (drop 2 body))

(defmacro fact [& _]
  (let [description (second &form)]
    (->> &form
         remove-description
         position/add-line-number-to-assertions
         form/assertions->functions
         (form/assertions->with-error-handling description))))

(defmacro facts [& body]
  `(do ~@(drop 2 &form)))
