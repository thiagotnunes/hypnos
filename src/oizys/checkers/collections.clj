(ns oizys.checkers.collections
  (:import
   [clojure.lang IPersistentVector])
  (:require
   [clojure.data        :as data]
   [clojure.set         :as set]
   [oizys.checkers.core :refer [defchecker]]
   [clojure.core.match  :refer [match]]))

(defmulti prefixed?
  (fn [actual expected] [(type actual) (type expected)]))

(defmethod prefixed? [IPersistentVector IPersistentVector] [actual expected]
  (let [actual-count (count actual)
        expected-count (count expected)]
    (when (<= expected-count actual-count)
      (= expected (subvec actual 0 expected-count)))))

(defmethod prefixed? [String String] [actual expected]
  (.startsWith actual expected))

(defmethod prefixed? [Object Object] [actual expected]
  (throw (RuntimeException. (str "Cannot perform operation for types " [(type actual) (type expected)]))))


(defmulti suffixed?
  (fn [actual expected] [(type actual) (type expected)]))

(defmethod suffixed? [IPersistentVector IPersistentVector] [actual expected]
  (let [actual-count (count actual)
        expected-count (count expected)]
    (when (<= expected-count actual-count)
      (= expected (subvec actual (- actual-count expected-count) actual-count)))))

(defmethod suffixed? [String String] [actual expected]
  (.endsWith actual expected))

(defmethod suffixed? [Object Object] [actual expected]
  (throw (RuntimeException. (str "Cannot perform operation for types " [(type actual) (type expected)]))))


(defmacro ^{:oizys-checker-fn true} matches [actual expected]
  `(fn []
     ~(if (= (class actual) String)
        `(re-matches ~expected ~actual)
        `(match [~actual]
                [~expected] true
                :else false))))

(defchecker starts-with [actual expected]
  (prefixed? actual expected))

(defchecker ends-with [actual expected]
  (suffixed? actual expected))

(defchecker has [actual quantifier & matchings]
  (->> matchings
       (map #(quantifier % actual))
       (every? true?)))
