(ns stopwatch.core
  (:require [stopwatch.impl :as impl]
            [net.cgrand.macrovich :as macro])
  #? (:cljs (:require-macros
              [stopwatch.impl :as impl]
              [net.cgrand.macrovich :as macro])))

(macro/usetime

#? (:cljs (def ^:private +?performance-now-stopwatch
            (when impl/+has-performance?
              (fn []
                (let [start (impl/performance-now)]
                  (fn []
                    (let [end (impl/performance-now)]
                      (impl/ms->nano (- end start)))))))))

#? (:cljs (def ^:private +?process-hrtime-stopwatch
            (when impl/+has-hrtime?
              (fn []
                (let [start (impl/hrtime-now)]
                  (fn []
                    (let [[diff-sec diff-nano] (impl/hrtime-since start)]
                      (+ (impl/sec->nano diff-sec) diff-nano))))))))

#? (:cljs (defn- ms-date-stopwatch []
            (let [start (.getTime (js/Date.))]
              (fn []
                (let [end (.getTime (js/Date.))]
                  (impl/ms->nano (- end start)))))))

#? (:cljs (def ^:private start* (or +?performance-now-stopwatch
                                    +?process-hrtime-stopwatch
                                    ms-date-stopwatch)))

(defn start
  "Creates and starts the stopwatch.

  The stopwatch is a function which returns the elapsed time in units of
  nanosecond:

    (let [elapsed (start)]
      (do-work)
      (println \"Elapsed:\" (elapsed) \"ns\"))

  Uses the most precise mechanism available on the target platform."
  []
  #? (:cljs (start*)
      :clj  (let [start (System/nanoTime)]
              (fn []
                (let [end (System/nanoTime)]
                  (- end start))))))

)
