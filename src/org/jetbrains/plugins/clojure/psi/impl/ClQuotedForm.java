package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClQuotedForm extends ClojurePsiElementImpl {
  public ClQuotedForm(ASTNode node) {
    super(node);
  }
}
