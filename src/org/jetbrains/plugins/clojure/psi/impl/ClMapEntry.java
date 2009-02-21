package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
 */
public class ClMapEntry extends ClojurePsiElementImpl {
  public ClMapEntry(@NotNull ASTNode astNode) {
    super(astNode, "ClMapEntry");
  }
}
