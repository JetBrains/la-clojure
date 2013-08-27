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
  (loop [containers [container]  occurences []]
    (if (empty? containers)
      occurences
      (if (PsiEquivalenceUtil/areElementsEquivalent
            (first containers)
            element)
        (recur (rest containers) (cons (first containers) occurences))
        (recur
          (into
            (rest containers)
            (get-children (first containers)))
          occurences)))))

(defn- get-text-from-Declaration
  [declaration]
  (str
    (:name declaration)
    " "
    (-> declaration
      :expression get-text)))

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
      (.getChildren
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


(defn ^PsiElement find-ancestor-by-name-set
  "Finds ancestor of element with name from names and returns it previous child"
  [^PsiElement element names]
  (loop [ancestor (.getParent element) prev element]
    (if (instance? ClList ancestor)
      (if (some->> ancestor
            get-list-name
            (contains? names))
        prev
        (recur (.getParent ancestor) ancestor)))))


(defn get-var-names
  [^PsiElement element]
  (let [names (atom ["var"])
        parse-list-name (fn
                          [name]
                          (let [parts (-> name
                                        (clojure.string/split #"-"))
                                suggestions (filter
                                              (complement nil?)
                                              (map
                                                (partial re-find #"\w+")
                                                parts))]
                            (swap! names into suggestions)))
        visit-element (fn
                        [^PsiElement psi-element]
                        (if (instance? ClList psi-element)
                          (parse-list-name
                            (get-list-name psi-element))))
        names-builder (proxy
                        [PsiRecursiveElementVisitor]
                        []
                        (visitElement [^PsiElement psi-element]
                          (do
                            (visit-element psi-element)
                            (.acceptChildren psi-element this))))]
    (do
      (.accept element names-builder)
      @names)))

(defn get-binding-symbol-by-name
  [^ClList let-form name]
  (let [symbols (some-> let-form
                    get-let-bindings
                    (PsiTreeUtil/findChildrenOfType ClSymbol))]
    (-> (filter #(=
                   name
                   (get-name-string %)) symbols)
      first)))
