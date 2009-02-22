package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;

/**
 * @author ilyas
*/
public class ClQuotedForm extends ClojurePsiElementImpl {
  public ClQuotedForm(ASTNode node) {
    super(node);
  }
}
