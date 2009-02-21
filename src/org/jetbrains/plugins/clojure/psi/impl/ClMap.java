package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClMap extends ClojurePsiElementImpl {
  public ClMap(ASTNode node) {
    super(node);
  }
}
