(ns oizys.line-position
  (:require
   [oizys.assertion     :as assertion]
   [clojure.zip         :as zip]
   [clojure.algo.monads :refer [domonad maybe-m]]))

(defn- left-line [position]
  (-> position zip/left zip/node meta :line))

(defn- right-line [position]
  (-> position zip/right zip/node meta :line))

(defn- previous-line [position]
  (domonad maybe-m
           [prev (zip/prev position)
            left (zip/left prev)
            node (zip/node left)]
           (-> node meta :line)))

(defn- guess-line [assertion-position base-line]
  (or (left-line assertion-position)
      (right-line assertion-position)
      (previous-line assertion-position)
      base-line))

(defn- add-line-to-assertion [form base-line]
  (let [node (zip/node form)]
    (with-meta node {:line (guess-line form base-line)})))

(defn- annotate-assertion [form base-line]
  (if (zip/end? form)
    (zip/root form)
    (if (assertion/assertions (zip/node form))
      (recur (-> form (zip/replace (add-line-to-assertion form base-line)) zip/next) (inc base-line))
      (recur (zip/next form) base-line))))

(defn annotate-assertions [form]
  (let [base-line (or (-> form meta :line) 1)
        form-zipper (zip/seq-zip form)]
    (annotate-assertion (zip/next form-zipper) base-line)))
