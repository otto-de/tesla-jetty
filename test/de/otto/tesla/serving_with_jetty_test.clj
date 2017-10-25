(ns de.otto.tesla.serving-with-jetty-test
  (:require [clojure.test :refer :all]
            [ring.adapter.jetty :as jetty]
            [de.otto.tesla.serving-with-jetty :as with-jetty]
            [de.otto.tesla.system :as system]
            [de.otto.tesla.util.test-utils :as tutils]
            [clj-http.client :as client]
            [de.otto.tesla.stateful.handler :as handlers]
            [de.otto.goo.goo :as goo]))

(deftest add-server-test
  (testing "it adds the jetty component to a system"
    (let [was-started (atom false)]
      (with-redefs [jetty/run-jetty (fn [_ _]
                                      (reset! was-started true)
                                      nil)]
        (goo/clear-default-registry!)
        (tutils/with-started [_ (system/start (with-jetty/add-server (system/base-system {})))]
                             (is (= true @was-started)))))))

(deftest integration-test
  (let [port 8081]
    (testing "it returns 404 for unknown paths"
      (goo/clear-default-registry!)
      (tutils/with-started [_ (with-jetty/add-server (system/base-system {:server-port port}))]
                           (is (= 404
                                  (:status (client/get (format "http://localhost:%d" port) {:throw-exceptions false}))))))
    (testing "it returns the response of matching routes"
      (goo/clear-default-registry!)
      (tutils/with-started [_ (with-jetty/add-server (system/base-system {:server-port port :status-url "/status-test"}))]
                           (is (= 200 (:status (client/get (format "http://localhost:%d/status-test" port) {:throw-exceptions false}))))))
    (testing "requests are recorded in the histogram"
      (goo/clear-default-registry!)
      (tutils/with-started [_ (with-jetty/add-server (system/base-system {:server-port port :status-url "/status-test"}))]
                           (client/get (format "http://localhost:%d/status-test" port) {:throw-exceptions false})
                           (is (= 1.0 (-> (goo/snapshot) (.raw) (.getSampleValue "http_duration_in_s_bucket"  (into-array String ["path", "method", "rc", "le"]) (into-array ["/status-test" , ":get", "200", "0.001"])))))))))

(deftest port-test
  (testing "uses the configured port"
    (let [config {:config {:server-port 8081}}]
      (is (= (with-jetty/port config) 8081))))
  (testing "uses 8080 as default"
    (let [config {:config {:config {}}}]
      (is (= (with-jetty/port config) 8080)))))

(deftest server-dependencies
  (with-redefs [jetty/run-jetty (fn [_ _] nil)]
    (testing "it starts up the server with no extra dependencies"
      (let [system-with-server (with-jetty/add-server (system/base-system {}))]
        (tutils/with-started [started (system/start system-with-server)]
                             (is (= #{:config :handler :jetty} (into #{} (keys (:server started))))))))

    (testing "it starts up the server with extra dependencies"
      (let [with-page          (assoc (system/base-system {}) :dummy-page (Object.))
            system-with-server (with-jetty/add-server with-page :dummy-page)]
        (tutils/with-started [started (system/start system-with-server)]
                             (is (= #{:config :handler :jetty :dummy-page} (into #{} (keys (:server started))))))))))

(deftest use-configurator
  (testing "using the configurator from config"
    (let [jetty-config (atom nil)]
      (with-redefs [jetty/run-jetty (fn [_ config]
                                      (reset! jetty-config config) nil)]
        (let [my-configurator    identity
              system-with-server (with-jetty/add-server (system/base-system {:jetty-options {:configurator my-configurator}}))
              started            (system/start system-with-server)
              stop               (system/stop started)]
          (is (= (:configurator @jetty-config) my-configurator))))))

  (testing "configurator should be extracted from config"
    (is (= (with-jetty/jetty-options {:jetty-options {:configurator "test"}}) {:configurator "test"}))))