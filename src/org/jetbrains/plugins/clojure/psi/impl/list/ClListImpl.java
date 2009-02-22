package org.jetbrains.plugins.clojure.psi.impl.list;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.NamedStub;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.ClojurePsiUtil;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

/**
 * @author ilyas
*/
public class ClListImpl extends ClListBaseImpl<NamedStub> implements ClList {

  public ClListImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClList";
  }

}
