(ns org.jetbrains.plugins.clojure.refactoring.ClojureRefactoringSupportProvider
  (:use [org.jetbrains.plugins.clojure.refactoring.introduceVariable.introduce-variable])
  (:use [org.jetbrains.plugins.clojure.utils.clojure-utils])
  (:gen-class :extends com.intellij.lang.refactoring.RefactoringSupportProvider
   :prefix "clj-"))

(defn clj-getIntroduceVariableHandler
  [this]
  (introduce-variable-action-handler))




