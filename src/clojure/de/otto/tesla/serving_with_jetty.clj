(ns de.otto.tesla.serving-with-jetty
  (:require [com.stuartsierra.component :as c]
            [compojure.core :as comp]
            [ring.adapter.jetty :as jetty]
            [de.otto.tesla.stateful.handler :as handler]
            [clojure.tools.logging :as log]
            [de.otto.goo.goo :as goo]
            [ring.util.response :as resp])
  (:import (org.eclipse.jetty.server.handler StatisticsHandler)
           (io.prometheus.client.jetty JettyStatisticsCollector)
           (org.eclipse.jetty.server Server)
           (de.otto.tesla.jetty LatencyHistogramHandler)))

(defn jetty-options [config]
  (if-let [jetty-options (or (get config :jetty-options) (get-in config [:config :jetty-options]))]
    jetty-options
    {}))

(defn port [config]
  (if-let [from-config (get-in config [:config :server-port])]
    (Integer. from-config)
    8080))

(defn instrument-jetty [^Server server]
  (let [prometheus-wrapper              (LatencyHistogramHandler. (.raw (goo/snapshot)))
        statistics-handler              (StatisticsHandler.)]
    (.setHandler statistics-handler prometheus-wrapper)
    (.setHandler prometheus-wrapper (.getHandler server))
    (.setHandler server prometheus-wrapper)
    (goo/register! (JettyStatisticsCollector. statistics-handler))))

(defrecord JettyServer [config handler]
  c/Lifecycle
  (start [self]
    (log/info "-> starting server")
    (let [handler-404 (comp/ANY "*" _request (resp/status (resp/response "") 404))
          all-routes  (comp/routes (handler/handler handler) handler-404)
          options     (jetty-options (:config self))
          server      (jetty/run-jetty all-routes (merge {:port (port config) :join? false :configurator instrument-jetty} options))]
      (assoc self :jetty server)))
  (stop [{jetty :jetty :as self}]
    (log/info "<- stopping server")
    (when jetty
      (.stop jetty))
    self))

(defn new-server [] (map->JettyServer {}))

(defn add-server [base-system & server-dependencies]
  (assoc base-system :server (c/using (new-server) (reduce conj [:config :handler] server-dependencies))))
