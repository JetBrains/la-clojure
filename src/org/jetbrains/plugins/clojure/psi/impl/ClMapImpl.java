package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClMap;

/**
 * @author ilyas
*/
public class ClMapImpl extends ClojurePsiElementImpl implements ClMap {
  public ClMapImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClMap";
  }
}
