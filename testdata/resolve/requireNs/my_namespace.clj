(ns examples.test
  (:require [clojure.contrib.jar :as jar]
            [clojure.contrib.classpath :as cp]
            [clojure.contrib.trace :as tr])
  (:import [javax.swing JFrame JPanel]
           [java.util Arrays]
           [clojure.lang PersistentArrayMap])
  (:use [clojure.contrib.string :only (spli)]))

(new JFrame (new JPanel))

(clojure.contrib.string/split-lines )

(<ref>jar/filenames-in-jar)
(cp/classpath-directories)
(tr/trace-fn-call)
