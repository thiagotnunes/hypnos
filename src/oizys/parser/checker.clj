(ns oizys.parser.checker
  (:require
   [oizys.checkers.core :as checker]))

(def wrapper {'not `complement})

(defn- expected-fn-from [expected]
  (if (list? expected)
    (when (symbol? (first expected))
      (first expected))
    (when (symbol? expected)
      expected)))

(defn- checker-fn? [expected-fn]
  (-> expected-fn resolve meta :oizys-checker-fn))

(defn- checker-from [expected]
  (let [expected-fn (expected-fn-from expected)]
    (when (and expected-fn (checker-fn? expected-fn))
      (if (list? expected)
        {:fn (first expected)
         :args (rest expected)}
        {:fn expected
         :args ()}))))

(defn- wrapper-from [expected]
  (when (list? expected)
    (some wrapper expected)))

(defn- build-expectation [actual expected]
  (if-let [checker (checker-from expected)]
    `(~(:fn checker) ~actual ~@(:args checker))
    `(~#'checker/equal ~expected ~actual)))

(defn checker->function [actual expected]
  (if-let [wrapper (wrapper-from expected)]
    `(~wrapper ~(build-expectation actual (second expected)))
    (build-expectation actual expected)))
