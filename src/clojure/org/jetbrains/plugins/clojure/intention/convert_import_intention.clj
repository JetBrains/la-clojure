(ns org.jetbrains.plugins.clojure.intention.convert_import_intention
  (:use [org.jetbrains.plugins.clojure.utils.psi-utils])
  (:import [com.intellij.lang LanguageAnnotators ASTNode]
           [com.intellij.codeInsight.intention IntentionAction IntentionManager]
           [com.intellij.psi PsiFile PsiElement PsiReference]
           [org.jetbrains.plugins.clojure.psi.util ClojurePsiFactory]
           [org.jetbrains.plugins.clojure.lexer ClojureTokenTypes]
           [org.jetbrains.plugins.clojure.parser ClojurePsiCreator ClojureElementTypes]
           [com.intellij.openapi.editor Editor]
           [org.jetbrains.plugins.clojure.file ClojureFileType]
           [org.jetbrains.plugins.clojure.psi.api.symbols ClSymbol]))


(defn- is-import?
  [^PsiReference psi-ref]
  (if-let [resolved-ref (.resolve psi-ref)]
    (true?
      (and
        (is-def?
          resolved-ref)
        (check-def
          resolved-ref
          "import"
          "clojure.core")))))


(defn- get-unquoted-symbol
  [^PsiElement qform]
  (left-down qform 2))

(defn- is-quoted-symbol?
  [^PsiElement form]
  (when-let [lst (left-down form 1)]
    (and
      (is-quoted? form)
      (=
        1
        (some-> lst
          .getChildren
          count))
      (is-symbol?
        (left-down lst 1)))))

(defn- is-symbol-or-quoted-symbol?
  [^PsiElement element]
  (or
    (is-symbol? element)
    (is-quoted-symbol? element)))


(defn- ^PsiElement get-import-statement
  [import-symbol]
  (when-let [lst-symbol (lift
                          import-symbol
                          2)]
    (right-down
      lst-symbol
      1)))


(defn- get-quoted-text
  [^PsiElement psi]
  (str
    "'"
    "("
    (.getText psi)
    ")"))

(defn- get-quoted-symbol
  [symbol project]
  (create-psi
    (create-symbol-node-from-text
      project
      (get-quoted-text
        symbol))))

(defn- change-form
  [^PsiElement before project]
  (if (is-quoted-symbol? before)
    (get-unquoted-symbol before)
    (get-quoted-symbol before project)))

(defn getText []
  "(Un)Quote statement")

(defn getFamilyName []
  "ConvertImportIntention")

(defn isAvailable [project editor file]
  (let [import (get-caret-psi-ref editor file)
        statement (get-import-statement
                    (get-caret-psi-element editor file))]
    (true?
      (and
        (is-clojure? file)
        ((maybe is-import?) import)
        (is-symbol-or-quoted-symbol? statement)))))

(defn invoke [project editor file]
  (when (isAvailable project editor file)
    (let [before (get-import-statement
                   (get-caret-psi-element editor file))
          after (change-form before project)]
      (.replace before after))))

(defn startInWriteAction []
  true)

(defn init []
  (.registerIntentionAndMetaData
    (IntentionManager/getInstance)
    (reify IntentionAction
      (getText [this]
        (getText))
      (getFamilyName [this]
        (getFamilyName))
      (isAvailable [this project editor file]
        (isAvailable project editor file))
      (invoke [this project editor file]
        (invoke project editor file))
      (startInWriteAction [this]
        (startInWriteAction)))
    (into-array java.lang.String ["Clojure"])))