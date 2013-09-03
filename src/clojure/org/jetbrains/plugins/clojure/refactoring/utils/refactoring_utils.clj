(ns org.jetbrains.plugins.clojure.refactoring.utils.refactoring-utils
  (:use [clojure.set])
  (:use [org.jetbrains.plugins.clojure.utils.clojure-utils :as clojure-utils])
  (:use [org.jetbrains.plugins.clojure.utils.java-wrappers])
  (:import [com.intellij.openapi.editor Editor SelectionModel EditorSettings Document]
   [com.intellij.openapi.project Project]
   [com.intellij.psi PsiFile PsiDocumentManager PsiElement PsiRecursiveElementVisitor]
   [com.intellij.psi.util PsiTreeUtil]
   [org.jetbrains.plugins.clojure.psi.api ClList ClVector]
   [com.intellij.refactoring.util CommonRefactoringUtil]
   [com.intellij.openapi.vfs ReadonlyStatusHandler VirtualFile]
   [com.intellij.openapi.util TextRange]
   [org.jetbrains.plugins.clojure.psi.util ClojurePsiFactory ClojurePsiUtil]
   [java.util Comparator]
   [com.intellij.codeInsight PsiEquivalenceUtil]
   [org.jetbrains.plugins.clojure.psi.api.symbols ClSymbol]
   [com.intellij.openapi.application ApplicationManager Application]))


(defrecord Declaration [name ^PsiElement expression])

(def sentinels
  #{"with-open"
    "when-let"
    "when-first"
    "letfn"
    "letfn-"
    "for"
    "if-let"
    "loop"
    "doseq"
    "declare"
    "fn"
    "defn"
    "defn-"
    "def"
    "ns"
    "import"
    "use"
    "with-local-vars"
    "with-bindings"
    "with-bindings*"
    "with-redefs"
    "bindings"
    "lazy-seq"
    "if"})

(def containers
  #{"let"})

(def guards
  (union sentinels containers))

(defn get-occurences
  [^PsiElement container ^PsiElement element]
  (letfn [(equal?
            [^PsiElement e1 ^PsiElement e2]
             (PsiEquivalenceUtil/areElementsEquivalent
              e1
              e2))
          (branch?
            [^PsiElement e]
            (and
              (not
                (equal?
                  e
                  element))
              (some-> e
                .getChildren
                empty?
                false?)))]
    (filter
      #(equal? element %)
      (psi-tree-seq container branch?))))

(defn- get-text-from-Declaration
  [declaration]
  (str
    (:name declaration)
    " "
    (-> declaration
      :expression
      get-text)))

(defn- join-Declarations
  [declarations]
  (clojure.string/join
    "\n"
    (map
      get-text-from-Declaration
      declarations)))

(defn- get-ClVector-from-string-declarations
  [declarations-string ^Project project]
  (-> (ClojurePsiFactory/getInstance project)
    (.createVectorFromText declarations-string)))

(defn create-ClVector-from-Declarations
  [declarations project]
  (-> (join-Declarations declarations)
    (get-ClVector-from-string-declarations project)))

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
      (get-children
        bindings))))


(defn inplace-available?
  [^Editor editor]
  (true?
    (and
      (some-> editor
        (.getSettings)
        (.isVariableInplaceRenameEnabled))
      (not
        (-> (ApplicationManager/getApplication)
          .isUnitTestMode)))))


(defn is-writable?
  [^PsiFile file ^Project project]
  (some->> file
    .getVirtualFile
    vector
    (into-array VirtualFile)
    (ReadonlyStatusHandler/ensureFilesWritable project)))


(defn get-first-expression-inside-range
  [^PsiElement element start end]
  (if-let [expression  (some-> element
               (.findElementAt start)
               (ClojurePsiUtil/getNextNonWhiteSpace))]
    (if (-> expression
          get-end-offset
          (#(<= % end)))
      expression)))

(defn get-expression
  [^PsiFile file start end]
  (if-let [expression (PsiTreeUtil/findElementOfClassAtRange file start end ClList)]
    expression
    (if-let [expression (get-first-expression-inside-range file start end)]
      (if (and
            (instance? ClList expression)
            (if-let [next-expression (get-first-expression-inside-range
                                       file
                                       (get-end-offset expression)
                                       end)]
              false
              true))
        expression))))



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

(defn replace-occurence!
  [^TextRange text-range new-name ^Editor editor]
  (let [document (.getDocument editor)
        start (.getStartOffset text-range)
        end (.getEndOffset text-range)]
    (.replaceString
      document
      start
      end
      new-name)))

(defn replace-occurences!
  [ranges name editor]
  (do
    (commit-document editor)
    (doseq
      [text-range (reverse ranges)]
      (replace-occurence! text-range name editor))
    (commit-document editor)))


(defn ^PsiElement find-ancestor-by-name-set
  "Finds ancestor of element with name from names and returns it previous child"
  [^PsiElement element names]
  (letfn [(guard?
            [^PsiElement element]
            (and
              (instance? ClList element)
              (contains?
                names
                (get-list-name element))))
          (vector?
            [element]
            (instance? ClVector element))
          (should-stop?
            [element]
            (or
              (nil? element)
              (guard? element)
              (vector? element)))]
    (first
      (drop-while
        (comp not should-stop? get-parent)
        (iterate get-parent element)))))



(defn get-binding-symbol-by-name
  [^ClList let-form name]
  (let [symbols (some-> let-form
                    get-let-bindings
                    (PsiTreeUtil/findChildrenOfType ClSymbol))]
    (-> (filter #(=
                   name
                   (get-name-string %)) symbols)
      first)))
