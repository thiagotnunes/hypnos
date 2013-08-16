(ns oizys.parser.checker
  (:require
   [oizys.checker :as checker]))

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

(defn checker->function [actual expected]
  (if-let [checker (checker-from expected)]
    `(~(:fn checker) ~actual ~@(:args checker))
    `(~#'checker/equal ~expected ~actual)))
