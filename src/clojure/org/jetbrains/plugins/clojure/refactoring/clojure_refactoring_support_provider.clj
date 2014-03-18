(ns org.jetbrains.plugins.clojure.refactoring.clojure-refactoring-support-provider
  (:use [org.jetbrains.plugins.clojure.refactoring.introduce.introduce-variable])
  (:import [com.intellij.lang LanguageRefactoringSupport LanguageExtension]
   [com.intellij.lang.refactoring RefactoringSupportProvider]
   [org.jetbrains.plugins.clojure ClojureLanguage]))

(defn init
  []
  (if (instance? LanguageExtension LanguageRefactoringSupport/INSTANCE)
    (.addExplicitExtension ^LanguageExtension
      LanguageRefactoringSupport/INSTANCE
      (ClojureLanguage/getInstance)
      (proxy
        [RefactoringSupportProvider] []
                                     (getIntroduceVariableHandler
                                       ([] (introduce-variable-action-handler))
                                       ([elem] (introduce-variable-action-handler)))))))


