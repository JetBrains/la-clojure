(ns org.jetbrains.plugins.clojure.intention.convert_import_intention
  (:import [com.intellij.lang LanguageAnnotators]
           [org.jetbrains.plugins.clojure ClojureLanguage]
           [com.intellij.codeInsight.intention IntentionAction IntentionManager]))

(defn getText []
  "My text") ;TODO

(defn getFamilyName []
  "ConvertImportIntention")

(defn isAvailable [project editor file]
  false) ;TODO

(defn invoke [project editor file]
  (let [document (.getDocument editor)]
    (.insertString document 0 "Some Text"))) ;TODO

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