(ns org.jetbrains.plugins.clojure.init-clojure
  (:require org.jetbrains.plugins.clojure.intention.convert-import-intention)
  (:require org.jetbrains.plugins.clojure.refactoring.clojure-refactoring-support-provider))

(defn init []
  (do
    (org.jetbrains.plugins.clojure.intention.convert-import-intention/init)
    (org.jetbrains.plugins.clojure.refactoring.clojure-refactoring-support-provider/init)))
