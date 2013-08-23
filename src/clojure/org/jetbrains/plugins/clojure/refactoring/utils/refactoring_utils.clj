(ns org.jetbrains.plugins.clojure.refactoring.utils.refactoring-utils
  (:use [org.jetbrains.plugins.clojure.utils.clojure-utils :as clojure-utils])
  (:import [com.intellij.openapi.editor Editor SelectionModel EditorSettings Document]
   [com.intellij.openapi.project Project]
   [com.intellij.psi PsiFile PsiDocumentManager PsiElement]
   [com.intellij.psi.util PsiTreeUtil]
   [org.jetbrains.plugins.clojure.psi.api ClList ClVector]
   [com.intellij.refactoring.util CommonRefactoringUtil]
   [com.intellij.openapi.vfs ReadonlyStatusHandler VirtualFile]
   [com.intellij.openapi.util TextRange]
   [org.jetbrains.plugins.clojure.psi.util ClojurePsiFactory ClojurePsiUtil]
   [java.util Comparator]
   [com.intellij.codeInsight PsiEquivalenceUtil]))


(defrecord Declaration [name ^PsiElement expression])

(defn get-occurences
  [^PsiElement container ^PsiElement element]
  (if (PsiEquivalenceUtil/areElementsEquivalent
        container
        element)
    (cons element '())
    (mapcat
      #(lazy-seq (get-occurences %1 element))
      (seq (.getChildren container)))))

(defn- get-text-from-Declaration
  [declaration]
  (str
    (:name declaration)
    " "
    (-> declaration
      :expression get-text)))

(defn join-Declarations
  [declarations]
  (clojure.string/join
    "\n"
    (map
      get-text-from-Declaration
      declarations)))

(defn get-ClVector-form-string-declarations
  [declarations-string ^Project project]
  (-> (ClojurePsiFactory/getInstance project)
    (.createVectorFromText declarations-string)))

(defn create-ClVector-from-Declarations
  [declarations project]
  (-> (join-Declarations declarations)
    (get-ClVector-form-string-declarations project)))

(defn create-let-form
  [project ^ClVector bindings ^PsiElement body]
  (-> (ClojurePsiFactory/getInstance project)
    (.createListFromText
      (str
        "let "
        (.getText
          bindings)
        "\n"
        (.getText
          body)))))

(defn get-Declarations-from-ClVector
  [^ClVector bindings]
  (map
    (fn [[^PsiElement name ^PsiElement expr]]
      (Declaration.
        (.getText
          name)
        expr))
    (partition
      2
      (.getChildren
        bindings))))


(defn inplace-available?
  [^Editor editor]
  (some-> editor
    (.getSettings)
    (.isVariableInplaceRenameEnabled)))

(defn get-selection-start
  [^Editor editor]
  (some-> editor
    .getSelectionModel
    .getSelectionStart))

(defn get-selection-end
  [^Editor editor]
  (some-> editor
    .getSelectionModel
    .getSelectionEnd))

(defn commit-all-documents
  [^Project project]
  (-> project
    PsiDocumentManager/getInstance
    .commitAllDocuments))

(defn is-writable?
  [^PsiFile file ^Project project]
  (some->> file
    .getVirtualFile
    vector
    (into-array VirtualFile)
    (ReadonlyStatusHandler/ensureFilesWritable project)))


(defn get-expression
  [file start end]
  (PsiTreeUtil/findElementOfClassAtRange file start end ClList))



(defn has-selection?
  [^Editor editor]
  (-> editor
    .getSelectionModel
    .hasSelection))


(defn- find-expression-and-invoke!
  [project ^Editor editor file invokes]
  (if-let [expression (ClojurePsiUtil/findSexpAtCaret editor false)]
    (do
      (let [start (-> expression
                    .getTextRange
                    .getStartOffset)
            end (-> expression
                  .getTextRange
                  .getEndOffset)]
        (-> editor
          .getSelectionModel
          (.setSelection start end)))
      (invokes editor))
    (show-error project editor (bundle-message
                                 "cannot.refactor.not.form"))))

(defn invoke-refactoring!
  [project editor file context invokes]
  (if (has-selection? editor)
    (invokes editor)
    (find-expression-and-invoke!
      project
      editor
      file
      invokes)))


(defn get-var-name
  []
  "IDEA") ;todo


(defn replace-occurence!
  [^PsiElement expression new-name ^Editor editor]
  (let [document (.getDocument editor)
        text-range (.getTextRange expression)
        start-offset (.getStartOffset text-range)
        end-offset (.getEndOffset text-range)
        document-manager (-> editor
                           .getProject
                           (PsiDocumentManager/getInstance))]
    (do
      (.replaceString
        document
        start-offset
        end-offset
        new-name)
      (.commitDocument
        document-manager
        document))))

(defn replace-occurences!
  [occurences name editor]
  (do
    (commit-document editor)
    (doseq
      [o (reverse occurences)]
      (replace-occurence! o name editor))))


