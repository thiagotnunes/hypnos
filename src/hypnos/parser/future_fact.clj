(ns hypnos.parser.future-fact
  (:require
   [hypnos.parser.description :as description]
   [hypnos.output.repl        :as output]))

(defn warn [form]
  (-> form
      description/description
      output/print-pending))
