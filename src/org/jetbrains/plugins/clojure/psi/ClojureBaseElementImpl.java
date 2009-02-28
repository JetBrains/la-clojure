package org.jetbrains.plugins.clojure.psi;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.PsiComment;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
 */
public abstract class ClojureBaseElementImpl <T extends StubElement> extends StubBasedPsiElementBase<T> implements ClojurePsiElement {

  private boolean isWrongElement(PsiElement element) {
    return element == null ||
        (element instanceof LeafPsiElement || element instanceof PsiWhiteSpace || element instanceof PsiComment);
  }

  public PsiElement getFirstNonLeafElement() {
    PsiElement first = getFirstChild();
    while (isWrongElement(first)) {
      first = first.getNextSibling();
    }
    return first;
  }

  public PsiElement getLastNonLeafElement() {
    PsiElement lastChild = getLastChild();
    while (isWrongElement(lastChild)) {
      lastChild = lastChild.getPrevSibling();
    }
    return lastChild;
  }

  public ClojureBaseElementImpl(T stub, @org.jetbrains.annotations.NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public ClojureBaseElementImpl(ASTNode node) {
    super(node);
  }
  
}
