(ns hypnos.checkers.collections-test
  (:require
   [hypnos.core :refer :all]))

(facts "about matches"
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

    (failing-fact "matches"
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

    (failing-fact "matches"
      {:a 1 :b 2} => (matches {:a 1 :b 1})
      {:a 1 :b 2} => (matches {:c 1 :b 2})
      {:a 1 :b 2 :c 3} => (matches ({:a 1 :b 2} :only [:a :b]))))

  (facts "for sets"
    (fact "simple matching"
      #{1 2 3} => (matches #{1 2 3}))
    
    (failing-fact "matches"
      #{1 2 3} => (matches #{1 2 4})
      #{1 2 3} => (matches #{1 2})))

  (facts "for strings"
    (fact "regex matching"
      "hello there" => (matches #"[a-z]* [a-z]*"))

    (failing-fact "regex matching"
      "hello there" => (matches #"[a-z]*"))))

(facts "about starts-with"
  (fact "for vectors"
    [1 2 3] => (starts-with [1])
    [1 2 3] => (starts-with [1 2])
    [1 2 3] => (starts-with [1 2 3]))

  (fact "for strings"
    "hello there" => (starts-with "h")
    "hello there" => (starts-with "hello")
    "hello there" => (starts-with "hello there"))

  (failing-fact "for vectors"
    [1 2 3] => (starts-with [2])
    [1 2 3] => (starts-with [1 2 3 4 5]))

  (failing-fact "for strings"
    "hello there" => (starts-with "ello")
    "hello there" => (starts-with "hello there my friend"))

  (fact "for unsupported types"
    (#{1 2 3} => (starts-with #{3})) => (throws RuntimeException)
    ({1 2} => (starts-with #{2})) => (throws RuntimeException)))

(facts "about ends-with"
  (fact "for vectors"
    [1 2 3] => (ends-with [3])
    [1 2 3] => (ends-with [2 3])
    [1 2 3] => (ends-with [1 2 3]))

  (fact "for strings"
    "hello there" => (ends-with "e")
    "hello there" => (ends-with "there")
    "hello there" => (ends-with "hello there"))

  (failing-fact "for vectors"
    [1 2 3] => (ends-with [1])
    [1 2 3] => (ends-with [1 2 3 4]))

  (failing-fact "for strings"
    "hello there" => (ends-with "ther")
    "hello there" => (ends-with "hello there my friend"))

  (fact "for unsupported types"
    (#{1 2 3} => (ends-with #{3})) => (throws RuntimeException)
    ({1 2} => (ends-with #{2})) => (throws RuntimeException)))

(fact "negating checkers"
  [1 2 3] => (not (matches [1 _ 4]))
  [1 2 3] => (not (starts-with [2]))
  [1 2 3] => (not (ends-with [2])))


(facts "checking collection elements against functions"
  (fact "verifies that every? elements of the collection"
    [1 3 5]  => (has every? odd?))

  (fact "verifies that not any element of the collection"
    #{1 3 5} => (has not-any? even?))

  (fact "verifies that some elements of the collection"
    '(1 2 3) => (has some odd?))

  (fact "verifies that not every element of the collection"
    [1 2 3]  => (has not-every? odd?))

  (failing-fact "when quantifier fails"
    '(1 2 5) => (has every? odd?))

  (fact "multiple functions are given"
    #{1 2 3} => (has some odd? #(< % 6) #(> % 0)))

  (failing-fact "when one of the functions fails"
    #{1 2 3} => (has some odd? #(< % 1) #(> % 0))))
