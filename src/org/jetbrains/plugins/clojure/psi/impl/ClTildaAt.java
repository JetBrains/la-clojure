package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClTildaAt extends ClojurePsiElementImpl {
  public ClTildaAt(ASTNode node) {
    super(node);
  }
}
