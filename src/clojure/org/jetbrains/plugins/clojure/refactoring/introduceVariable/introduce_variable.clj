(ns org.jetbrains.plugins.clojure.refactoring.introduce-variable
  (:use [org.jetbrains.plugins.clojure.refactoring.utils.refactoring-utils])
  (:import [com.intellij.refactoring RefactoringActionHandler]
   [com.intellij.refactoring.introduce.inplace OccurrencesChooser]
   [com.intellij.openapi.util Pass Computable]
   [com.intellij.openapi.command CommandProcessor]))

(defn- can-introduce?
  [expr]
  true) ;todo

(defn- get-expression-to-introduce
  [file start end]
  (if-let [expr (get-expression file start end)]
    (if (can-introduce? expr)
      expr)))

(defn- introduce-variable
  [expr editor]
  (if (is-inplace-available? editor)
    (run-inplace expr editor)))

(defn- show-chooser
  [editor selected all callback]
  (some-> editor
    (OccurrencesChooser/simpleChooser)
    .showChooser selected all callback))

(defn- run-refactoring-inside
  [start
   end
   file
   editor
   expr]
  nil) ;todo


(defn- introduce-computable
  [start
   end
   file
   editor
   expr]
  (reify
    Computable
    (compute [this]
      (run-refactoring-inside
        start
        end
        file
        editor
        expr))))


(defn- run-inplace
  [expr editor]
  (if (is-inplace-available? editor)
    (let [cmd (fn []
                ())
          callback (proxy
                     [Pass] []
                            (pass [this replace-choice]
                              (-> (CommandProcessor/getInstance)
                                (.executeCommand
                                  project
                                  cmd
                                  refactoring-name
                                  nil))))]
      (show-chooser
        editor
        expr
        (list expr)
        callback))))

(defn invoke
  ([project editor file context]
   (let [invokes (fn [editor]
                   (let [start (get-selection-start editor)
                         end (get-selection-end editor)]
                     invoke project editor file start end))]
     (invoke-refactoring project editor file context invokes)))
  ([project editor file context start end]
   (when (is-file-ok? file)
     (commit-all-documents project)
     (if-let [expr (get-expression-to-introduce)]
       (introduce-variable expr))))) ;todo




(defn introduce-variable-action-handler
  []
  (reify
    RefactoringActionHandler
    (invoke [this project editor file context]
      (invoke project editor file context))))
