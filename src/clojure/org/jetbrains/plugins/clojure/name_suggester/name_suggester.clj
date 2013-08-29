(ns
  ^{:author Stanislav.Osipov}
  org.jetbrains.plugins.clojure.name_suggester.name-suggester
  (:use [org.jetbrains.plugins.clojure.utils.clojure-utils :as clojure-utils])
  (:use [org.jetbrains.plugins.clojure.utils.java-wrappers])
  (:import [org.jetbrains.plugins.clojure.psi.api.symbols ClSymbol]
           [org.jetbrains.plugins.clojure.psi.api ClList]
           [com.intellij.psi PsiElement]))



(defn generate-names
  [string]
  "Regexp splits by CamelCase, - and /"
  (let [names (filter
               (complement nil?)
               (map
                 (partial re-find #"\w+")
                 (map
                   clojure.string/lower-case
                   (some->
                     string
                     (clojure.string/split #"(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|-|/")))))]
    (if (contains? #{"get" "set" "is"} (first names))
      (rest names)
      names)))


(defn harvest-symbols
  [^PsiElement container]
  (map
    get-text
    (filter
      #(instance? ClSymbol %)
      (psi-tree-seq
        container))))

(defn sieve-names
  [names forbidden-names]
  (filter
    #((complement contains?) forbidden-names %)
    names))

(defn dummy-suggestion
  [forbidden-names name]
  (first
    (drop-while
      #(contains? forbidden-names %)
      (cons
        name
        (map
          #(str name %)
          (iterate inc 1))))))

(defn get-var-names
  [^PsiElement element ^PsiElement container]
  (let [forbidden (set (harvest-symbols container))
        dummy (dummy-suggestion forbidden "val")]
    (if (instance? ClList element)
      (conj
        (-> element
          get-list-name
          generate-names
          (sieve-names forbidden)
          vec)
        dummy)
      [dummy])))

