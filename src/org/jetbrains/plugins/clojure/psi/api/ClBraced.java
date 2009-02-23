package org.jetbrains.plugins.clojure.psi.api;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author ilyas
 */
public interface ClBraced {
  @NotNull
  PsiElement getFirstBrace();

  @Nullable
  PsiElement getLastBrace();
}
