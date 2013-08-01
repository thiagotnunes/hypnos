(ns oizys.core
  (:require
   [oizys.meta  :as meta]
   [oizys.form  :as form]
   [clojure.zip :as zip]))

(defn- fact-description [form]
  (second form))

(defn- fact-body [form]
  (drop 2 form))

(defmacro fact [& _]
  (let [description (fact-description &form)
        fact-body (fact-body &form)]
    (->> fact-body
         meta/annotate-assertions
         form/assertions->functions
         (form/assertions->with-error-handling description))))

(defmacro facts [& body]
  `(do ~@(drop 2 &form)))
