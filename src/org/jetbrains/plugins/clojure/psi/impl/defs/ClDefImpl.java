package org.jetbrains.plugins.clojure.psi.impl.defs;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.ClojurePsiUtil;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.impl.ClListImpl;

/**
 * @author ilyas
*/
public class ClDefImpl extends ClListImpl implements ClDef {

  public ClDefImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClDef";
  }

  /**
   * @return Name of string symbol defined
   */
  @Nullable
  public ClSymbol getNameSymbol() {
    final ClSymbol first = findChildByClass(ClSymbol.class);
    if (first == null) return null;
    assert "def".equals(first.getText());
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
}
