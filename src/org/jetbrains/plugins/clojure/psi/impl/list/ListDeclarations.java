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

/**
 * @author ilyas
 */
public class ListDeclarations {

  public static final String LET = "let";
  public static final String FN = "fn";
  public static final String DEFN = "defn";
  public static final String IMPORT = "import";

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
}
