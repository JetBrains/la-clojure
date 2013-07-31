package org.jetbrains.plugins.clojure.psi.resolve;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Trinity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.scope.NameHint;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.ClListLike;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.impl.list.ListDeclarations;

/**
 * @author ilyas
 */
public abstract class ResolveUtil {

  public static boolean treeWalkUp(PsiElement place, PsiScopeProcessor processor) {
    PsiElement lastParent = null;
    PsiElement run = place;
    while (run != null) {
      if (!run.processDeclarations(processor, ResolveState.initial(), lastParent, place)) return false;
      lastParent = run;
      run = run.getContext(); //same as getParent
    }

    return true;
  }

  public static boolean processChildren(PsiElement element, PsiScopeProcessor processor,
                                        ResolveState substitutor, PsiElement lastParent, PsiElement place) {
    PsiElement run = lastParent == null ? element.getLastChild() : lastParent.getPrevSibling();
    while (run != null) {
      if (PsiTreeUtil.findCommonParent(place, run) != run && !run.processDeclarations(processor, substitutor, null, place))
        return false;
      run = run.getPrevSibling();
    }

    return true;
  }

  public static boolean processElement(PsiScopeProcessor processor, PsiNamedElement namedElement) {
    return processElement(processor, namedElement, ResolveState.initial());
  }

  public static boolean processElement(PsiScopeProcessor processor, PsiNamedElement namedElement, ResolveState state) {
    if (namedElement == null) return true;
    NameHint nameHint = processor.getHint(NameHint.KEY);
    String name = nameHint == null ? null : nameHint.getName(ResolveState.initial());
    String actualName = namedElement.getName();
    final String renamed = state.get(RENAMED_KEY);
    if (renamed != null) actualName = renamed;
    if (name == null || name.equals(actualName)) {
      return processor.execute(namedElement, state);
    }
    return true;
  }

  public static PsiElement[] mapToElements(ClojureResolveResult[] candidates) {
    PsiElement[] elements = new PsiElement[candidates.length];
    for (int i = 0; i < elements.length; i++) {
      elements[i] = candidates[i].getElement();
    }

    return elements;
  }

  public static Key<String> RENAMED_KEY = Key.create("clojure.renamed.key");

}
