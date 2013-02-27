package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.scope.NameHint;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.*;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.impl.list.ListDeclarations;
import org.jetbrains.plugins.clojure.psi.impl.ns.NamespaceUtil;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.clojure.psi.util.ClojureKeywords;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;

import java.util.*;

/**
 * @author ilya
 */
public abstract class ImportOwner {
  public static boolean processDeclarations(PsiElement self, PsiScopeProcessor processor, PsiElement place) {
    for (PsiElement element : self.getChildren()) {
      if (element instanceof ClList) {
        ClList directive = (ClList) element;
        final PsiElement first = directive.getFirstNonLeafElement();
        if (first == null) return true;
        final String headText = first.getText();
        if (!processImports(processor, place, directive, headText)) return false;
        if (!processUses(processor, place, directive, headText)) return false;
        if (!processRequires(processor, place, directive, headText)) return false;
      }
    }
    return true;
  }

  /**
   * Require directive loads clojure namespaces
   * Syntax in namespace:
   * {Require in namespace} ::= '(' ':require' {Require Directive}* ')'
   * {Require Directive}    ::= '(' {Prefix} {Name Directive}* ')' |
   *                            '[' {Prefix} {Name Directive}* ']' |
   *                            '[' {Namespace Qualified Name} ':as' {Identifier} ']'
   *                            {Namespace Qualified Name}
   * {Name Directive}       ::= '[' {Namespace Name} ':as' {Identifier} ']' |
   *                            {Namespace Name}
   *
   * Examples:
   * (:require clojure.string)
   * (:require [clojure.string :as str])
   * (:require (clojure.string))
   * (:require (clojure string reflect))
   * (:require (clojure [string :as str] reflect))
   *
   * Syntax for function call is the same, you just can quote it (unquoted will not work in this case).
   *
   * Examples:
   * (require 'clojure.string)
   * (require '[clojure.string :as str])
   * (require '(clojure.string))
   * (require '(clojure string reflect))
   * (require '(clojure [string :as str] reflect))
   */
  public static boolean processRequires(PsiScopeProcessor processor, PsiElement place, ClList child, String headText) {
    final boolean isRequireKeyword = ClojureKeywords.REQUIRE.equals(headText);
    final boolean isRequireFunction = ListDeclarations.REQUIRE.equals(headText);
    if (isRequireKeyword || isRequireFunction) {
      for (PsiElement stmt : child.getChildren()) {
        if (isRequireKeyword && !checkRequireStatement(processor, place, child, stmt)) return false;
        if (isRequireFunction && stmt instanceof ClQuotedForm) {
          final ClojurePsiElement quotedElement = ((ClQuotedForm) stmt).getQuotedElement();
          if (!checkRequireStatement(processor, place, child, quotedElement)) return false;
        }
      }
    }
    return true;
  }


  public static boolean processUses(PsiScopeProcessor processor, PsiElement place, ClList directive, String headText) {
    if (!(ClojureKeywords.USE.equals(headText) || ListDeclarations.USE.equals(headText))) {
      return true;
    }

    final Set<ClSymbol> accum = new HashSet<ClSymbol>();

    accum.addAll(Arrays.asList(directive.getAllQuotedSymbols()));
    accum.addAll(Arrays.asList(directive.getAllSymbols()));


    final Project project = directive.getProject();

    for (ClSymbol symbol : accum) {
      for (PsiNamedElement element : NamespaceUtil.getDeclaredElements(symbol.getNameString(), project)) {
        if (element != null && !ResolveUtil.processElement(processor, element)) {
          return false;
        }
      }
    }


    /*
     Process grouped uses, e.g.,

     (ns some.namespace
           (:use (base.name module-1 module-2)))

      */
    final PsiElement[] children = directive.getChildren();
    for (PsiElement child : children) {
      if (child instanceof ClVector || child instanceof ClList) {
        ClListLike list = (ClListLike) child;
        final PsiElement element = list.getFirstNonLeafElement();
        if (element instanceof ClSymbol) {
          ClSymbol firstSymbol = (ClSymbol) element;
          final String prefix = firstSymbol.getNameString();
          final ClSymbol[] allSymbols = list.getAllSymbols();
          for (int i = 1; i < allSymbols.length; i++) {
            final ClSymbol suffix = allSymbols[i];
            final String fqn = prefix + "." + suffix.getNameString();
            for (PsiNamedElement ns : NamespaceUtil.getDeclaredElements(fqn, project)) {
              if (!ResolveUtil.processElement(processor, ns)) {
                return false;
              }
            }
          }
          if (allSymbols.length == 1 && list instanceof ClVector) {
            for (PsiNamedElement ns : NamespaceUtil.getDeclaredElements(allSymbols[0].getName(), project)) {
              if (!ResolveUtil.processElement(processor, ns)) {
                return false;
              }
            }
          }
        }
      }
    }
    return true;
  }

  /**
   * Import directive imports Java classes.
   * Syntax in namespace:
   * {Import in namespace} ::= '(' ':import' {Import Directive}* ')'
   * {Import Directive}    ::= '(' {Package} {Class Name}* ')' |
   *                           '[' {Package} {Class Name}* ']' |
   *                           {Class Qualified Name}
   *
   * Examples:
   * (:import java.util.Date)
   * (:import (java.util Date ArrayList))
   * (:import [java.util Date ArrayList])
   *
   * Syntax for function call is the same, you just can quote it.
   *
   * Examples:
   * (import 'java.util.Date)
   * (import java.util.Date)
   * (import '(java.util Date))
   * (import '[java.util Date])
   * (import (java.util Date ArrayList))
   */
  public static boolean processImports(PsiScopeProcessor processor, PsiElement place, ClList child, String headText) {
    final boolean isImportKeyword = ClojureKeywords.IMPORT.equals(headText);
    final boolean isImportFunction = ListDeclarations.IMPORT.equals(headText);
    if (isImportKeyword || isImportFunction) {
      for (PsiElement stmt : child.getChildren()) {
        if (!checkImportStatement(processor, place, child, stmt)) return false;
        if (isImportFunction && stmt instanceof ClQuotedForm) {
          final ClojurePsiElement quotedElement = ((ClQuotedForm) stmt).getQuotedElement();
          if (!checkImportStatement(processor, place, child, quotedElement)) return false;
        }
      }
    }

    return true;
  }

  private static boolean checkImportStatement(PsiScopeProcessor processor, PsiElement place, ClList child, PsiElement stmt) {
    if (stmt instanceof ClSymbol) {
      if (!checkImportQualifier(processor, place, child, ((ClSymbol) stmt).getNameString())) return false;
    } else if (stmt instanceof ClVector || stmt instanceof ClList) {
      final ClListLike listLike = (ClListLike) stmt;
      final List<String> qualifiedNames = extractImportQualifiedNames(listLike);
      for (String qualifiedName : qualifiedNames) {
        if (!checkImportQualifier(processor, place, child, qualifiedName)) return false;
      }
    }
    return true;
  }

  private static boolean checkRequireStatement(PsiScopeProcessor processor, PsiElement place, ClList child, PsiElement stmt) {
    if (stmt instanceof ClSymbol) {
      if (!checkRequireQualifier(processor, place, child, ((ClSymbol) stmt).getNameString())) return false;
    } else if (stmt instanceof ClVector && isAliasVector((ClVector) stmt)) {
      ClVector vector = (ClVector) stmt;
      final ClSymbol[] symbols = vector.getAllSymbols();
      if (!processVectorAliasSymbols(processor, symbols)) return false;
      if (symbols.length > 0 && !checkRequireQualifier(processor, place, child, symbols[0].getNameString())) {
        return false;
      }
    } else if (stmt instanceof ClVector || stmt instanceof ClList) {
      final ClListLike listLike = (ClListLike) stmt;
      if (!processRequireQualifiedNames(processor, place, child, listLike)) return false;
    }
    return true;
  }

  private static boolean processVectorAliasSymbols(PsiScopeProcessor processor, ClSymbol[] symbols) {
    if (symbols.length > 1) {
      final PsiElement nextNonWhiteSpace = ClojurePsiUtil.getNextNonWhiteSpace(symbols[0]);
      if (nextNonWhiteSpace != null && nextNonWhiteSpace instanceof ClKeyword) {
        ClKeyword keyword = (ClKeyword) nextNonWhiteSpace;
        if (keyword.getName().equals(ClojureKeywords.AS)) {
          NameHint nameHint = processor.getHint(NameHint.KEY);
          String alias = nameHint == null ? null : nameHint.getName(ResolveState.initial());
          final String aliasName = symbols[1].getName();
          if (alias != null && alias.equals(aliasName)) {
            for (ResolveResult result : symbols[0].multiResolve(false)) {
              final PsiElement element = result.getElement();
              if (element instanceof PsiNamedElement) {
                PsiNamedElement namedElement = (PsiNamedElement) element;
                return processor.execute(namedElement, ResolveState.initial());
              }
            }
          } else if (nameHint == null) {
            if (!processor.execute(symbols[1], ResolveState.initial())) return false;
          }
        }
      }
    }
    return true;
  }

  public static boolean isAliasVector(ClVector vector) {
    final ClKeyword keyword = vector.findFirstChildByClass(ClKeyword.class);
    if (keyword == null) return false;
    return keyword.getName().equals(ClojureKeywords.AS);
  }

  private static boolean checkImportQualifier(PsiScopeProcessor processor, PsiElement place, ClList child, String qualifiedName) {
    final PsiClass clazz = JavaPsiFacade.getInstance(child.getProject()).
        findClass(qualifiedName, GlobalSearchScope.allScope(place.getProject()));
    return !(clazz != null && !ResolveUtil.processElement(processor, clazz));
  }

  private static boolean checkRequireQualifier(PsiScopeProcessor processor, PsiElement place, ClList child, String qualifiedName) {
    //todo: See http://youtrack.jetbrains.com/issue/CLJ-169 for more details
    //In current state we don't need to do any work here
    return true;
  }

  private static List<String> extractImportQualifiedNames(ClListLike listLike) {
    final List<String> qualifiedNames = new ArrayList<String>();
    final PsiElement fst = listLike.getFirstNonLeafElement();
    if (fst instanceof ClSymbol) {
      PsiElement next = fst.getNextSibling();
      while (next != null) {
        if (next instanceof ClSymbol) {
          ClSymbol clazzSym = (ClSymbol) next;
          qualifiedNames.add(((ClSymbol) fst).getNameString() + "." + clazzSym.getNameString());
        }
        next = next.getNextSibling();
      }
    }
    return qualifiedNames;
  }

  private static boolean processRequireQualifiedNames(PsiScopeProcessor processor, PsiElement place, ClList child,
                                                      ClListLike listLike) {
    final PsiElement fst = listLike.getFirstNonLeafElement();
    if (fst instanceof ClSymbol) {
      PsiElement next = fst.getNextSibling();
      while (next != null) {
        if (next instanceof ClSymbol) {
          ClSymbol clazzSym = (ClSymbol) next;
          if (!checkRequireQualifier(processor, place, child, ((ClSymbol) fst).getNameString() + "." + clazzSym.getNameString())) {
            return false;
          }
        } else if (next instanceof ClVector && isAliasVector((ClVector) next)) {
          ClVector vector = (ClVector) next;
          final ClSymbol[] symbols = vector.getAllSymbols();
          if (!processVectorAliasSymbols(processor, symbols)) return false;
          if (symbols.length > 0 && !checkRequireQualifier(processor, place, child,
              ((ClSymbol) fst).getNameString() + "." + symbols[0].getNameString())) {
            return false;
          }
        }
        next = next.getNextSibling();
      }
    }
    return true;
  }
}
