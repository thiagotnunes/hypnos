# Hypnos

A Clojure lighter weight testing library based on midje.

## Usage

Add this line to your project dependencies:

```clojure
[hypnos "0.0.1"]
```

Currently, hypnos tests should be evaluated in the REPL.

Simple equality tests:

```clojure
(ns my-test
  (:require
    [hypnos.core :refer :all]))
    
(facts "about equality"

  (fact "simple equality tests are done using the => matcher"
    (+ 1 1) => 2))
    
  (fact "several assertions can be done in the same test"
    (let [a 2
          b 2]
      (* a b) => 4
      (/ a b) => 1
      [a b] => [1 2])))
```

Simple checkers:

```clojure
(facts "about simple checkers"
  (fact "not checker negates the expected value"
    1 => (not 2))

  (fact "it is truthy when it is not nil nor false"
    1  => truthy
    [] => truthy
    nil => (not truthy))
  
  (fact "it is falsey when it is nil or false"
    false => falsey
    nil => falsey
    1 => (not falsey))
  
  (fact "exceptions can be tested with the throws checker"
    (letfn [(bang! [] (throw (RuntimeException. "bang!")))]
      (bang!) => (throws RuntimeException)))
    
  (fact "roughly checker defaults to 0.1 error delta"
    0.11 => (roughly 0.1)    ; default to 0.1 error delta
    100.3 => (roughly 100 1) ; custom error delta of 1))

```

Collection checkers:

```clojure
; Matches for collections uses clojure.core.match underneath
(facts "about matches checker for collections"
  (fact "for vectors"
    [1 2 3] => (matches [1 _ 3]) ; _ can be anything
    [1 2 3] => (matches [1 & r]) ; matches 1 at the beginning and ignores the rest
    [1 2 3] => (not (matches [_ 4 _])))
    
  (fact "for maps"
    {:a 1 :b 2 :c 3} => (matches {:a _ :b 2}) ; map may contain other keys
    {:a 1 :b 2} => (matches {:a _ :b 2} :only [:a :b]) ; map should contain only the :a and :b keys)
  
  (fact "for sets"
    #{1 2 3} => (matches #{1 2})))

  
(facts "about starts-with checker"
  (fact "for vectors"
    [1 2 3] => (starts-with [1 2])
    [1 2 3] => (not (starts-with [2]))))
    
(facts "about ends-with checker"
  (fact "for vectors"
    [1 2 3] => (ends-with [2 3])
    [1 2 3] => (not (ends-with [1 2])))) 

    
(facts "about the has checker"
  (fact "verifies that every elements of the collection"
    [1 3 5]  => (has every? odd?)
    [1 3 5]  => (not (has every? even?)))

  (fact "verifies that not any element of the collection"
    #{1 3 5} => (has not-any? even?))

  (fact "verifies that some elements of the collection"
    '(1 2 3) => (has some odd?))

  (fact "verifies that not every element of the collection"
    [1 2 3]  => (has not-every? odd?))
    
  (fact "multiple functions can be given"
    #{1 2 3} => (has some odd? #(< % 6) #(> % 0)))
```

String checkers:

```clojure
(facts "about matches checker for strings"    
  (fact "does regex comparison"
    "hypnos 123" => (matches #"[a-z]+ \d\d\d")))
  
(facts "about starts-with checker"   
  (fact "for strings"
    "hypnos 123" => (starts-with "hyp")))
    
(facts "about ends-with checker"  
  (fact "for strings"
    "hypnos 123" => (ends-with "23"))) 
```

Custom checkers:

```clojure
(defchecker zero? [actual]
  (= 0 actual))
  
(fact "is zero"
  (- 1 1) => zero?)
  
(defchecker instance-of [actual expected]
  (instance? expected actual))
  
(fact "is valid"
  1 => (instance-of Number))
  
(defchecker contain-keys? [actual & args]
  (->> args
       (map #(contains? actual %))
       (every? true?)))
  
(fact "contain keys"
  {:a 1 :b 2 :c 3} => (contain-keys? :a :b :c))
```

Pending tests:

```clojure
(facts "about pending"
  (future-fact "this will not be evaluated"
    (throw (RuntimeException. "should not throw this"))
    1 => 2))
  
; outputs => PENDING: "about pending - this will not be evaluated"
```


## License

(The MIT License)

Copyright (c) 2013 Thiago Tasca Nunes

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 'Software'), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
