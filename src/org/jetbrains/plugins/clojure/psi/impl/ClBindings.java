package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;

/**
 * @author ilyas
*/
public class ClBindings extends ClojurePsiElementImpl {
  public ClBindings(ASTNode node) {
    super(node);
  }
}
