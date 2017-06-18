(ns stopwatch.impl
  (:require [net.cgrand.macrovich :as macro])
  #? (:cljs (:require-macros
              [net.cgrand.macrovich :as macro]
              [stopwatch.impl :refer [nano->ms ms->nano sec->nano ms->sec]])))

(macro/deftime

(defmacro nano->ms  [n] `(/ ~n 1000 1000))
(defmacro ms->nano  [n] `(* ~n 1000 1000))
(defmacro sec->nano [n] `(ms->nano (* ~n 1000)))
(defmacro ms->sec   [n] `(/ ~n 1000))

)

(macro/usetime

#? (:cljs (defn oget [o k]
            (when o (aget o k))))

;; Browser - Performance.now
#? (:cljs (def +?window        (when (exists? js/window) js/window)))
#? (:cljs (def +?performance   (if (exists? js/performance)
                                           js/performance
                                           (oget +?window "performance"))))
;;
#? (:cljs (def +?perf-now      (or (oget +?performance "now")
                                   (oget +?performance "mozNow")
                                   (oget +?performance "msNow")
                                   (oget +?performance "oNow")
                                   (oget +?performance "webkitNow"))))

;; Node - process.hrtime
#? (:cljs (def +?process       (when (exists? js/process) js/process)))
#? (:cljs (def +?hrtime        (oget +?process "hrtime")))

#? (:cljs (def +has-performance? (boolean +?perf-now)))
#? (:cljs (def +has-hrtime?      (boolean +?hrtime)))

)

(macro/deftime

;; result = Now ms +-5u (elapsed since timing.navigationStart)
(defmacro performance-now []
  `(macro/case :cljs (.call +?perf-now +?performance)))

;; result = [sec, nano] (relative to an arbitrary number in the past)
(defmacro hrtime-now []
  `(macro/case :cljs (+?hrtime)))

(defmacro hrtime-since [v]
  `(macro/case :cljs (+?hrtime ~v)))

)
