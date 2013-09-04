(ns org.jetbrains.plugins.clojure.refactoring.introduce.introduce-variable
  (:use [org.jetbrains.plugins.clojure.refactoring.utils.refactoring-utils])
  (:use [org.jetbrains.plugins.clojure.utils.clojure-utils])
  (:use [org.jetbrains.plugins.clojure.utils.java-wrappers])
  (:use [org.jetbrains.plugins.clojure.name_suggester.name-suggester])
  (:import [com.intellij.refactoring RefactoringActionHandler]
   [com.intellij.refactoring.introduce.inplace OccurrencesChooser InplaceVariableIntroducer]
   [com.intellij.openapi.util Pass Computable TextRange]
   [com.intellij.openapi.command CommandProcessor]
   [com.intellij.openapi.project Project]
   [com.intellij.openapi.editor Editor Document SelectionModel CaretModel]
   [com.intellij.psi PsiFile PsiElement PsiDocumentManager PsiNamedElement]
   [com.intellij.openapi.application ApplicationManager Application]
   [org.jetbrains.plugins.clojure.psi.api ClVector ClList]
   [org.jetbrains.plugins.clojure.refactoring.utils.refactoring_utils Declaration]
   [org.jetbrains.plugins.clojure.psi.api.symbols ClSymbol]
   [java.util LinkedHashSet]
   [com.intellij.refactoring.introduce.inplace OccurrencesChooser$ReplaceChoice]
   [com.intellij.util.containers ContainerUtil]))

(def refactoring-name (bundle-message
                        "introduce.variable.title"))


(defn- file-ok?!
  [^Project project ^Editor editor ^PsiFile file]
  (let [clojure? (with-error!
                   is-clojure?
                   project
                   editor
                   (bundle-message
                     "must.be.clojure"))
        writable? (with-error!
                    is-writable?
                    project
                    editor
                    (bundle-message
                      "file.is.not.writable"))]
    (if-let [answer (and
                      (clojure? file)
                      (writable? file project))]
      answer
      false)))

(defn- can-introduce?
  [expression ^Project project ^Editor editor]
  true) ;todo

(defn- get-container-bindings
  [container]
  (if (is-let-form? container)
    (get-let-bindings
      container)))


(defn- create-bindings
  [container name expression position project]
  (let [get-offset (comp get-end-offset :expression)
        before? (fn
                  [declaration]
                  (> position
                    (get-offset declaration)))
        bindings (get-container-bindings container)
        new-declaration (Declaration. name expression)
        declarations (get-Declarations-from-ClVector bindings)
        before (take-while before? declarations)
        after (drop (count before) declarations)
        new-declarations (concat
                           before
                           (cons
                             new-declaration
                             after))]
    (create-ClVector-from-Declarations
      new-declarations
      project)))

(defn- create-bindings-text
  [container name expression position project]
  (get-text
    (create-bindings
      container
      name
      expression
      position
      project)))

(defn- get-container-body-text
  [container]
  (if (is-let-form? container)
    (let [body-expr (get-let-body-expr container)
          offset (get-start-offset container)
          start (- ((comp get-start-offset first) body-expr)
                  offset)
          end (- ((comp get-end-offset last) body-expr)
                offset)
          range (TextRange/create start end)
          container-text (get-text container)]
      (range-substring
        range
        container-text))
    (get-text
      container)))


(defn- modify-psi-tree
  [^PsiElement container expression name position project]
  (let [body-text (get-container-body-text
                    container)
        bindings-text (create-bindings-text
                        container
                        name
                        expression
                        position
                        project)
        created-container (create-let-form
                            project
                            bindings-text
                            body-text)]
    (.replace container created-container)))

(defn- introduce-runner!
  [expression ^PsiElement container occurences name project file editor]
  (let [container-position (get-start-offset container)
        first-position (get-start-offset (first occurences))
        ranges (map
                 get-text-range
                 occurences)]
    (do
      (replace-occurences! ranges name editor)
      (some-> (find-element-by-offset file container-position)
        (modify-psi-tree expression name first-position project)))))

(defn- refactor-write-action!
  [expression container occurences name project file editor]
  (run-computable-write-action!
    (reify
      Computable
      (compute [this]
        (introduce-runner!
          expression
          container
          occurences
          name
          project
          file
          editor)))))


(defn- run-inplace!
  [expression container occurences name suggestions ^Project project ^Editor editor file]
  (letfn [(run-inplace-introducer!
            [^PsiNamedElement named-element]
            (let [introducer (proxy
                               [InplaceVariableIntroducer]
                               [named-element editor project refactoring-name (into-array PsiElement occurences) nil]
                               (checkLocalScope []
                                 (.getContainingFile named-element)))]
              (if (inplace-available? editor)
                (.performInplaceRefactoring
                  introducer
                  (new-linked-hash-set suggestions)))))
          (do-inplace-refactoring!
            [replace]
            (do
              (remove-selection editor)
              (move-to-offset
                editor
                (some-> replace
                  (get-binding-symbol-by-name name)
                  get-text-offset))
              (commit-document editor)
              (do-postponed-operations-and-release-document editor)
              (some-> replace
                (get-binding-symbol-by-name name)
                run-inplace-introducer!)))
           (cmd
             []
             (-> (refactor-write-action! expression container occurences name project file editor)
               do-inplace-refactoring!))]
    (execute-command!
      project
      cmd
      refactoring-name
      nil)))

(defn- container?
  [element]
  (if (instance? ClList element)
    (contains?
      containers
      (get-list-name element))))

(defn- get-container
  [ancestor]
  (let [parent (get-parent ancestor)
        grand-parent (get-parent parent)]
    (if (and
          (instance? ClVector parent)
          (container? grand-parent))
      grand-parent
      (if (container? parent)
        parent
        ancestor))))

(defn- get-search-scope
  [ancestor]
  (let [parent (get-parent ancestor)
        grand-parent (get-parent parent)]
    (if (and
          (instance? ClVector parent)
          (container? grand-parent))
      grand-parent
      ancestor)))

(defn- do-refactoring!
  [expression project editor file]
  (let [ancestor (find-ancestor-by-name-set
                   expression
                   guards)
        container (get-container
                    ancestor)
        search-scope (get-search-scope
                       ancestor)
        names (get-var-names expression container)
        name (first names)
        occurences (doall
                     (get-occurences search-scope expression))
        callback (proxy
                   [Pass]
                   []
                   (pass [replace-choice]
                     (let [replaces (if (=
                                          OccurrencesChooser$ReplaceChoice/NO
                                          replace-choice)
                                      (vector expression)
                                      occurences)]
                       (run-inplace! expression container replaces name names project editor file))))]
    (if (inplace-available? editor)
      (-> (OccurrencesChooser/simpleChooser editor)
        (.showChooser expression occurences callback))
      (->> OccurrencesChooser$ReplaceChoice/ALL
        (.pass callback)))))


(defn- invoke-on-expression!
  [project editor file start end]
  (if-let [expression (get-expression file start end)]
    (if (can-introduce? expression project editor)
      (do-refactoring! expression project editor file))
    (show-error project editor (bundle-message
                                 "cannot.refactor.not.form"))))

(defn- invoke-selection!
  [project editor file context start end]
  (do
    (commit-all-documents project)
    (if (file-ok?! project editor file)
      (invoke-on-expression! project editor file start end))))

(defn invoke!
  [project editor file context]
  (let [invokes (fn [e]
                  (let [start (get-selection-start editor)
                        end (get-selection-end editor)]
                    (invoke-selection! project e file context start end)))]
    (invoke-refactoring! project editor file context invokes)))


(defn introduce-variable-action-handler
  []
  (reify
    RefactoringActionHandler
    (invoke [this project editor file context]
      (invoke! project editor file context))))



