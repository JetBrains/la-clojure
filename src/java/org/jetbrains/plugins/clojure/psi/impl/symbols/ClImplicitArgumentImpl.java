package org.jetbrains.plugins.clojure.psi.impl.symbols;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
public class ClImplicitArgumentImpl extends ClSymbolImpl {
  public ClImplicitArgumentImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClImplicitArgument";
  }

  @NotNull
  public String getNameString() {
    return getText();
  }
}
