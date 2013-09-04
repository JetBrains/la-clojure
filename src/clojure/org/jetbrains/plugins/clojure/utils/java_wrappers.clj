(ns org.jetbrains.plugins.clojure.utils.java-wrappers
  (:import [com.intellij.psi PsiFile PsiElement PsiReference PsiDocumentManager]
           [org.jetbrains.plugins.clojure.psi.util ClojurePsiFactory ClojurePsiUtil]
           [org.jetbrains.plugins.clojure.parser ClojureElementTypes ClojurePsiCreator]
           [com.intellij.openapi.editor Editor CaretModel]
           [org.jetbrains.plugins.clojure.file ClojureFileType]
           [com.intellij.lang ASTNode]
           [com.intellij.psi.tree IElementType]
           [org.jetbrains.plugins.clojure.psi.api ClojureFile ClVector ClList]
           [org.jetbrains.plugins.clojure.psi.api.defs ClDef]
           [org.jetbrains.plugins.clojure ClojureBundle]
           [com.intellij.refactoring.util CommonRefactoringUtil]
           [com.intellij.openapi.project Project]
           [org.jetbrains.plugins.clojure.psi ClojurePsiElement]
           [com.intellij.psi.util PsiTreeUtil]
           [com.intellij.openapi.util TextRange Computable]
           [org.jetbrains.plugins.clojure.psi.api.symbols ClSymbol]
           [com.intellij.openapi.application ApplicationManager Application]
           [com.intellij.openapi.command CommandProcessor]
           [com.intellij.util.containers ContainerUtil]))

(defn range-substring
  [^TextRange text-range ^String s]
  (some-> text-range
    (.substring s)))

(defn get-current-position
  [^Editor editor]
  (some-> editor
    .getCaretModel
    .getOffset))

(defn get-text-range
  [^PsiElement e]
  (some-> e
    .getTextRange))



(defn get-children
  [^PsiElement element]
  (some-> element
    .getChildren
    vec))

(defn get-end-offset
  [^PsiElement element]
  (-> element
    .getTextRange
    .getEndOffset))

(defn get-text
  [^PsiElement element]
  (.getText element))

(defn get-start-offset
  [^PsiElement element]
  (-> element
    .getTextRange
    .getStartOffset))

(defn commit-document
  [^Editor editor]
  (let [document (.getDocument editor)]
    (some-> editor
      .getProject
      (PsiDocumentManager/getInstance)
      (.commitDocument document))))

(defn do-postponed-operations-and-release-document
  [^Editor editor]
  (let [document (.getDocument editor)]
    (some-> editor
      .getProject
      (PsiDocumentManager/getInstance)
      (.doPostponedOperationsAndUnblockDocument document))))

(defn get-namespace
  [^ClojureFile file]
  (.getNamespace file))

(defn create-psi
  [^ASTNode node]
  (ClojurePsiCreator/createElement node))

(defn get-caret-psi-element
  [editor ^PsiElement element]
  (.findElementAt
    element
    (get-current-position editor)))


(defn find-ref-by-offset
  [^PsiFile file offset]
  (.findReferenceAt
    file
    offset))


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

(defn has-selection?
  [^Editor editor]
  (-> editor
    .getSelectionModel
    .hasSelection))


(defn get-list-name
  [^ClList list]
  (some-> list
    .getFirstSymbol
    .getNameString))


(defn get-name-string
  [^ClSymbol symbol]
  (.getNameString symbol))

(defn get-text-offset
  [^PsiElement element]
  (some-> element
    .getTextOffset))

(defn execute-command!
  [^Project project cmd refactoring-name group-id]
  (-> (CommandProcessor/getInstance)
    (.executeCommand
      project
      cmd
      refactoring-name
      group-id)))

(defn run-computable-write-action!
  [^Computable write-action]
  (-> (ApplicationManager/getApplication)
    (.runWriteAction write-action)))

(defn remove-selection
  [^Editor editor]
  (-> editor
    .getSelectionModel
    .removeSelection))

(defn move-to-offset
  [^Editor editor offset]
  (-> editor
    .getCaretModel
    (.moveToOffset offset)))

(defn get-parent
  [^PsiElement element]
  (some-> element
    .getParent))

(defn new-linked-hash-set
  [^Iterable coll]
  (ContainerUtil/newLinkedHashSet coll))


(defn shift-right
  [^TextRange text-range offset]
  (if-let [range text-range]
    (.shiftRight text-range offset)))