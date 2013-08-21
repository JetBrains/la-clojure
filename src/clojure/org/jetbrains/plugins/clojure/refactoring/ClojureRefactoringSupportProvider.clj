(ns org.jetbrains.plugins.clojure.refactoring.ClojureRefactoringSupportProvider
  (:use [org.jetbrains.plugins.clojure.refactoring.introduce-variable])
  (:gen-class :extends com.intellij.lang.refactoring.RefactoringSupportProvider
              :prefix "clj-"))

(defn clj-getIntroduceVariableHandler
  [this]
  (introduce-variable-action-handler))




