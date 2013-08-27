(ns oizys.checkers.collections
  (:require
   [clojure.data        :as data]
   [clojure.set         :as set]
   [oizys.checkers.core :refer [defchecker]]
   [clojure.core.match  :refer [match]]))

(defmacro ^{:oizys-checker-fn true} matches [actual expected]
  `(fn []
     (match [~actual]
            [~expected] true
            :else false)))
