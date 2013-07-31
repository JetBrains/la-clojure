package org.jetbrains.plugins.clojure.psi.api;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;

/**
 * @author ilyas
 */
public interface ClBraced extends ClojurePsiElement {
  @NotNull
  PsiElement getFirstBrace();

  @Nullable
  PsiElement getLastBrace();
}
