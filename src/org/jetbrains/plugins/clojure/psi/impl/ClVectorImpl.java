package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClVector;

/**
 * @author ilyas
*/
public class ClVectorImpl extends ClojurePsiElementImpl implements ClVector {
  public ClVectorImpl(ASTNode node) {
    super(node, "ClVector");
  }
}
