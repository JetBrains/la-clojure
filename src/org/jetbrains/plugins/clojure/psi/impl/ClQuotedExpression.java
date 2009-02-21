package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.parser.ClojurePsiElement;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClQuotedExpression extends ClojurePsiElement {
  public ClQuotedExpression(ASTNode node) {
    super(node);
  }
}
