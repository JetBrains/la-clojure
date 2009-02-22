package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClSet;

/**
 * @author ilyas
 */
public class ClSetImpl extends ClojurePsiElementImpl implements ClSet {

  public ClSetImpl(ASTNode node) {
    super(node, "ClSet");
  }

}
