#tesla-jetty

This library provides a component, that adds an embedded jetty server to [tesla-microservice](https://github.com/otto-de/tesla-mivroservice). 
This componenent has been extracted from tesla-microservice in order to allow for operation with other, especially non-blocking, server implementations as well as headless operation.

[![Clojars Project](http://clojars.org/de.otto/tesla-jetty/latest-version.svg)](http://clojars.org/de.otto/tesla-jetty)


[![Build Status](https://travis-ci.org/otto-de/tesla-jetty.svg)](https://travis-ci.org/otto-de/tesla-jetty)
[![Dependencies Status](http://jarkeeper.com/otto-de/tesla-jetty/status.svg)](http://jarkeeper.com/otto-de/tesla-jetty)

## Configuration

The config ```:server-port``` will be used as port. Default is ```8080```. 

## Usage

Because tesla-microservice is a provided dependency, you must always specify two dependencies in your project clj:

```clojure
:dependencies [[de.otto/tesla-microservice "0.1.15"]
               [de.otto/tesla-jetty "0.1.0"]]
```
Add the server to the base-system before starting it. Pass in additional dependencies of the server (e.g. ```:my-page```): 
```clojure
  (system/start (serving-with-jetty/add-server (system/base-system {}) :my-page))
```

See [tesla-examples/simple-example](https://github.com/otto-de/tesla-examples/tree/master/simple-example) for an example. The more elegant syntax with the ```->```-threading macro would look like this:

```clojure
(system/start
  (-> (system/base-system {})
      (assoc :my-page (new-page))
      (serving-with-jetty/add-server :my-page)))
```
See [tesla-examples/mongo-example](https://github.com/otto-de/tesla-examples/tree/master/mongo-example).

## Compatibility
Versions ```0.1.0``` and above of tesla-jetty are compatible with versions ```0.1.15``` and above of tesla-microservice.
Versions ```0.3.0``` and above switched to jetty-12, but should be compatible with versions ```0.17.5``` and above of tesla-microservice

## License
Apache License
