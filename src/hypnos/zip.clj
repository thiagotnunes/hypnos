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

(defn insert-left [form node]
  (-> form
      (zip/insert-left node)
      zip/next))

(defn insert-right [form node]
    (-> form
      (zip/insert-right node)
      zip/next))


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
  (fn [nodes]
    (apply func nodes)))

(defn where [fn]
  fn)

(def remove-fn-mapping {current-node #(-> %)
                        left-node    #(-> % remove-left zip/next)
                        right-node   remove-right})

(def insert-fn-mapping {current-node #(zip/replace %1 %2)
                        left-node insert-left
                        right-node insert-right})

(defn- remove-nodes [form nodes-fn]
  (let [remove-fn (->> nodes-fn
                       (map #(remove-fn-mapping %))
                       reverse
                       (apply comp))]
    (remove-fn form)))

(defn- insert-node [form replacement nodes-fn]
  (let [insert-fn (-> nodes-fn
                      last
                      insert-fn-mapping)]
    (insert-fn form replacement)))

(defn replace-in [form nodes-fn by-fn where-fn]
  (letfn [(replace-in-form [form]
            (if (zip/end? form)
              form
              (if (where-fn form)
                (let [nodes ((apply juxt nodes-fn) form)
                      replacement (by-fn nodes)]
                  (recur (-> form
                             (insert-node replacement nodes-fn)
                             (remove-nodes nodes-fn)
                             zip/next)))
                (recur (zip/next form)))))]
    (-> form
        zip/seq-zip
        replace-in-form
        zip/root)))
