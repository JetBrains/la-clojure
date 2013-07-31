package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;

/**
 * @author ilyas
*/
public class ClAt extends ClojurePsiElementImpl {
  public ClAt(ASTNode node) {
    super(node);
  }
}
