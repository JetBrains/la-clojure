package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClVector;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
*/
public class ClVectorImpl extends ClojurePsiElementImpl implements ClVector {
  public ClVectorImpl(ASTNode node) {
    super(node, "ClVector");
  }

  @NotNull
  public PsiElement getFirstBrace() {
    PsiElement element = findChildByType(ClojureTokenTypes.LEFT_SQUARE);
    assert element != null;
    return element;
  }

  public PsiElement getLastBrace() {
    return findChildByType(ClojureTokenTypes.RIGHT_SQUARE);
  }
}
