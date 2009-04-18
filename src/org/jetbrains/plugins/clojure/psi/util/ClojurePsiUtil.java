package org.jetbrains.plugins.clojure.psi.util;

import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.impl.ClKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;

/**
 * @author ilyas
 */
public class ClojurePsiUtil {
  public static final String GEN_CLASS = ":gen-class";
  public static final String EXTENDS = ":extends";
  public static final String IMPLEMENTS = ":implements";

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

  public static ClKey findNamespaceKeyByName(ClList ns, String keyName) {
    final ClList list = ns.findFirstChildByClass(ClList.class);
    if (list == null) return null;
    for (PsiElement element : list.getChildren()) {
      if (element instanceof ClKey) {
        ClKey key = (ClKey) element;
        if (keyName.equals(key.getText())) {
          return key;
        }
      }
    }
    return null;
  }

  @Nullable
  public static PsiElement getNextNonWhiteSpace(PsiElement element) {
    PsiElement next = element.getNextSibling();
    while (next != null && (next instanceof PsiWhiteSpace)) {
      next = next.getNextSibling();
    }
    return next;
  }
}
