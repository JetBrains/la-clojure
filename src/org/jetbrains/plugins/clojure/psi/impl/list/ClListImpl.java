package org.jetbrains.plugins.clojure.psi.impl.list;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.NamedStub;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.annotations.NotNull;

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

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
    return ListDeclarations.get(processor, state, lastParent, place, this, getHeadText());
  }
}
