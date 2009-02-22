package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.ClojurePsiUtil;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.impl.symbols.ClSymbol;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;

/**
 * @author ilyas
*/
public class ClListImpl extends ClojurePsiElementImpl implements ClList {

  public ClListImpl(ASTNode node) {
    super(node, "ClList");
  }

  @Nullable
  public String getPresentableText() {
    final ClSymbol first = findChildByClass(ClSymbol.class);
    if (first == null) return null;
    PsiElement next = ClojurePsiUtil.findNextSiblingByClass(first, ClSymbol.class);
    final String text1 = first.getText();
    if (next == null) return text1;
    else return text1 + " " + next.getText();
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
  
}
