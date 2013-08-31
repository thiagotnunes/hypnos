(ns oizys.core
  (:require
   [oizys.parser.future-fact   :as future-fact]
   [oizys.parser.description   :as description]
   [oizys.parser.assertion     :as assertion]
   [oizys.parser.meta          :as meta]
   [potemkin                   :as potemkin]
   [oizys.checkers.core        :as checkers]
   [oizys.checkers.collections :as collections]))

(defn- fact-body [form]
  (->> form
       (drop 2)
       first))

(defn- facts-body [form]
  (drop 2 form))

(defmacro failing-fact [& _]
  (-> &form
      meta/annotate
      description/format
      assertion/assertions->refute-functions
      assertion/error-handling
      fact-body))

(defmacro fact [& _]
  (-> &form
      meta/annotate
      description/format
      assertion/assertions->confirm-functions
      assertion/error-handling
      fact-body))

(defmacro facts [& _]
  `(do ~@(-> &form
             description/format
             description/add-nested
             facts-body)))

(defmacro future-fact [& _]
  (-> &form
      description/format
      future-fact/warn))

(potemkin/import-vars
 [oizys.checkers.core
  
  defchecker
  equal
  truthy
  falsey
  throws
  roughly]
 
 [oizys.checkers.collections

  matches
  starts-with
  ends-with])
