package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.parser.ClojurePsiElement;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClTilda extends ClojurePsiElement {
  public ClTilda(ASTNode node) {
    super(node);
  }
}
