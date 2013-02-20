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

  public static boolean processRequires(PsiScopeProcessor processor, PsiElement place, ClList directive, String headText) {
    if (!ClojureKeywords.REQUIRE.equals(headText) &&
        !ListDeclarations.REQUIRE.equals(headText)) {
      return true;
    }

    final ClListLike[] clauses = PsiTreeUtil.getChildrenOfType(directive, ClListLike.class);
    if (clauses == null) return true;

    // process :as aliases for namespaces
    for (ClListLike clause : clauses) {
      final PsiElement first = clause.getNonLeafElement(1);
      final PsiElement second = clause.getNonLeafElement(2);
      final PsiElement third = clause.getNonLeafElement(3);
      if (first instanceof ClSymbol && third instanceof ClSymbol &&
          second instanceof ClKeyword && ClojureKeywords.AS.equals(second.getText())) {
        final ClSymbol from = (ClSymbol) first;
        final ClSymbol to = (ClSymbol) third;
        NameHint nameHint = processor.getHint(NameHint.KEY);
        String alias = nameHint == null ? null : nameHint.getName(ResolveState.initial());
        if (alias != null && alias.equals(to.getName())) {
          for (ResolveResult result : from.multiResolve(false)) {
            final PsiElement element = result.getElement();
            if (element instanceof PsiNamedElement) {
              PsiNamedElement namedElement = (PsiNamedElement) element;
              return processor.execute(namedElement, ResolveState.initial());
            }
          }
        } else if (nameHint == null) {
          if (!processor.execute(to, ResolveState.initial())) return false;
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
   * {Import Directive} ::= '(' {Package} {Class Name}* ')' |
   *                        '[' {Package} {Class Name}* ']' |
   *                        {Class Qualified Name}
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
        if (!checkStatement(processor, place, child, stmt)) return false;
        if (isImportFunction && stmt instanceof ClQuotedForm) {
          final ClojurePsiElement quotedElement = ((ClQuotedForm) stmt).getQuotedElement();
          if (!checkStatement(processor, place, child, quotedElement)) return false;
        }
      }
    }

    return true;
  }

  private static boolean checkStatement(PsiScopeProcessor processor, PsiElement place, ClList child, PsiElement stmt) {
    if (stmt instanceof ClSymbol) {
      if (!checkQualifier(processor, place, child, ((ClSymbol) stmt).getNameString())) return false;
    } else if (stmt instanceof ClVector || stmt instanceof ClList) {
      final ClListLike listLike = (ClListLike) stmt;
      final List<String> qualifiedNames = extractQualifiedNames(listLike);
      for (String qualifiedName : qualifiedNames) {
        if (!checkQualifier(processor, place, child, qualifiedName)) return false;
      }
    }
    return true;
  }

  private static boolean checkQualifier(PsiScopeProcessor processor, PsiElement place, ClList child, String qualifiedName) {
    final PsiClass clazz = JavaPsiFacade.getInstance(child.getProject()).
        findClass(qualifiedName, GlobalSearchScope.allScope(place.getProject()));
    return !(clazz != null && !ResolveUtil.processElement(processor, clazz));
  }

  private static List<String> extractQualifiedNames(ClListLike listLike) {
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
}
