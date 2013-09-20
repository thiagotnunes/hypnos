(ns hypnos.parser.future-fact
  (:require
   [hypnos.parser.description :as description]
   [hypnos.output.repl        :as output]))

(defn warn [result-fn]
  (fn [form]
    (-> form
        description/description
        result-fn)))
