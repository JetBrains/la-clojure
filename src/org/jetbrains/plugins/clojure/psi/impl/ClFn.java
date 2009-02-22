package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;

/**
 * @author ilyas
*/
public class ClFn extends ClojurePsiElementImpl {
  public ClFn(ASTNode node) {
    super(node);
  }
}
