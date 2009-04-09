package org.jetbrains.plugins.clojure.psi.impl.symbols;

import com.intellij.lang.ASTNode;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
*/
public class ClSymbolImpl extends ClojurePsiElementImpl implements ClSymbol {
  public ClSymbolImpl(ASTNode node) {
    super(node);
  }

  @Override
  public PsiReference getReference() {
    return this;
  }

  @Override
  public String toString() {
    return "ClSymbol";
  }

  @NotNull
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    //todo implement me!
    return new ResolveResult[0];
  }

  public PsiElement getElement() {
    return this;
  }

  public TextRange getRangeInElement() {
    return new TextRange(0, getTextLength());
  }

  public PsiElement resolve() {
    //todo implement me!
    return null;
  }

  public String getCanonicalText() {
    return null;
  }

  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return this;
  }

  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    return this;
  }

  public boolean isReferenceTo(PsiElement element) {
    return false;
  }

  public Object[] getVariants() {
    return CompleteSymbol.getVariants(this);
  }

  public boolean isSoft() {
    return false;
  }

  @NotNull
  public String getNameString() {
    //todo is it safe?
    return getText();
  }
}
