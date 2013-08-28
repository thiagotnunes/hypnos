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

    (fact "with or patterns"
      [1 2 3] => (matches [1 (:or 2 3 4 5) 3]))

    (lie "matches"
      (let [d 4]
        [1 2 3] => (matches [1 2 4])
        [1 2 3] => (matches [1 2 3 d])
        [1 2 3] => (matches [1 _ 4])
        [1 2 3] => (matches [_ _ 4])
        [1 2 3] => (matches [1 2 3 4 & r])
        [1 2 3] => (matches [2 3 & r])
        [1 2 3] => (matches [1 (:or 3 4 5) 3]))))

  (facts "for maps"
    (fact "simple matching"
      {:a 1 :b 2} => (matches {:a 1 :b 2})
      {:d 4 :a 1 :b 2 :c 3} => (matches {:a 1 :b 2}))
    
    (fact "with only matching"
      {:a 1 :b 2} => (matches ({:a 1 :b 2} :only [:a :b])))

    (fact "with placeholder"
      {:a 1 :b 2} => (matches {:a _})
      {:nested {:a 1 :b 2}} => (matches {:nested {:a _}}))

    (lie "matches"
      {:a 1 :b 2} => (matches {:a 1 :b 1})
      {:a 1 :b 2} => (matches {:c 1 :b 2})
      {:a 1 :b 2 :c 3} => (matches ({:a 1 :b 2} :only [:a :b]))))

  (facts "for sets"
    (fact "simple matching"
      #{1 2 3} => (matches #{1 2 3}))
    
    (lie "matches"
      #{1 2 3} => (matches #{1 2 4})
      #{1 2 3} => (matches #{1 2}))))

(facts "about starts-with"
  (fact "for vectors"
    [1 2 3] => (starts-with [1])
    [1 2 3] => (starts-with [1 2])
    [1 2 3] => (starts-with [1 2 3]))

  (fact "for strings"
    "hello there" => (starts-with "h")
    "hello there" => (starts-with "hello")
    "hello there" => (starts-with "hello there"))

  (lie "for vectors"
    [1 2 3] => (starts-with [2])
    [1 2 3] => (starts-with [1 2 3 4 5]))

  (lie "for strings"
    "hello there" => (starts-with "ello")
    "hello there" => (starts-with "hello there my friend")))

(facts "about ends-with"
  (fact "for vectors"
    [1 2 3] => (ends-with [3])
    [1 2 3] => (ends-with [2 3])
    [1 2 3] => (ends-with [1 2 3]))

  (fact "for strings"
    "hello there" => (ends-with "e")
    "hello there" => (ends-with "there")
    "hello there" => (ends-with "hello there"))

  (lie "for vectors"
    [1 2 3] => (ends-with [1])
    [1 2 3] => (ends-with [1 2 3 4]))

  (lie "for strings"
    "hello there" => (ends-with "ther")
    "hello there" => (ends-with "hello there my friend")))
