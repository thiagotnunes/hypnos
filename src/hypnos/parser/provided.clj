(ns hypnos.parser.provided
  (:require
   [velcro.core :refer :all]
   [potemkin :as potemkin]))

(defn revert-to [original-fns]
  (doseq [[func original-fn] @original-fns]
    (alter-var-root func (fn [_] original-fn))))

(defn mock [func mock-fn original-fns]
  `(alter-var-root (var ~func)
                   (fn [original-fn##]
                     (swap! ~original-fns assoc (var ~func) original-fn##)
                     (fn [] ~mock-fn))))

(defn- mocks [form]
  (let [mocks (second form)
        func (-> mocks first first)
        mock-fn (-> mocks second)
        body (drop 2 form)]
    (potemkin/unify-gensyms
     `(let [original-fns## (atom {})]
        ~(mock func mock-fn `original-fns##)
        ~@body
        (revert-to original-fns##)))))

(defn provided->mocks [form]
  (replace-in form
              [up-node]
              (by mocks)
              (where #(= (current-node %) 'provided))))










