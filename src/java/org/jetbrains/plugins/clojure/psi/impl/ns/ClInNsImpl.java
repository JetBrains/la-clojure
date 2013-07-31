package org.jetbrains.plugins.clojure.psi.impl.ns;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.api.ClQuotedForm;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClNsStub;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;

/**
 * @author ilyas
 */
public class ClInNsImpl extends ClNsImpl{

  public ClInNsImpl(ClNsStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public ClInNsImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public ClSymbol getNameSymbol() {
    final PsiElement element = getSecondNonLeafElement();
    if (element instanceof ClQuotedForm) {
      final ClQuotedForm form = (ClQuotedForm) element;
      final ClojurePsiElement elt = form.getQuotedElement();
      if (elt instanceof ClSymbol) {
        return (ClSymbol) elt;
      }
      return null;
    }
    return null;
  }


}
