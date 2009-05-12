package org.jetbrains.plugins.clojure.psi.impl.list;

import org.jetbrains.plugins.clojure.psi.ClojureBaseElementImpl;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.NamedStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.lang.ASTNode;

/**
 * @author ilyas
 */
public abstract class ClListBaseImpl<T extends NamedStub> extends ClojureBaseElementImpl<T> implements ClList {

  public ClListBaseImpl(T stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public ClListBaseImpl(ASTNode node) {
    super(node);
  }

  @Nullable
  public String getPresentableText() {
    final ClSymbol first = findChildByClass(ClSymbol.class);
    if (first == null) return null;
    final String text1 = getHeadText();
    PsiElement next = ClojurePsiUtil.findNextSiblingByClass(first, ClSymbol.class);
    if (next == null) return text1;
    else return text1 + " " + next.getText();
  }

  @Nullable
  public String getHeadText() {
    final ClSymbol first = findChildByClass(ClSymbol.class);
    if (first == null) return null;
    return first.getText();
  }

  @Nullable
  public ClSymbol getFirstSymbol() {
    PsiElement child = getFirstChild();
    while (child instanceof LeafPsiElement) {
      child = child.getNextSibling();
    }
    if (child instanceof ClSymbol) {
      return (ClSymbol) child;
    }
    return null;
  }

  @NotNull
  public PsiElement getFirstBrace() {
    PsiElement element = findChildByType(ClojureTokenTypes.LEFT_PAREN);
    assert element != null;
    return element;
  }

  public PsiElement getSecondNonLeafElement() {
    PsiElement first = getFirstChild();
    while (first != null && isWrongElement(first)) {
      first = first.getNextSibling();
    }
    PsiElement second = first.getNextSibling();
    while (second != null && isWrongElement(second)) {
      second = second.getNextSibling();
    }
    return second;
  }

  public PsiElement getLastBrace() {
    return findChildByType(ClojureTokenTypes.RIGHT_PAREN);
  }


  public ClSymbol[] getAllSymbols() {
    return findChildrenByClass(ClSymbol.class);
  }
}
