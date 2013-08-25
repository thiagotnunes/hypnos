(defproject oizys "0.1.0"
  :description "Testing framework for clojure"
  :url "http://www.thiagotnunes.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/algo.monads "0.1.4"]
                 [slingshot "0.10.3"]
                 [potemkin "0.3.2"]
                 [colorize "0.1.1" :exclusions [org.clojure/clojure]]
                 [org.clojure/core.match "0.2.0-rc5"]])
