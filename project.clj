(defproject de.otto/tesla-jetty "0.1.4-SNAPSHOT"
  :description "basic microservice."
  :url "https://github.com/otto-de/tesla-jetty"
  :license {:name "Apache License 2.0"
            :url  "http://www.apache.org/license/LICENSE-2.0.html"}
  :scm {:name "git"
        :url  "https://github.com/otto-de/tesla-jetty"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-jetty-adapter "1.6.2"]]
  :lein-release {:deploy-via :clojars}
  :exclusions [org.clojure/clojure
               org.slf4j/slf4j-nop
               org.slf4j/slf4j-log4j12
               log4j
               commons-logging/commons-logging]

  :profiles {:provided {:dependencies [[de.otto/tesla-microservice "0.11.10"]
                                       [com.stuartsierra/component "0.3.2"]]}
             :dev      {:dependencies [[javax.servlet/servlet-api "2.5"]
                                       [org.slf4j/slf4j-api "1.7.25"]
                                       [ch.qos.logback/logback-core "1.2.3"]
                                       [ch.qos.logback/logback-classic "1.2.3"]
                                       [ring-mock "0.1.5"]]
                        :plugins      [[lein-release/lein-release "1.0.9"]]}}
  :test-paths ["test" "test-resources"])
