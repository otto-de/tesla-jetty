(defproject de.otto/tesla-jetty "0.2.9-SNAPSHOT"
  :description "basic microservice."
  :url "https://github.com/otto-de/tesla-jetty"
  :license {:name "Apache License 2.0"
            :url  "http://www.apache.org/license/LICENSE-2.0.html"}
  :scm {:name "git"
        :url  "https://github.com/otto-de/tesla-jetty"}

  :dependencies [[org.clojure/clojure "1.10.2"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [io.prometheus/simpleclient_jetty "0.1.0"]
                 ;Overwrite outdated,transitive dependencies
                 [org.eclipse.jetty/jetty-server "9.4.8.v20171121"]
                 [org.eclipse.jetty/jetty-servlet "9.4.8.v20171121"]]
  :lein-release {:deploy-via :clojars}
  :exclusions [org.clojure/clojure
               org.slf4j/slf4j-nop
               org.slf4j/slf4j-log4j12
               log4j]
  :source-paths      ["src/clojure"]
  :java-source-paths ["src/java"]
  :javac-options ["-source" "8" "-target" "8"]
  :profiles {:provided {:dependencies [[de.otto/tesla-microservice "0.11.16"]
                                       [com.stuartsierra/component "0.3.2"]]}
             :dev      {:dependencies [[org.slf4j/slf4j-api "1.7.25"]
                                       [ch.qos.logback/logback-core "1.2.3"]
                                       [ch.qos.logback/logback-classic "1.2.3"]
                                       [ring-mock "0.1.5"]
                                       [clj-http "3.7.0"]]
                        :plugins      [[lein-release/lein-release "1.0.9"]]}}
  :test-paths ["test" "test-resources"])
