(ns oizys.checkers.collections-test
  (:require
   [oizys.core :refer :all]))

(facts "about contains"
  (facts "for vectors"
    (fact "simple matching"
      [1 2 3] => (matches [1 2 3]))

    (fact "with bindings"
      (let [a 1
            b 2
            c 3]
        [1 2 3] => (matches [a b c])))

    (fact "with placeholder"
      [1 2 3] => (matches [1 _ 3])
      [1 2 3] => (matches [1 _ _])
      [1 2 3] => (matches [_ _ _]))

    (fact "with rest"
      [1 2 3] => (matches [1 & r])
      [1 2 3] => (matches [1 2 & r]))

    (lie "matches"
      (let [d 4]
        [1 2 3] => (matches [1 2 3 d])
        [1 2 3] => (matches [1 2 4])
        [1 2 3] => (matches [1 _ 4])
        [1 2 3] => (matches [_ _ 4])))))
