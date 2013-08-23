(ns org.jetbrains.plugins.clojure.refactoring.introduce.introduce-variable
  (:use [org.jetbrains.plugins.clojure.refactoring.utils.refactoring-utils])
  (:use [org.jetbrains.plugins.clojure.utils.clojure-utils])
  (:import [com.intellij.refactoring RefactoringActionHandler]
   [com.intellij.refactoring.introduce.inplace OccurrencesChooser]
   [com.intellij.openapi.util Pass Computable TextRange]
   [com.intellij.openapi.command CommandProcessor]
   [com.intellij.openapi.project Project]
   [com.intellij.openapi.editor Editor Document]
   [com.intellij.psi PsiFile PsiElement]
   [com.intellij.openapi.application ApplicationManager Application]
   [org.jetbrains.plugins.clojure.psi.api ClVector]
   [org.jetbrains.plugins.clojure.refactoring.utils.refactoring_utils Declaration]))

(def refactoring-name (bundle-message
                        "introduce.variable.title"))


(defn- file-ok?
  [^Project project ^Editor editor ^PsiFile file]
  (let [clojure? (with-error
                   is-clojure?
                   project
                   editor
                   (bundle-message
                     "must.be.clojure"))
        writable? (with-error
                    is-writable?
                    project
                    editor
                    (bundle-message
                      "file.is.not.writable"))]
    (if-let [answer (and
                      (clojure? file)
                      (writable? file project))]
      answer)))

(defn- can-introduce?
  [expression ^Project project ^Editor editor]
  true) ;todo

(defn- get-container-body
  [container]
  (if (is-let-form? container)
    (get-let-body
      container)
    container))

(defn- modify-psi-tree
  [^PsiElement container bindings project]
  (let [body (get-container-body container)
        created-container (create-let-form
                            project
                            bindings
                            body)]
    (.replace container created-container)))

(defn- introduce-runner!
  [expression ^PsiElement container occurences name bindings project file editor]
  (let [container-position (-> container
                             .getTextRange
                             .getStartOffset)]
    (do
      (replace-occurences! occurences name editor)
      (some-> (find-element-by-offset file container-position)
        (modify-psi-tree bindings project)))))


(defn- refactor-cmd!
  [expression container occurences name bindings project file editor]
  (-> (ApplicationManager/getApplication)
    (.runWriteAction
      (fn [] (introduce-runner!
               expression
               container
               occurences
               name
               bindings
               project
               file
               editor)))))


(defn- run-inplace!
  [expression container occurences name bindings project editor file]
  (-> (CommandProcessor/getInstance)
    (.executeCommand
      project
      (fn [] (refactor-cmd!
               expression
               container
               occurences
               name
               bindings
               project
               file
               editor))
      refactoring-name
      nil)))

(defn- get-container
  [expression]
  expression) ;todo



(defn- ^ClVector get-container-bindings
  [container expression name project]
  (let [declaration (Declaration. name expression)]
    (create-ClVector-from-Declarations
      (if (is-let-form?
            container)
        (conj
          (get-Declarations-from-ClVector
            (get-let-bindings
              container))
          declaration)
        (vector declaration))
      project)))

(defn- do-refactoring!
  [expression project editor file]
  (let [name (get-var-name)
        container (get-container
                    expression)
        occurences (get-occurences container expression)
        bindings (get-container-bindings container expression name project)]
    (if (inplace-available? editor)
      (run-inplace! expression container occurences name bindings project editor file))))


(defn- invoke-on-expression!
  [project editor file start end]
  (if-let [expression (get-expression file start end)]
    (if (can-introduce? expression project editor)
      (do-refactoring! expression project editor file))
    (show-error project editor "Selected block should be a list form")))

(defn- invoke-selection!
  [project editor file context start end]
  (do
    (commit-all-documents project)
    (if (file-ok? project editor file)
      (invoke-on-expression! project editor file start end))))

(defn invoke!
  [project editor file context]
  (let [invokes (fn [e]
                  (let [start (get-selection-start editor)
                        end (get-selection-end editor)]
                    (invoke-selection! project e file context start end)))]
    (invoke-refactoring project editor file context invokes)))


(defn introduce-variable-action-handler
  []
  (reify
    RefactoringActionHandler
    (invoke [this project editor file context]
      (invoke! project editor file context))))



