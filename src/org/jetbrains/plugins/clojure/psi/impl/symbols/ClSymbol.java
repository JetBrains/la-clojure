package org.jetbrains.plugins.clojure.psi.impl.symbols;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.IncorrectOperationException;

/**
 * @author ilyas
*/
public class ClSymbol extends ClojurePsiElementImpl {
  public ClSymbol(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClSymbol";
  }

}
