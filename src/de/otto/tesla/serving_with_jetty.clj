(ns de.otto.tesla.serving-with-jetty
  (:require [com.stuartsierra.component :as c]
            [ring.adapter.jetty :as jetty]
            [de.otto.tesla.stateful.handler :as handler]
            [clojure.tools.logging :as log]))

(defn port [config]
  (if-let [from-config (get-in config [:config :server-port])]
    (Integer. from-config)
    8080))

(defrecord JettyServer [config handler]
  c/Lifecycle
  (start [self]
    (log/info "-> starting server")
    (let [all-routes (handler/handler handler)
          server (jetty/run-jetty all-routes {:port (port config) :join? false})]
      (assoc self :jetty server)))

  (stop [self]
    (log/info "<- stopping server")
    (if-let [server (:jetty self)]
      (.stop server))
    self))

(defn new-server [] (map->JettyServer {}))

(defn add-server [base-system & server-dependencies]
  (assoc base-system :server (c/using (new-server) (reduce conj [:config :handler] server-dependencies))))
