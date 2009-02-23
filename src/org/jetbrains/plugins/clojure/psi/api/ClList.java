package org.jetbrains.plugins.clojure.psi.api;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.annotations.Nullable;

/**
 * @author ilyas
 */
public interface ClList extends ClojurePsiElement, ClBraced {
  @Nullable
  String getPresentableText();

  @Nullable
  ClSymbol getFirstSymbol();
}
