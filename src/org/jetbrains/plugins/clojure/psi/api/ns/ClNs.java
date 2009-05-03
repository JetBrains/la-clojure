package org.jetbrains.plugins.clojure.psi.api.ns;

import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiNamedElement;

/**
 * @author ilyas
 */
public interface ClNs extends ClList, PsiNamedElement {
  @Nullable
  ClSymbol getNameSymbol();

  String getDefinedName();
}
