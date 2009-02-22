package org.jetbrains.plugins.clojure.psi;

import com.intellij.psi.PsiElement;

/**
 * @author ilyas
 */
public class ClojurePsiUtil {

  public static <T> T findNextSiblingByClass(PsiElement element, Class<T> aClass) {
    PsiElement next = element.getNextSibling();
    while (next != null && !aClass.isInstance(next)) {
      next = next.getNextSibling();
    }
    return (T)next;
  }

}
