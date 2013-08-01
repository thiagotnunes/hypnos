(ns support.calculator)

(defn plus [& args]
  (reduce + args))
