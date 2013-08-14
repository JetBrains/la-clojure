(ns org.jetbrains.plugins.clojure.refactoring.introduce-variable
  (:import [com.intellij.refactoring RefactoringActionHandler]))

(defn invoke
  [project editor file context]
  (let [document (.getDocument editor)]
    (.insertString document 0 "Some Text"))) ;todo


(defn introduce-variable-action-handler
  []
  (reify
    RefactoringActionHandler
    (invoke [this project editor file context]
      (invoke project editor file context))))
