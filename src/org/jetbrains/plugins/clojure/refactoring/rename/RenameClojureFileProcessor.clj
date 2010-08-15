(ns org.jetbrains.plugins.clojure.refactoring.rename.RenameClojureFileProcessor
  (:gen-class
    :extends com.intellij.refactoring.rename.RenamePsiElementProcessor
    :exposes-methods {renameElement super-renameElement}
    :state state
    :init init)

  (:import [org.jetbrains.plugins.clojure.psi.api ClojureFile])
  )



; :init functions (-init in this case) are unusual, in that they always return a
; vector, the first element of which is a vector of arguments for the superclass
; constructor - since our superclass takes no args, this vector is empty.
; The second element of the vector is the state for the instance.
(defn -init []
  [[] (atom [])])

(defn -canProcessElement [element]
  (instance? ClojureFile element))

(defn -renameElement [element, newName, usages, listener]
  (let [ns (.getNamespace element)]
    (if (not (nil? ns))
      (println ns)
      (println "TEST!")))) 

