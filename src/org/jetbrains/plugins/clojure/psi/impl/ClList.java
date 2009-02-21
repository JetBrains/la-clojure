package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.parser.ClojurePsiElement;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClList extends ClojurePsiElement {
  public ClList(ASTNode node) {
    super(node);
  }
}
