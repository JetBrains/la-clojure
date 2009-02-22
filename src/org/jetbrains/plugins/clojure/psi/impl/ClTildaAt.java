package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;

/**
 * @author ilyas
*/
public class ClTildaAt extends ClojurePsiElementImpl {
  public ClTildaAt(ASTNode node) {
    super(node);
  }
}
