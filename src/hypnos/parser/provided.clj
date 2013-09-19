(ns hypnos.parser.provided
  (:require
   [velcro.core :refer :all]
   [potemkin    :as potemkin]))

(defn- function-name [element]
  (-> element first first))

(defn- format-mock [mock]
  (let [args (-> mock first rest seq)
        return (second mock)]
    [args return]))

(defn- format-mocks [[func mocks]]
  (let [formatted (->> mocks
                       (map format-mock)
                       (into {}))]
    [func formatted]))

(defn- mock-fn-from [mock]
  (fn [& args]
    (let [return (mock (seq args))]
      return)))

(defn- mock! [func mock original-fns]
  (let [func-var (resolve func)]
    (alter-var-root func-var
                    (fn [original-fn]
                      (swap! original-fns assoc func-var original-fn)
                      (mock-fn-from mock)))))

(defn revert-to! [original-fns]
  (doseq [[func original-fn] @original-fns]
    (alter-var-root func (fn [_] original-fn))))

(defn create-mocks! [mocks original-fns]
  (let [function-mocks-pairs (->> mocks
                                  (partition 2)
                                  (group-by function-name)
                                  (map format-mocks)
                                  (into {}))]
    (doseq [[func mock] function-mocks-pairs]
      (mock! func mock original-fns))))

(defn mocks-fn-from! [errors]
  (fn [form]
    (let [mocks (second form)
          body (drop 2 form)]
      (potemkin/unify-gensyms
       `(let [original-fns## (atom {})]
          (create-mocks! '~mocks original-fns##)
          ~@body
          (revert-to! original-fns##))))))

(defn provided->mocks [errors]
  (fn [form]
    (replace-in form
                [up-node]
                (by (mocks-fn-from! errors))
                (where #(= (current-node %) 'provided)))))










