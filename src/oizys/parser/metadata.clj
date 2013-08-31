(ns oizys.parser.metadata
  (:require
   [oizys.assertion :as assertion]
   [oizys.zip       :as ozip]
   
   [clojure.zip     :as zip]))

(defn- line-number [node]
  (-> node meta :line))

(defn- left-line-number [form]
  (-> form ozip/left-node line-number))

(defn- right-line-number [form]
  (-> form ozip/right-node line-number))

(defn- from-previous-line-number [form]
  (when-let [previous-line (-> form ozip/previous-node line-number)]
    (inc previous-line)))

(defn- guess-line [form base-line]
  (or (left-line-number form)
      (right-line-number form)
      (from-previous-line-number form)
      base-line))

(defn- add-line-number [assertion line]
  (with-meta assertion {:line line}))

(defn- annotate-assertion [form base-line]
  (let [guessed-line (guess-line form base-line)]
    (-> form
        zip/node
        (add-line-number guessed-line))))

(defn annotate [form]
  (let [base-line (or (-> form meta :line) 1)]
    (ozip/traverse form
                   assertion/assertions
                   #(zip/replace % (annotate-assertion % base-line)))))
