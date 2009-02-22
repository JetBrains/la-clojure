package org.jetbrains.plugins.clojure.psi.impl.defs;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.impl.ClList;
import org.jetbrains.plugins.clojure.psi.impl.ClSymbol;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

/**
 * @author ilyas
*/
public class ClDef extends ClList {

  public ClDef(ASTNode node) {
    super(node);
  }

  protected ClojurePsiElementImpl getDefinition(String symbol) {
    ClSymbol sym = getNameSymbol();
    if (sym != null) {
      String name = sym.getText();
      assert name != null;
      //System.out.println(symbol + " " + this + " " + name);
      if (name.equals(symbol)) {
        return this;
      }
    }

    return super.getDefinition(symbol);
  }

  protected ClSymbol getNameSymbol() {
    PsiElement[] children = getChildren();
    if (children.length > 0 && children[0] instanceof ClSymbol)
      return (ClSymbol) children[0];
    return null;
  }

  public String getName() {
    ClSymbol sym = getNameSymbol();
    if (sym != null) {
      String name = sym.getText();
      assert name != null;
      return name;
    }
    return "";
  }
}
