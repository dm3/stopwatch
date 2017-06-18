(ns stopwatch.clock
  (:require [net.cgrand.macrovich :as macro]
            [stopwatch.impl :as impl])
  #? (:cljs (:require-macros
              [net.cgrand.macrovich :as macro]
              [stopwatch.impl :refer [nano->ms ms->nano sec->nano ms->sec]])))

(macro/usetime

#? (:cljs (def ^:private +?performance-now-ms
            (when impl/+has-performance?
              (fn [] (impl/performance-now)))))

#? (:cljs (def ^:private +?performance-now-nanos
            (when impl/+has-performance?
              (fn [] (ms->nano (impl/performance-now))))))

;;;;;;;;; Public

;; 64 bit float can represent integers exactly up to 9,007,199,254,740,993
;; which is ~9,000,000 seconds or ~250 hours.
;; Therefore, converting [sec, nano] representations to nanos should have
;; the same precision until the process

;; result = nano (relative to an arbitrary point in the past)
#? (:cljs (def ^:private +?process-hrtime-now-nanos
            (when impl/+has-hrtime?
              (fn []
                (let [[sec nano] (impl/hrtime-now)]
                  (+ (sec->nano sec) nano))))))

#? (:cljs (def nanos*
            (or +?performance-now-nanos
                +?process-hrtime-now-nanos
                (fn [] (ms->nano (.getTime (js/Date.)))))))

;;;;;; Since load
#? (:cljs (def ^:private +?perf-start-ms (-> impl/+?performance (impl/oget "timing")
                                             (impl/oget "navigationStart"))))
#? (:cljs (def ^:private +?load-hrtime   (when impl/+has-hrtime?
                                           (impl/hrtime-now))))
(def                     +?start-nano #? (:cljs (ms->nano (.getTime (js/Date.)))
                                          :clj  (System/nanoTime)))

;; result = Epoch ms +-5u
#? (:cljs (def ^:private +?performance-since-load-nanos
            (when impl/+has-performance?
              (fn [] (ms->nano
                       (- (impl/performance-now) +?perf-start-ms))))))

#? (:cljs (def ^:private +?process-hrtime-since-load-nanos
            (when impl/+has-hrtime?
              (fn []
                (let [[diff-sec diff-nano] (impl/hrtime-since +?load-hrtime)]
                  (+ (sec->nano diff-sec) diff-nano))))))

#? (:cljs (def nanos-since-load*
            (or +?performance-since-load-nanos
                +?process-hrtime-since-load-nanos
                (fn [] (- (ms->nano (.getTime (js/Date.)))
                          +?start-nano)))))

)

(macro/deftime

(defmacro epoch
  "Returns the curent epoch time in milliseconds."
  []
  `(macro/case :cljs (.getTime (js/Date.))
               :clj  (System/currentTimeMillis)))

(defmacro nanos
  "Returns the curent nanoseconds counted from an arbitrary point in the past."
  []
  `(macro/case :cljs (nanos*)
               :clj  (System/nanoTime)))

(defmacro nanos-since-load
  "Returns the nanoseconds since the load of this namespace."
  []
  `(macro/case :cljs (nanos-since-load*)
               :clj  (- (System/nanoTime) +?start-nano)))
)
