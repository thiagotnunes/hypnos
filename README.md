# oizys

A Clojure testing library

## Usage

```
(facts "about the initial tests"
  (fact "this should pass"
    1 => 1)

  (fact "this should not pass"
    1 => 2))
```

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
