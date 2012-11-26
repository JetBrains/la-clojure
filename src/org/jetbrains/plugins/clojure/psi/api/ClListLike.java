package org.jetbrains.plugins.clojure.psi.api;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

/**
 * @author ilyas
 */
public interface ClListLike extends ClBraced {
  <T> T findFirstChildByClass(Class<T> aClass);

  @Nullable
  String getHeadText();

  ClSymbol[] getAllSymbols();

  ClSymbol[] getAllQuotedSymbols();
}
