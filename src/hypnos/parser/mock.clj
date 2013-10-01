(ns hypnos.parser.mock
  (:require
   [velcro.core :refer :all]))

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

(defn- expectations->fns-returns [expectations]
  (->> expectations
       (partition 2)
       (group-by function-name)
       (map format-mocks)
       (into {})))

(defn- mock-fn-from [args-return-map]
  (fn [& args]
    (args-return-map (seq args))))

(defn create-mocks! [expectations context]
  (doseq [[func args-return-map] (expectations->fns-returns expectations)]
    (let [func-var (resolve func)]
      (alter-var-root func-var
                      (fn [original-fn]
                        (swap! context conj {:original-fn original-fn
                                             :func func-var
                                             :number-of-calls 0
                                             :expected-with (keys args-return-map)
                                             :called-with []})
                        (mock-fn-from args-return-map))))))

(defn revert-to! [context]
  (doseq [{original-fn :original-fn func :func} @context]
    (alter-var-root func (fn [_] original-fn))))

(defn mocks! [errors]
  (fn [form]
    (let [expectations (second form)
          body (drop 2 form)]
      `(let [context# (atom [])]
         (create-mocks! '~expectations context#)
         ~@body
         (revert-to! context#)))))

(defn expectations->mocks [errors]
  (fn [form]
    (replace-in form
                [up-node]
                (by (mocks! errors))
                (where #(= (current-node %) 'provided)))))
