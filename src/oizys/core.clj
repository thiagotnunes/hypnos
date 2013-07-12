(ns oizys.core)

(defn- third [coll]
  (nth coll 2))

(defn- split-checks [body]
  (partition 3 body))

(defn- actual [check]
  (first check))

(defn- expected [check]
  (third check))

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
