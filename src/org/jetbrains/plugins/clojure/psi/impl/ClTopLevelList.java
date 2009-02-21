package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
*/
public class ClTopLevelList extends ClList {
  public ClTopLevelList(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClTopLevelList";
  }
}
