package org.jetbrains.plugins.clojure.psi.impl.list;

import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClQuotedForm;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.impl.symbols.ClSymbolImpl;

/**
 * @author ilyas
 */
public class ListDeclarations {

  public static final String LET = "let";
  public static final String FN = "fn";
  public static final String DEFN = "defn";
  public static final String IMPORT = "import";
  private static final String MEMFN = "memfn";
  private static final String DOT = ".";

  public static boolean get(PsiScopeProcessor processor,
                            ResolveState state,
                            PsiElement lastParent,
                            PsiElement place,
                            ClList list,
                            @Nullable String headText) {
    if (headText == null) return true;
    if (headText.equals(FN)) return processFnDeclaration(processor, list, place);
    if (headText.equals(LET)) return processLetDeclaration(processor, list, place);
    if (headText.equals(IMPORT)) return processImportDeclaration(processor, list, place);
    if (headText.equals(MEMFN)) return processMemFnDeclaration(processor, list, place);
    if (headText.equals(DOT)) return processDotDeclaration(processor, list, place);

    return true;
  }

  private static boolean processDotDeclaration(PsiScopeProcessor processor, ClList list, PsiElement place) {
    final PsiElement parent = place.getParent();
    if (parent == null || list == parent) return true;

    final PsiElement second = list.getSecondNonLeafElement();
    if (second instanceof ClSymbol && place != second) {
      ClSymbol symbol = (ClSymbol) second;
      for (ResolveResult result : symbol.multiResolve(false)) {
        final PsiElement element = result.getElement();
        if (element instanceof PsiNamedElement && !ResolveUtil.processElement(processor, (PsiNamedElement) element)) {
          return false;
        }
      }
    }

    if (parent.getParent() == list) {
      if (place instanceof ClSymbol && ((ClSymbol) place).getQualifierSymbol() == null) {
        ClSymbol symbol = (ClSymbol) place;
        ResolveResult[] results = ClSymbolImpl.MyResolver.resolveJavaMethodReference(symbol, list.getParent(), true);
        for (ResolveResult result : results) {
          final PsiElement element = result.getElement();
          if (element instanceof PsiNamedElement && !ResolveUtil.processElement(processor, (PsiNamedElement) element)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  private static boolean processMemFnDeclaration(PsiScopeProcessor processor, ClList list, PsiElement place) {
    if (place instanceof ClSymbol && place.getParent() == list && ((ClSymbol) place).getQualifierSymbol() == null) {
      ClSymbol symbol = (ClSymbol) place;
      ResolveResult[] results = ClSymbolImpl.MyResolver.resolveJavaMethodReference(symbol, list.getParent(), true);
      for (ResolveResult result : results) {
        final PsiElement element = result.getElement();
        if (element instanceof PsiNamedElement && !ResolveUtil.processElement(processor, (PsiNamedElement) element)) {
          return false;
        }
      }
    }

    return true;
  }

  private static boolean processImportDeclaration(PsiScopeProcessor processor, ClList list, PsiElement place) {
    final PsiElement second = list.getSecondNonLeafElement();
    final Project project = list.getProject();
    final JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
    if (second instanceof ClSymbol) {
      ClSymbol symbol = (ClSymbol) second;
      final String symbolName = symbol.getNameString();
      final PsiClass clazz = facade.findClass(symbolName, GlobalSearchScope.allScope(project));
      if (clazz != null && !ResolveUtil.processElement(processor, clazz)) {
        return false;
      }
    } else if (second instanceof ClQuotedForm) {
      // process import of form (import '(java.util List Set))
      ClQuotedForm quotedForm = (ClQuotedForm) second;
      final ClojurePsiElement element = quotedForm.getQuotedElement();
      if (element instanceof ClList) {
        ClList inner = (ClList) element;
        final PsiElement first = inner.getFirstNonLeafElement();
        if (first instanceof ClSymbol) {
          final ClSymbol packSym = (ClSymbol) first;

          final PsiPackage pack = facade.findPackage(packSym.getNameString());
          if (pack != null) {
            if (place.getParent() == inner && place != packSym) {
              pack.processDeclarations(processor, ResolveState.initial(), null, place);
            } else {
              PsiElement next = packSym.getNextSibling();
              while (next != null) {
                if (next instanceof ClSymbol) {
                  ClSymbol clazzSym = (ClSymbol) next;
                  final PsiClass clazz = facade.findClass(pack.getQualifiedName() + "." + clazzSym.getNameString(), GlobalSearchScope.allScope(project));
                  if (clazz != null && !ResolveUtil.processElement(processor, clazz)) {
                    return false;
                  }
                }
                next = next.getNextSibling();
              }
            }
          }
        }
      }
    }
    return true;
  }

  private static boolean processLetDeclaration(PsiScopeProcessor processor, ClList list, PsiElement place) {
    return true;
  }

  private static boolean processFnDeclaration(PsiScopeProcessor processor, ClList list, PsiElement place) {
    final PsiElement second = list.getSecondNonLeafElement();
    if (!(second instanceof ClSymbol) || place == second) return true;
    return ResolveUtil.processElement(processor, ((ClSymbol) second));
  }

  public static boolean isLocal(PsiElement element) {
    if (element instanceof ClSymbol) {
      ClSymbol symbol = (ClSymbol) element;
      final PsiElement parent = symbol.getParent();

      if (parent instanceof ClList) {
        ClList list = (ClList) parent;
        if (FN.equals(list.getHeadText())) return true;
      }
    }

    return false;
  }
}
