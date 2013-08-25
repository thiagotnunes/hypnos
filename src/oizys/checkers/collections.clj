(ns oizys.checkers.collections
  (:require
   [clojure.data        :as data]
   [clojure.set         :as set]
   [oizys.checkers.core :refer [defchecker]]
   [clojure.core.match  :refer [match]]))

(defn- match-collection [actual expected]
  (match expected
         actual true
         :else false))

(defchecker contains [actual expected]
  (if (coll? expected)
    (match-collection actual expected)
    (some #{expected} actual)))
