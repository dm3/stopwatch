# stopwatch

[![Build Status](https://travis-ci.org/dm3/stopwatch.png?branch=master)](https://travis-ci.org/dm3/stopwatch)

A Clojure/script stopwatch implementation.

Uses `System/nanoTime` on the JVM.

Uses the most precise mechanism depending on the Javascript runtime:

  * [Performance.now](https://developer.mozilla.org/en-US/docs/Web/API/Performance/now)
  * [process.hrtime](https://nodejs.org/api/process.html#process_process_hrtime_time)
  * [js/Date](https://docs.microsoft.com/en-us/scripting/javascript/reference/date-object-javascript)

## Usage

Add the following dependency to your project.clj or build.boot:

```clojure
[dm3/stopwatch "0.1.1"]
```

then require the namespace:

```clojure
(require '[stopwatch.core :as stopwatch])
```

Using synchronously:

```clojure
(let [elapsed (stopwatch/start)]
  (do-work)
  (println "Elapsed: " (elapsed) "ns"))
```

and asynchronously, using core.async:

```clojure
(let [elapsed (stopwatch/start)]
  (go
    (<! (do-work))
    (println "Elapsed: " (elapsed) "ns")))
```

## License

Copyright © 2017 Vadim Platonov

Distributed under the MIT License.
