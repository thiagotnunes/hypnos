(ns hypnos.core
  (:require
   [hypnos.parser.future-fact   :as future-fact]
   [hypnos.parser.description   :as description]
   [hypnos.parser.assertion     :as assertion]
   [hypnos.parser.metadata      :as metadata]
   [hypnos.parser.output        :as output]
   [hypnos.parser.errors        :as errors]
   [hypnos.parser.mock          :as mock]
   [hypnos.checkers.collections :as collections]
   [hypnos.checkers.core        :as checkers]
   [hypnos.output.repl          :as repl]
   
   [potemkin :as potemkin]))

(defn- fact-body [form]
  (->> form
       (drop 2)
       first))

(defn- facts-body [form]
  (drop 2 form))

(defmacro failing-fact [& _]
  (let [errors (errors/errors-var!)
        add-error-handling (errors/error-handling-fn errors)
        parse-assertions (assertion/assertions->refutes errors)
        add-output-printing (output/printing-fn errors repl/result)]
    (-> &form
        description/normalize
        metadata/annotate
        parse-assertions
        add-error-handling
        fact-body)))

(defmacro fact [& _]
  (let [errors (errors/errors-var!)
        add-error-handling (errors/error-handling-fn errors)
        parse-mocks (mock/expectations->mocks errors)
        parse-assertions (assertion/assertions->confirms errors)
        add-output-printing (output/printing-fn errors repl/result)]
    (-> &form
        description/normalize
        metadata/annotate
        parse-mocks
        parse-assertions
        add-error-handling
        add-output-printing
        fact-body)))

(defmacro facts [& _]
  `(do ~@(-> &form
             description/normalize
             description/nest
             facts-body)))

(defmacro future-fact [& _]
  (let [add-output-printing (future-fact/warn repl/pending)]
    (-> &form
        description/normalize
        add-output-printing)))

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
