package org.jetbrains.plugins.clojure.psi.impl.defs;

import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClDefnDashImpl extends ClDefImpl {

  public ClDefnDashImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClDen-";
  }
}
