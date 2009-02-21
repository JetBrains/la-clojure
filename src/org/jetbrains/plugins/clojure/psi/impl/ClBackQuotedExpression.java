package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClBackQuotedExpression extends ClojurePsiElementImpl {
  public ClBackQuotedExpression(ASTNode node) {
    super(node);
  }
}
