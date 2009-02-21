package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.parser.ClojurePsiElement;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClAt extends ClojurePsiElement {
  public ClAt(ASTNode node) {
    super(node);
  }
}
