(ns hypnos.zip-test
  (:require
   [hypnos.core :refer :all]
   [hypnos.zip  :refer :all]

   [clojure.zip :as zip]))

(fact "replaces the current node with symbol"
  (letfn [(minus [_] '-)]
    
    (replace-in '(+ 1 2)
                [current-node]
                (by minus)
                (where #(= (current-node %) '+))) => '(- 1 2)))

(fact "replaces the current node with list"
  (letfn [(sum [_] '(+ 6 7))]
    
    (replace-in '(+ 1 2)
                [current-node]
                (by sum)
                (where #(= (current-node %) 1)))
    => '(+ (+ 6 7) 2)))

(fact "replaces all the occurrences"
  (letfn [(sum [_] '(+ 6 7))]
    
    (replace-in '(+ 1 2 1 3 1 4)
                [current-node]
                (by sum)
                (where #(= (current-node %) 1)))
    => '(+ (+ 6 7) 2 (+ 6 7) 3 (+ 6 7) 4)))

(fact "replaces left, right and current node"
  (letfn [(assertion->fn [actual expected assertion] (list 'assertion-fn actual expected))]
    (replace-in '(test "test" 1 -> 2)
                [left-node right-node current-node]
                (by assertion->fn)
                (where #(= (current-node %) '->)))
    => '(test "test" (assertion-fn 1 2))))
