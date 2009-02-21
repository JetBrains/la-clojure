package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClMetadata extends ClojurePsiElementImpl {
  public ClMetadata(ASTNode node) {
    super(node);
  }
}
