package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;

/**
 * @author ilyas
 */
public class ClSet extends ClojurePsiElementImpl {
  public ClSet(ASTNode node) {
    super(node, "ClSet");
  }

}
