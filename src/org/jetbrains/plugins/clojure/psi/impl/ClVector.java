package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;

/**
 * @author ilyas
*/
public class ClVector extends ClojurePsiElementImpl {
  public ClVector(ASTNode node) {
    super(node, "ClVector");
  }
}
