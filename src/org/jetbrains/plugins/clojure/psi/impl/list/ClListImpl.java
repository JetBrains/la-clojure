package org.jetbrains.plugins.clojure.psi.impl.list;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.NamedStub;
import org.jetbrains.plugins.clojure.psi.api.ClList;

/**
 * @author ilyas
*/
public class ClListImpl extends ClListBaseImpl<NamedStub> implements ClList {

  public ClListImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClList"; 
  }

}
