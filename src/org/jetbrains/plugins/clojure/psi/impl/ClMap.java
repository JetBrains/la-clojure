package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.parser.ClojurePsiElement;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClMap extends ClojurePsiElement {
  public ClMap(ASTNode node) {
    super(node);
  }
}
