package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.psi.ClStubElementType;
import org.jetbrains.plugins.clojure.psi.ClojureBaseElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClKeywordStub;

/**
 * @author ilyas
*/
public class ClKeywordImpl extends ClojureBaseElementImpl<ClKeywordStub> implements ClKeyword {
  public ClKeywordImpl(ASTNode node) {
    super(node);
  }

  public ClKeywordImpl(ClKeywordStub stub, @NotNull ClStubElementType nodeType) {
    super(stub, nodeType);
  }

  @Override
  public String toString() {
    return "ClKeyword";
  }

  @Override
  public String getName() {
    return getText();
  }

  public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
    throw new IncorrectOperationException("Name changing for the keyword");
  }
}
