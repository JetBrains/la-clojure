package org.jetbrains.plugins.clojure.psi.impl.symbols;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

/**
 * @author ilyas
*/
public class ClSymbolImpl extends ClojurePsiElementImpl implements ClSymbol {
  public ClSymbolImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClSymbol";
  }

}
