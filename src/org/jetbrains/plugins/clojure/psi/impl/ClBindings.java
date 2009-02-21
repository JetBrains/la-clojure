package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClBindings extends ClojurePsiElementImpl {
  public ClBindings(ASTNode node) {
    super(node);
  }
}
