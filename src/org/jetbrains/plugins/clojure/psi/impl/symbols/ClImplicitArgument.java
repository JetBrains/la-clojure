package org.jetbrains.plugins.clojure.psi.impl.symbols;

import com.intellij.lang.ASTNode;

/**
 * @author ilyas
 */
public class ClImplicitArgument extends ClSymbol{
  public ClImplicitArgument(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClImplicitArgument";
  }
}
