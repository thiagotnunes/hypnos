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

(defn- add-line-number [assertion line]
  (with-meta assertion {:line line}))

(defn- add-filename [assertion]
  (vary-meta assertion assoc :filename "file"))

(defn- annotate-assertion [form base-line]
  (let [guessed-line (guess-line form base-line)]
    (-> form
        zip/node
        (add-line-number guessed-line)
        add-filename)))

(defn- traverse [form base-line]
  (if (zip/end? form)
    (zip/root form)
    (if (assertion/assertions (zip/node form))
      (recur (zip/next (zip/replace form (annotate-assertion form base-line))) (inc base-line))
      (recur (zip/next form) base-line))))

(defn annotate-assertions [form]
  (let [base-line (or (-> form meta :line) 1)]
    (traverse (zip/seq-zip form) base-line)))
