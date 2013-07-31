package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClLiteral;

/**
 * @author ilyas
*/
public class ClLiteralImpl extends ClojurePsiElementImpl implements ClLiteral {
  public ClLiteralImpl(ASTNode node) {
    super(node, "ClLiteral");
  }
}
