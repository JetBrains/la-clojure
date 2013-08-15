(ns org.jetbrains.plugins.clojure.refactoring.utils.refactoring-utils
  (:use [org.jetbrains.plugins.clojure.utils.psi-utils])
  (:import [com.intellij.openapi.editor Editor SelectionModel]
   [com.intellij.openapi.project Project]
   [com.intellij.psi PsiFile PsiDocumentManager]
   [com.intellij.openapi.vfs ReadonlyStatusHandler]
   [com.intellij.openapi.vfs.ReadonlyStatusHandler OperationStatus]
   [com.intellij.psi.util PsiTreeUtil]
   [org.jetbrains.plugins.clojure.psi.api ClList]))


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
  (let [read-only-handler (ReadonlyStatusHandler/getInstance project)]
    (some->> file
      .getVirtualFile
      (.ensureFilesWritable read-only-handler)
      .hasReadonlyFiles
      false?)))

(defn is-file-ok?
  [^PsiFile file ^Project project]
  (true?
    (and
      (is-clojure? file)
      (is-writeable? file project))))


(defn get-expression
  [file start end]
  (PsiTreeUtil/findElementOfClassAtRange file start end ClList))

(defn invoke-refactoring
  [project editor file context invokes]
  (if
    (-> editor
      .getSelectionModel
      .hasSelection)
    (invokes editor))) ;todo
