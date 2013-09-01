(ns oizys.parser.checker
  (:require
   [oizys.checkers.core :as checker]))

(def wrappers {'not `complement})

(defn- expected-fn-from [expected]
  (if (list? expected)
    (when (symbol? (first expected))
      (first expected))
    (when (symbol? expected)
      expected)))

(defn- has-checker-fn? [expected]
  (when-let [expected-fn (expected-fn-from expected)]
    (-> expected-fn resolve meta :oizys-checker-fn)))

(defn- checker-from [expected]
  (when (has-checker-fn? expected)
    (if (list? expected)
      {:fn (first expected)
       :args (rest expected)}
      {:fn expected
       :args ()})))

(defn- wrapper-from [expected]
  (when (list? expected)
    (some wrappers expected)))

(defn- checking-function [actual expected]
  (if-let [checker (checker-from expected)]
    `(~(:fn checker) ~actual ~@(:args checker))
    `(~#'checker/equal ~expected ~actual)))

(defn checker->function [actual expected]
  (if-let [wrapper (wrapper-from expected)]
    `(~wrapper ~(checking-function actual (second expected)))
    (checking-function actual expected)))
