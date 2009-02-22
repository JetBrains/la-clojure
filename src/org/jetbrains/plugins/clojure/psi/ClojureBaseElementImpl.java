package org.jetbrains.plugins.clojure.psi;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
 */
public class ClojureBaseElementImpl <T extends StubElement> extends StubBasedPsiElementBase<T> implements ClojurePsiElement {

  public ClojureBaseElementImpl(T stub, @org.jetbrains.annotations.NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public ClojureBaseElementImpl(ASTNode node) {
    super(node);
  }
  
}
