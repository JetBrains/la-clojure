package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;

/**
 * @author ilyas
 */
public class ClMapEntry extends ClojurePsiElementImpl {
  public ClMapEntry(@NotNull ASTNode astNode) {
    super(astNode, "ClMapEntry");
  }
}
