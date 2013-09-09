(ns hypnos.parser.metadata
  (:require
   [hypnos.assertion :as assertion]

   [velcro.core :refer :all]))

(defn- line-number [node]
  (-> node meta :line))

(defn- add-line-number [assertion line]
  (with-meta assertion {:line line}))

(defn- annotate-assertion [node base-line]
  (let [guessed-line (or (line-number node) base-line)]
    (add-line-number node guessed-line)))

(defn annotate [form]
  (let [base-line (or (-> form meta :line) 1)]
    (replace-in form
                [current-node]
                (by #(annotate-assertion % base-line))
                (where #(assertion/assertions (current-node %))))))
