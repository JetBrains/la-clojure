(ns examples.test
  (:require [clojure.contrib.jar :as jar]
            [clojure.contrib.classpath :as cp]
            [clojure.contrib.trace :as tr])
  (:import [javax.swing JFrame JPanel]
           [java.util Arrays]
           [clojure.lang PersistentArrayMap]))

(new JFrame (new JPanel))

(jar/filenames-in-jar)
(cp/classpath-directories)
(tr/<ref>trace-fn-call)


