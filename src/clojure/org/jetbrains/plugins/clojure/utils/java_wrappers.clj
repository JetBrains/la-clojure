(ns org.jetbrains.plugins.clojure.utils.java-wrappers
  (:import [com.intellij.psi PsiFile PsiElement PsiReference PsiDocumentManager]
           [org.jetbrains.plugins.clojure.psi.util ClojurePsiFactory ClojurePsiUtil]
           [org.jetbrains.plugins.clojure.parser ClojureElementTypes ClojurePsiCreator]
           [com.intellij.openapi.editor Editor]
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
           [com.intellij.openapi.util TextRange]
           [org.jetbrains.plugins.clojure.psi.api.symbols ClSymbol]))



(defn get-current-position
  [^Editor editor]
  (some-> editor
    .getCaretModel
    .getOffset))


(defn get-children
  [^PsiElement element]
  (vec
    (.getChildren element)))

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
  (.getTextOffset element))
