package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClVector extends ClojurePsiElementImpl {
  public ClVector(ASTNode node) {
    super(node, "ClVector");
  }
}
