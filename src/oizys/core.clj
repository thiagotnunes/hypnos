(ns oizys.core)

(defn- split-at-checker [body]
  (split-at 1 body))

(defn- actual [body]
  (->> body
       split-at-checker
       first))

(defn- expected [body]
  (->> body
       split-at-checker
       second
       (drop 1)))

(defn- evaluate [full-expr]
  (let [expr (first full-expr)]
    (if (coll? expr)
      (eval expr)
      expr)))

(defn- check [actual expected]
  (let [evaluated-actual (evaluate actual)
        evaluated-expected (evaluate expected)]
    (if (= evaluated-actual evaluated-expected)
      true
      (throw (AssertionError. (str (first actual) " != " (first expected)))))))

(defmacro fact [description & body]
  (let [actual# (actual body)
        expected# (expected body)]
    (check actual# expected#)))
