package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;

/**
 * @author ilyas
*/
public class ClMetaForm extends ClojurePsiElementImpl {
  public ClMetaForm(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "MetaForm";
  }

}
