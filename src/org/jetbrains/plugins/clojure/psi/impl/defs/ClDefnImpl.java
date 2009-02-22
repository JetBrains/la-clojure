package org.jetbrains.plugins.clojure.psi.impl.defs;

import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClDefnImpl extends ClDefImpl {

  public ClDefnImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClDefn";
  }
}
