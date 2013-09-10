(ns hypnos.core
  (:require
   [hypnos.parser.future-fact   :as future-fact]
   [hypnos.parser.description   :as description]
   [hypnos.parser.assertion     :as assertion]
   [hypnos.parser.provided      :as provided]
   [hypnos.parser.metadata      :as metadata]
   [hypnos.checkers.collections :as collections]
   [hypnos.checkers.core        :as checkers]
   
   [potemkin :as potemkin]))

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
      provided/provided->mocks
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
 [hypnos.checkers.core
  
  defchecker
  equal
  truthy
  falsey
  throws
  roughly]
 
 [hypnos.checkers.collections

  matches
  starts-with
  ends-with
  has])
