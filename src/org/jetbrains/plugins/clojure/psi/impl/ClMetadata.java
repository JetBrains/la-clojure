package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.parser.ClojurePsiElement;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClMetadata extends ClojurePsiElement {
  public ClMetadata(ASTNode node) {
    super(node);
  }
}
