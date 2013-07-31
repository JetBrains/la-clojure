package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;

/**
 * @author ilyas
*/
public class ClBackQuotedExpression extends ClojurePsiElementImpl {
  public ClBackQuotedExpression(ASTNode node) {
    super(node);
  }
}
