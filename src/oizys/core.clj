(ns oizys.core
  (:require
   [oizys.parser.future-fact   :as future-fact]
   [oizys.parser.description   :as description]
   [oizys.parser.assertion     :as assertion]
   [oizys.parser.metadata      :as metadata]
   
   [oizys.checkers.collections :as collections]
   [oizys.checkers.core        :as checkers]
   
   [potemkin                   :as potemkin]))

(defn- fact-body [form]
  (->> form
       (drop 2)
       first))

(defn- facts-body [form]
  (drop 2 form))

(defmacro failing-fact [& _]
  (-> &form
      metadata/annotate
      description/normalize
      assertion/assertions->refutes
      assertion/error-handling
      fact-body))

(defmacro fact [& _]
  (-> &form
      metadata/annotate
      description/normalize
      assertion/assertions->confirms
      assertion/error-handling
      fact-body))

(defmacro facts [& _]
  `(do ~@(-> &form
             description/normalize
             description/nest
             facts-body)))

(defmacro future-fact [& _]
  (-> &form
      description/normalize
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
