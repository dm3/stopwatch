(def project 'dm3/stopwatch)
(def version "0.1.1-SNAPSHOT")

(set-env! :resource-paths #{"src"}
          :source-paths   #{"test"}
          :exclusions     '[org.clojure/clojurescript]
          :dependencies   '[[org.clojure/clojure "1.8.0"]
                            [org.clojure/clojurescript "1.9.562"]
                            [net.cgrand/macrovich "0.2.0"]

                            ;; Dev
                            [org.clojure/tools.namespace "0.2.11" :scope "test"]
                            [adzerk/boot-cljs "2.0.0" :scope "test"]
                            [adzerk/boot-test "1.1.2" :scope "test"]
                            [crisptrutski/boot-cljs-test "0.3.0" :scope "test"]
                            [adzerk/bootlaces "0.1.13" :scope "test"]])

(task-options!
 pom {:project     project
      :version     version
      :description "Clojure/script stopwatch"
      :url         "https://github.com/dm3/stopwatch"
      :scm         {:url "https://github.com/dm3/stopwatch"}
      :license     {"MIT License" "https://opensource.org/licenses/MIT"}})

(require '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-test :as t]
         '[adzerk.bootlaces :as b]
         '[crisptrutski.boot-cljs-test :as c]
         '[clojure.java.io :as io]
         '[clojure.tools.namespace.repl :as tnr])

(b/bootlaces! version :dont-modify-paths? true)

(defn dev! []
  (alter-var-root #'*warn-on-reflection* (constantly true))
  (merge-env! :source-paths #{"test"})
  (task-options! cljs {:optimizations :none, :source-map true})
  (apply tnr/set-refresh-dirs
    (map #(.getPath (io/file (System/getProperty "user.dir") %)) ["src"])))

(defn reset []
  (tnr/refresh-all))

(deftask test-all []
  (dev!)
  (comp (t/test)
        (c/test-cljs :js-env :phantom)
        (c/test-cljs :js-env :node)))

(deftask repl-dev []
  (dev!)
  (repl))

(deftask build []
  (comp (pom) (jar) (install)))

(deftask release []
  (comp (build) (b/push-release)))
