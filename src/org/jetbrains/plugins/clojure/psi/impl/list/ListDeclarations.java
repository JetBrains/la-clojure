package org.jetbrains.plugins.clojure.psi.impl.list;

import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;

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
