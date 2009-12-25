package org.jetbrains.plugins.clojure.psi.api;

import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

/**
 * @author ilyas
 */
public interface ClListLike extends ClBraced {
  ClSymbol[] getAllSymbols();

  <T> T findFirstChildByClass(Class<T> aClass);

}
