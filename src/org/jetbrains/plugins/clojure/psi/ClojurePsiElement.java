package org.jetbrains.plugins.clojure.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * @author ilyas
 */
public interface ClojurePsiElement extends PsiElement {

  @Nullable
  PsiElement getFirstNonLeafElement();

  @Nullable
  PsiElement getLastNonLeafElement();

  <T> T findFirstChildByClass(Class<T> aClass);
}
