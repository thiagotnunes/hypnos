(ns oizys.checkers.collections
  (:require
   [clojure.data        :as data]
   [clojure.set         :as set]
   [oizys.checkers.core :refer [defchecker]]))

(defchecker contains [actual expected]
  (cond
   (vector? expected) (= expected actual)
   :else              (some #{expected} actual)))
