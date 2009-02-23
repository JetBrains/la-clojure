package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClMap;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
*/
public class ClMapImpl extends ClojurePsiElementImpl implements ClMap {
  public ClMapImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClMap";
  }

  @NotNull
  public PsiElement getFirstBrace() {
    PsiElement element = findChildByType(ClojureTokenTypes.LEFT_CURLY);
    assert element != null;
    return element;
  }

  public PsiElement getLastBrace() {
    return findChildByType(ClojureTokenTypes.RIGHT_CURLY);
  }

  
}
