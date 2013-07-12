(ns oizys.core)

(defn- split-checks [body]
  (partition 3 body))

(defn- actual [body]
  (first body))

(defn- expected [body]
  (nth body 2))

(defn- evaluate [expr]
  (eval expr))

(defn- check [actual expected]
  (let [evaluated-actual (evaluate actual)
        evaluated-expected (evaluate expected)]
    (if (= evaluated-actual evaluated-expected)
      true
      (throw (AssertionError. (str (first actual) " != " (first expected)))))))

(defmacro fact [description & body]
  (let [checks# (split-checks body)]
    (doseq [check# checks#]
      (check (actual check#) (expected check#)))))
