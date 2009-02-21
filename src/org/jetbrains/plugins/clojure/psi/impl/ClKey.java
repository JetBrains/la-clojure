package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.parser.ClojurePsiElement;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClKey extends ClojurePsiElement {
  public ClKey(ASTNode node) {
    super(node);
  }
}
