(ns babashka.run-tests
  (:require [clojure.test :as t]
            [babashka.fs-node-test]))

(def ^:private old-fail (get-method t/report [:cljs.test/default :fail]))
(defmethod t/report [:cljs.test/default :fail] [m]
  (set! (.-exitCode js/process) 1)
  (old-fail m))

(def ^:private old-error (get-method t/report [:cljs.test/default :error]))
(defmethod t/report [:cljs.test/default :error] [m]
  (set! (.-exitCode js/process) 1)
  (old-error m))

(t/run-tests 'babashka.fs-node-test)
