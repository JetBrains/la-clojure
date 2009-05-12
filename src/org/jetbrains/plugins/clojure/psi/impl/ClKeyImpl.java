package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;

/**
 * @author ilyas
*/
public class ClKeyImpl extends ClojurePsiElementImpl implements ClKeyword {
  public ClKeyImpl(ASTNode node) {
    super(node, "ClKeyword");
  }
}
