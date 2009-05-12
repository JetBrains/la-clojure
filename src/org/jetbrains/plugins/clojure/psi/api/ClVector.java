package org.jetbrains.plugins.clojure.psi.api;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

/**
 * @author ilyas
 */
public interface ClVector extends ClojurePsiElement, ClBraced, ClListLike {

  ClSymbol[] getOddSymbols();
}
