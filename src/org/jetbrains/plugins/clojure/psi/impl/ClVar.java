package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClVar extends ClojurePsiElementImpl {
  public ClVar(ASTNode node) {
    super(node);
  }
}
