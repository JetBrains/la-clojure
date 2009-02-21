package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClList extends ClojurePsiElementImpl {
  public ClList(ASTNode node) {
    super(node, "ClList");
  }
}
