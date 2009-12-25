package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClSet;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
public class ClSetImpl extends ClojurePsiElementImpl implements ClSet {

  public ClSetImpl(ASTNode node) {
    super(node, "ClSet");
  }

  @NotNull
  public PsiElement getFirstBrace() {
    // XXX: there must be a cleaner way of doing this...
    ASTNode sharp;
    while ((sharp = getNode().findChildByType(ClojureTokenTypes.SHARP)) != null) {
      ASTNode next = sharp.getTreeNext();
      if (ClojureTokenTypes.LEFT_CURLY.equals(next.getElementType())) {
        return sharp.getPsi();
      }
    }
    throw new AssertionError();
  }

  public PsiElement getLastBrace() {
    return findChildByType(ClojureTokenTypes.RIGHT_CURLY);
  }

}
