package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.parser.ClojurePsiElement;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClFn extends ClojurePsiElement {
  public ClFn(ASTNode node) {
    super(node);
  }
}
