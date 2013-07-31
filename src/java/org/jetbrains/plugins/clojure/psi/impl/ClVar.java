package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;

/**
 * @author ilyas
*/
public class ClVar extends ClojurePsiElementImpl {
  public ClVar(ASTNode node) {
    super(node);
  }
}
