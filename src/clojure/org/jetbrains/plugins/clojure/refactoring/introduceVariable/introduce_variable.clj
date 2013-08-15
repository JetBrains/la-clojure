(ns org.jetbrains.plugins.clojure.refactoring.introduce-variable
  (:use [org.jetbrains.plugins.clojure.refactoring.utils.refactoring-utils])
  (:import [com.intellij.refactoring RefactoringActionHandler]))

(defn invoke
  ([project editor file context]
   (let [invokes (fn [editor]
                   (let [start (get-selection-start editor)
                         end (get-selection-end editor)]
                     invoke project editor file start end))]
     (invoke-refactoring project editor file context invokes)))
  ([project editor file context start end]
   ())) ;todo


(defn introduce-variable-action-handler
  []
  (reify
    RefactoringActionHandler
    (invoke [this project editor file context]
      (invoke project editor file context))))
