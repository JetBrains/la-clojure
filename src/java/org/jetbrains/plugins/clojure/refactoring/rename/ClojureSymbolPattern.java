package org.jetbrains.plugins.clojure.refactoring.rename;

import com.intellij.patterns.ObjectPattern;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

/**
 * @author ilyas
 */
public class ClojureSymbolPattern extends ObjectPattern<ClSymbol, ClojureSymbolPattern>{
  public ClojureSymbolPattern() {
    super(ClSymbol.class);
  }
}
