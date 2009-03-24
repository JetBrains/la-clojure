package org.jetbrains.plugins.clojure.psi.api.symbols;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiPolyVariantReference;

/**
 * @author ilyas
 */
public interface ClSymbol extends ClojurePsiElement, PsiPolyVariantReference {
  @NotNull
  String getNameString();
}
