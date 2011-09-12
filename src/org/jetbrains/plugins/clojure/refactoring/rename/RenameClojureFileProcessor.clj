(ns org.jetbrains.plugins.clojure.refactoring.rename.RenameClojureFileProcessor
  (:gen-class
    :extends com.intellij.refactoring.rename.RenamePsiElementProcessor
    :exposes-methods {renameElement superRenameElement}
    :state state
    :init init)

  (:import [org.jetbrains.plugins.clojure.psi.api ClojureFile]
           [com.intellij.openapi.util.text StringUtil]))

; :init functions (-init in this case) are unusual, in that they always return a
; vector, the first element of which is a vector of arguments for the superclass
; constructor - since our superclass takes no args, this vector is empty.
; The second element of the vector is the state for the instance.
(defn -init []
  [[] (atom [])])

(defn -canProcessElement [this element]
  (instance? ClojureFile element))

(defn -renameElement [this elem newName usages listener]
  (let [ns (.getNamespace elem)
        prefix (.getNamespacePrefix elem)]
    (do
      (.superRenameElement this elem newName usages listener)
      (if (not (nil? ns))
        (.setNamespace elem (str prefix "." (StringUtil/trimEnd newName ".clj")))))))

