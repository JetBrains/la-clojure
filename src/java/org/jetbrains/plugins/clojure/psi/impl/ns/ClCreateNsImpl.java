package org.jetbrains.plugins.clojure.psi.impl.ns;

import org.jetbrains.plugins.clojure.psi.stubs.api.ClNsStub;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
 */
public class ClCreateNsImpl extends ClNsImpl{
  public ClCreateNsImpl(ClNsStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public ClCreateNsImpl(ASTNode node) {
    super(node);
  }
}
