(ns hypnos.core
  (:require
   [hypnos.parser.future-fact   :as future-fact]
   [hypnos.parser.description   :as description]
   [hypnos.parser.assertion     :as assertion]
   [hypnos.parser.provided      :as provided]
   [hypnos.parser.metadata      :as metadata]
   [hypnos.parser.errors        :as errors]
   [hypnos.checkers.collections :as collections]
   [hypnos.checkers.core        :as checkers]
   [hypnos.output.repl          :as output]
   
   [potemkin :as potemkin]))

(defn- fact-body [form]
  (->> form
       (drop 2)
       first))

(defn- facts-body [form]
  (drop 2 form))

(defmacro failing-fact [& _]
  (let [errors (errors/errors-var!)
        with-error-handling (errors/error-handling-fn errors output/print)
        assertions->refutes (assertion/assertions->refutes errors)]
    (-> &form
        description/normalize
        metadata/annotate
        with-error-handling
        assertions->refutes
        fact-body)))

(defmacro fact [& _]
  (let [errors (errors/errors-var!)
        with-error-handling (errors/error-handling-fn errors output/print)
        provided->mocks (provided/provided->mocks errors)
        assertions->confirms (assertion/assertions->confirms errors)]
    (-> &form
        description/normalize
        metadata/annotate
        with-error-handling
        provided->mocks
        assertions->confirms
        fact-body)))

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
