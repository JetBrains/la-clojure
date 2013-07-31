package org.jetbrains.plugins.clojure.psi.api.symbols;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.plugins.clojure.psi.resolve.processors.ResolveKind;

/**
 * @author ilyas
 */
public interface ClSymbol extends ClojurePsiElement, PsiPolyVariantReference, PsiNamedElement {

  final ClSymbol[] EMPTY_ARRAY = new ClSymbol[0];

  @NotNull
  String getNameString();

  @Nullable
  PsiElement getReferenceNameElement();

  @Nullable
  String getReferenceName();

  @Nullable
  ClSymbol getRawQualifierSymbol();

  /**
   * Raw qualifier or in case of it's empty it can be symbol from import list
   * (import '(java.util Date))
   * @return real qualifier of the symbol
   */
  @Nullable
  ClSymbol getQualifierSymbol();

  boolean isQualified();

  @Nullable
  PsiElement getSeparatorToken();

  public ResolveKind[] getKinds();
}
