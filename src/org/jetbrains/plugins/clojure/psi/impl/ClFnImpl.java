package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClFn;

/**
 * @author ilyas
*/
public class ClFnImpl extends ClojurePsiElementImpl implements ClFn {
  public ClFnImpl(ASTNode node) {
    super(node);
  }
}
