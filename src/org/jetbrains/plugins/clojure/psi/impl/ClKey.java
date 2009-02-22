package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;

/**
 * @author ilyas
*/
public class ClKey extends ClojurePsiElementImpl {
  public ClKey(ASTNode node) {
    super(node, "ClKeyword");
  }
}
