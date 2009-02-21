package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.parser.ClojurePsiElement;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClVar extends ClojurePsiElement {
  public ClVar(ASTNode node) {
    super(node);
  }
}
