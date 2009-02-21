package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClTilda extends ClojurePsiElementImpl {
  public ClTilda(ASTNode node) {
    super(node);
  }
}
