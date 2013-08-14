(ns org.jetbrains.plugins.clojure.init_clojure
  (:require org.jetbrains.plugins.clojure.intention.convert_import_intention))

(defn init []
  (do
    (org.jetbrains.plugins.clojure.intention.convert_import_intention/init)))
