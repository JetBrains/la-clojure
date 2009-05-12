package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.ClVector;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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

  public ClSymbol[] getAllSymbols() {
    return findChildrenByClass(ClSymbol.class);
  }

  public ClSymbol[] getOddSymbols() {
    final ClojurePsiElement[] elems = findChildrenByClass(ClojurePsiElement.class);
    final ArrayList<ClSymbol> res = new ArrayList<ClSymbol>();
    for (int i = 0; i < elems.length; i++) {
      ClojurePsiElement elem = elems[i];
      if (i % 2 == 0 && elem instanceof ClSymbol) {
        res.add((ClSymbol) elem);
      }
    }
    return res.toArray(new ClSymbol[res.size()]);
  }
}
