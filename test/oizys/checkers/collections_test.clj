(ns oizys.checkers.collections
  (:require
   [oizys.core :refer :all]))

(future-fact "contains some"
  [1 2 3] => (contains [2 3])
  '(1 2 3) => (contains '(2 3))
  #{1 2 3} => (contains #{2 3})
  {:animals {:meerkats {:babies 1
                        :teenagers 2
                        :adults 3}}}
  => (contains {:babies 1})
  {:animals {:meerkats {:babies 1
                        :teenagers 2
                        :adults 3}}}
  => (contains {_ {_ {:babies 1}}}))

(future-fact "starts-with"
  [1 2 3] => (starts-with [1 2])
  '(1 2 3) => (starts-with '(1 2))
  #{1 2 3} => (contains #{1 2})

(future-fact "ends-with"
  [1 2 3] => (ends-with [2 3])
  '(1 2 3) => (ends-with '(2 3))
  #{1 2 3} => (ends-with #{2 3}))
