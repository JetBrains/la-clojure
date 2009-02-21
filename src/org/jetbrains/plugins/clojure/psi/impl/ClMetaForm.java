package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClMetaForm extends ClojurePsiElementImpl {
  public ClMetaForm(ASTNode node) {
    super(node);
  }
}
