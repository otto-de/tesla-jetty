(defproject de.otto/tesla-jetty "0.3.0"
  :description "basic microservice."
  :url "https://github.com/otto-de/tesla-jetty"
  :license {:name "Apache License 2.0"
            :url  "http://www.apache.org/license/LICENSE-2.0.html"}
  :scm {:name "git"
        :url  "https://github.com/otto-de/tesla-jetty"}

  :dependencies [[org.clojure/clojure "1.11.1"]
                 [info.sunng/ring-jetty9-adapter "0.30.3"]
                 [info.sunng/ring-jetty9-adapter-http3 "0.4.3"]]
  :lein-release {:deploy-via :clojars}
  :exclusions [org.clojure/clojure
               org.slf4j/slf4j-nop
               org.slf4j/slf4j-log4j12
               log4j]
  :source-paths      ["src/clojure"]
  :java-source-paths ["src/java"]
  :javac-options ["-source" "8" "-target" "8"]
  :profiles {:provided {:dependencies [[org.slf4j/slf4j-api "2.0.6"]
                                       [de.otto/tesla-microservice "0.17.4"]
                                       [com.stuartsierra/component "1.1.0"]]}
             :dev      {:dependencies [
                                       [ch.qos.logback/logback-core "1.4.5"]
                                       [ch.qos.logback/logback-classic "1.4.5"]
                                       [ring-mock "0.1.5"]
                                       [clj-http "3.12.3"]]
                        :plugins      [[lein-release/lein-release "1.0.9"]]}}
  :test-paths ["test" "test-resources"])
