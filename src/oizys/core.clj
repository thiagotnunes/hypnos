(ns oizys.core
  (:require
   [oizys.parser :as parser]))

(defmacro fact [description & body]
  (parser/parse-fact body))
