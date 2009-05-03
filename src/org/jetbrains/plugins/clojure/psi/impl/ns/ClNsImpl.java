package org.jetbrains.plugins.clojure.psi.impl.ns;

import com.intellij.lang.ASTNode;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.impl.list.ClListBaseImpl;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClNsStub;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;

/**
 * @author ilyas
 */
public class ClNsImpl extends ClListBaseImpl<ClNsStub> implements ClNs, StubBasedPsiElement<ClNsStub> {

  public ClNsImpl(ClNsStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public ClNsImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClNs";
  }

  @Override
  @Nullable
  public String getName() {
    return getDefinedName();
  }

  /**
   * @return Name of string symbol defined
   */
  @Nullable
  public ClSymbol getNameSymbol() {
    final ClSymbol first = findChildByClass(ClSymbol.class);
    if (first == null) return null;
    return ClojurePsiUtil.findNextSiblingByClass(first, ClSymbol.class);
  }

  public String getDefinedName() {
    ClSymbol sym = getNameSymbol();
    if (sym != null) {
      String name = sym.getText();
      assert name != null;
      return name;
    }
    return "";
  }


  public PsiElement setName(@NonNls String name) throws IncorrectOperationException {
    //todo implement me
    return this;
  }

  @Override
   public int getTextOffset() {
    final ClSymbol symbol = getNameSymbol();
    if (symbol != null) {
      return symbol.getTextRange().getStartOffset();
    }
    return super.getTextOffset();
  }
}