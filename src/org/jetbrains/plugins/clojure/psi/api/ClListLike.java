package org.jetbrains.plugins.clojure.psi.api;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

/**
 * @author ilyas
 */
public interface ClListLike extends ClojurePsiElement {
  ClSymbol[] getAllSymbols();

  <T> T findFirstChildByClass(Class<T> aClass);

}
