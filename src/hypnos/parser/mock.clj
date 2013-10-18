(ns hypnos.parser.mock
  (:require
   [velcro.core :refer :all]
   [clojure.set :refer [difference]]))

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

(defn- mock-fn-from [context original-fn func-var args-return-map]
  (dosync
   (alter context assoc func-var {:original-fn original-fn
                                  :func func-var
                                  :number-of-calls 0
                                  :expected-with (keys args-return-map)
                                  :called-with []}))
  (fn [& args]
    (let [call-args (seq args)]
      (dosync
       (alter context update-in [func-var :number-of-calls] inc)
       (alter context update-in [func-var :called-with] conj call-args))
      (args-return-map call-args))))

(defn create-mocks! [expectations context]
  (doseq [[func args-return-map] (expectations->fns-returns expectations)]
    (let [func-var (resolve func)]
      (alter-var-root func-var
                      (fn [original-fn]
                        (mock-fn-from context
                                      original-fn
                                      func-var
                                      args-return-map))))))

(defn revert-to! [context]
  (doseq [{original-fn :original-fn func :func} (vals @context)]
    (alter-var-root func (fn [_] original-fn))))

(defn assert-mocks [context errors]
  (doseq [{called-with :called-with
           expected-with :expected-with
           number-of-calls :number-of-calls
           :as func-context} (vals @context)]
    (let [called-set (set called-with)
          expected-set (set expected-with)]
      (when (not (= called-set expected-set))
        (swap! errors conj (merge func-context {:type :mock-args
                                                :unexpected-args (difference (set called-set) expected-set)}))))
    
    (when (<= number-of-calls 0)
      (swap! errors conj (assoc func-context :type :mock-number-of-calls)))))

(defn mocks! [errors]
  (fn [form]
    (let [expectations (second form)
          body (drop 2 form)]
      `(let [context# (ref {})]
         (create-mocks! '~expectations context#)
         ~@body
         (revert-to! context#)
         (assert-mocks context# ~errors)))))

(defn expectations->mocks [errors]
  (fn [form]
    (replace-in form
                [up-node]
                (by (mocks! errors))
                (where #(= (current-node %) 'provided)))))
