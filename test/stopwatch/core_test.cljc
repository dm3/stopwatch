(ns stopwatch.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [stopwatch.core :as sut])
  #? (:cljs (:require-macros [stopwatch.core :as sut])))

(defn- do-work [times]
  (let [!result (atom 0)]
    (dotimes [i times]
      (reset! !result #? (:cljs (js/Date.) :clj (java.util.Date.))))
    @!result))

(deftest stopwatch-test
  (let [elapsed (sut/start)]
    (do-work 1e6)
    (let [a (elapsed)]
      (is (> a 0))
      (is (>= (elapsed) a)))))
