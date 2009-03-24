package org.jetbrains.plugins.clojure.psi.util;

import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;

/**
 * @author ilyas
 */
public class ClojurePsiUtil {

  @Nullable
  public static ClList findFormByName(ClojurePsiElement container, @NotNull String name) {
    for (PsiElement element : container.getChildren()) {
      if (element instanceof ClList) {
        ClList list = (ClList) element;
        final ClSymbol first = list.getFirstSymbol();
        if (first != null && name.equals(first.getNameString())) {
          return list;
        }
      }
    }
    return null;
  }

  public static <T> T findNextSiblingByClass(PsiElement element, Class<T> aClass) {
    PsiElement next = element.getNextSibling();
    while (next != null && !aClass.isInstance(next)) {
      next = next.getNextSibling();
    }
    return (T)next;
  }
}
