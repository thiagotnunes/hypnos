(ns hypnos.parser.provided
  (:require
   [velcro.core :refer :all]))

(defn- revert [func original-fn]
  `(alter-var-root (var ~func) (fn [new-fn] ~original-fn)))

(defn- mocks [_ to-mock]
  (let [func (-> to-mock first first)
        mock-fn (second to-mock)]
    `(do
       (alter-var-root (var ~func)
                       (fn [original-fn#]
                         (fn [] ~mock-fn))))))

(defn provided->mocks [form]
  (replace-in form
              [current-node right-node]
              (by-spliced mocks)
              (where #(= (current-node %) 'provided))))










