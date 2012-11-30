package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.scope.NameHint;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClListLike;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.impl.list.ListDeclarations;
import org.jetbrains.plugins.clojure.psi.impl.ns.NamespaceUtil;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.clojure.psi.util.ClojureKeywords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ilya
 */
public abstract class ImportOwner {
  public static boolean processImports(PsiElement self, PsiScopeProcessor processor, PsiElement place, JavaPsiFacade facade) {
    for (PsiElement element : self.getChildren()) {
      if (element instanceof ClList) {
        ClList directive = (ClList) element;
        final PsiElement first = directive.getFirstNonLeafElement();
        if (first == null) {
          return true;
        }
        final String headText = first.getText();
        if (processImports(processor, place, facade, directive, headText)) {
          return true;
        }
        if (processUses(processor, place, facade, directive, headText)) {
          return true;
        }
        if (processRequires(processor, place, facade, directive, headText)) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean processRequires(PsiScopeProcessor processor, PsiElement place, JavaPsiFacade facade, ClList directive, String headText) {
    if (!ClojureKeywords.REQUIRE.equals(headText) &&
        !ListDeclarations.REQUIRE.equals(headText)) {
      return false;
    }

    final ClListLike[] clauses = PsiTreeUtil.getChildrenOfType(directive, ClListLike.class);
    if (clauses == null) return false;

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
          processor.execute(to, ResolveState.initial());
        }
      }
    }
    return false;
  }

  private static boolean processUses(PsiScopeProcessor processor, PsiElement place, JavaPsiFacade facade, ClList directive, String headText) {
    if (!(ClojureKeywords.USE.equals(headText) || ListDeclarations.USE.equals(headText))) {
      return false;
    }

    final Set<ClSymbol> accum = new HashSet<ClSymbol>();

    accum.addAll(Arrays.asList(directive.getAllQuotedSymbols()));
    accum.addAll(Arrays.asList(directive.getAllSymbols()));


    final Project project = directive.getProject();

    for (ClSymbol symbol : accum) {
      for (PsiNamedElement element : NamespaceUtil.getDeclaredElements(symbol.getNameString(), project)) {
        if (element != null && !ResolveUtil.processElement(processor, element)) {
          return true;
        }
      }
    }


    /*
     Process grouped uses, e.g.,

     (ns some.namespace
           (:use (base.name module-1 module-2)))

      */
    final PsiElement second = directive.getSecondNonLeafElement();
    if (second != null && second instanceof ClList) {
      final ClList imports = (ClList) second;
      final PsiElement element = imports.getFirstNonLeafElement();
      if (element instanceof ClSymbol) {
        ClSymbol firstSymbol = (ClSymbol) element;
        final String prefix = firstSymbol.getNameString();
        final ClSymbol[] allSymbols = imports.getAllSymbols();
        for (int i = 1; i < allSymbols.length; i++) {
          final ClSymbol suffix = allSymbols[i];
          final String fqn = prefix + "." + suffix.getNameString();
          for (PsiNamedElement ns : NamespaceUtil.getDeclaredElements(fqn, project)) {
            if (!ResolveUtil.processElement(processor, ns)) {
              return true;
            }
          }
        }
      }

    }
    return false;
  }

  private static boolean processImports(PsiScopeProcessor processor, PsiElement place, JavaPsiFacade facade, ClList child, String headText) {
    if (!(ClojureKeywords.IMPORT.equals(headText) || ListDeclarations.IMPORT.equals(headText))) {
      return false;
    }

    for (PsiElement stmt : child.getChildren()) {
      if (stmt instanceof ClListLike) {
        final ClListLike listLike = (ClListLike) stmt;
        final PsiElement fst = listLike.getFirstNonLeafElement();
        if (fst instanceof ClSymbol) {
          final PsiPackage pack = facade.findPackage(((ClSymbol) fst).getNameString());
          if (pack != null) {
            if (place.getParent() == listLike && place != fst) {
              pack.processDeclarations(processor, ResolveState.initial(), null, place);
            } else {
              PsiElement next = fst.getNextSibling();
              while (next != null) {
                if (next instanceof ClSymbol) {
                  ClSymbol clazzSym = (ClSymbol) next;
                  final PsiClass clazz = facade.findClass(pack.getQualifiedName() + "." + clazzSym.getNameString(),
                      GlobalSearchScope.allScope(place.getProject()));
                  if (clazz != null && !ResolveUtil.processElement(processor, clazz)) {
                    return false;
                  }
                  if (clazz != null) {
                    for (PsiMethod method : clazz.getAllMethods()) {
                      if (!ResolveUtil.processElement(processor, method)) return false;
                    }
                    for (PsiField field : clazz.getAllFields()) {
                      if (!ResolveUtil.processElement(processor, field)) return false;
                    }
                  }
                }
                next = next.getNextSibling();
              }
            }
          }
        }
      }
    }
    return false;
  }
}
