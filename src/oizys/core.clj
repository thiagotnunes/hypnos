(ns oizys.core
  (:require
   [oizys.line-position :as position]
   [oizys.form          :as form]
   [clojure.zip         :as zip]))

(defn- fact-description [form]
  (second form))

(defn- fact-body [form]
  (drop 2 form))

(defmacro fact [& _]
  (let [description (fact-description &form)
        fact-body (fact-body &form)]
    (->> fact-body
         position/annotate-assertions
         form/assertions->functions
         (form/assertions->with-error-handling description))))

(defmacro facts [& body]
  `(do ~@(drop 2 &form)))
