(ns oizys.parser
  (:require
   [oizys.function :as function]
   [oizys.checker  :as checker]))

(declare parse-expressions)

(defn- has-assertion? [expressions]
  (checker/checkers (second expressions)))

(defn- parse-assertion [[head checker tail]]
  (let [check-fn (function/fn->symbol (var checker/check))]
    (list 'apply check-fn [head tail (checker/symbol-for checker)])))

(defn- parse-head [[head & _]]
  (if (list? head)
    (parse-expressions head)
    head))

(defn- parse-tail [[_ & tail]]
  (parse-expressions tail))

(defn- parse-expressions [body]
  (if (seq body)
    (let [head (parse-head body)
          tail (rest body)
          expressions (cons head tail)]
      (if (has-assertion? expressions)
        (cons (parse-assertion (take 3 expressions))
              (parse-expressions (drop 3 expressions)))
        (cons head (parse-tail expressions))))
    ()))

(defn parse-fact [body]
  (cons 'do (parse-expressions body)))
