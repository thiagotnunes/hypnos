(ns hypnos.parser.provided
  (:require
   [velcro.core :refer :all]
   [potemkin :as potemkin]))

(defn revert-to! [original-fns]
  (doseq [[func original-fn] @original-fns]
    (alter-var-root func (fn [_] original-fn))))

(defn mock! [func return original-fns]
  (let [func-var (resolve func)]
    (alter-var-root func-var
                    (fn [original-fn]
                      (swap! original-fns assoc func-var original-fn)
                      (fn [] return)))))

(defn create-mocks! [mocks original-fns]
  (let [mock-function-pairs (partition 2 mocks)]
    (doseq [[func return] mock-function-pairs]
      (mock! (first func) return original-fns))))

(defn mocks! [form]
  (let [mocks (second form)
        body (drop 2 form)]
    (potemkin/unify-gensyms
     `(let [original-fns## (atom {})]
        (create-mocks! '~mocks original-fns##)
        ~@body
        (revert-to! original-fns##)))))

(defn provided->mocks [form]
  (replace-in form
              [up-node]
              (by mocks!)
              (where #(= (current-node %) 'provided))))










