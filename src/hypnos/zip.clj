(ns hypnos.zip
  (:require
   [clojure.algo.monads :refer [domonad maybe-m]]
   [clojure.zip         :as zip]))

(defn current-node [form]
  (zip/node form))

(defn left-node [form]
  (-> form zip/left zip/node))

(defn right-node [form]
  (-> form zip/right zip/node))

(defn previous-node [form]
  (domonad maybe-m
           [prev (zip/prev form)
            left (zip/left prev)]
           (zip/node left)))

(defn remove-left [form]
  (-> form zip/left zip/remove))

(defn remove-right [form]
  (-> form zip/right zip/remove))

(defn traverse [form pred func]
  (letfn [(traverse-form [form]
            (if (zip/end? form)
              form
              (if (pred (zip/node form))
                (recur (zip/next (func form)))
                (recur (zip/next form)))))]
    (-> form
        zip/seq-zip
        traverse-form
        zip/root)))


(defn by [func]
  (fn [nodes-fn form]
    (let [nodes ((apply juxt nodes-fn) form)]
      (apply func nodes))))

(defn where [fn]
  fn)

(defn replace-in [form nodes-fn by-fn where-fn]
  (letfn [(replace-in-form [form]
            (if (zip/end? form)
              form
              (if (where-fn form)
                (recur (-> form
                           (zip/insert-left (by-fn nodes-fn form))
                           zip/remove
                           zip/next))
                (recur (zip/next form)))))]
    (-> form
        zip/seq-zip
        replace-in-form
        zip/root)))
