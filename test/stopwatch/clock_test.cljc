(ns stopwatch.clock-test
  (:require [clojure.test :refer [deftest testing is]]
            [stopwatch.clock :as sut])
  #? (:cljs (:require-macros [stopwatch.clock :as sut])))

(defn- do-work [times]
  (let [!result (atom 0)]
    (dotimes [i times]
      (reset! !result (sut/nanos)))
    @!result))

(deftest current-nanos
  (let [start (sut/nanos)]
    (do-work 1e6)
    (is (< start (sut/nanos)))))

(deftest current-epoch
  (let [start (sut/epoch)]
    (do-work 1e6)
    (is (< start (sut/epoch)))))

(deftest nanos-since-load
  (let [start (sut/nanos-since-load)]
    (do-work 1e6)
    (is (< start (sut/nanos-since-load)))))
