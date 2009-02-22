package org.jetbrains.plugins.clojure.psi.api.defs;

import com.intellij.psi.PsiNamedElement;
import com.intellij.navigation.NavigationItem;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.annotations.Nullable;

/**
 * @author ilyas
 */
public interface ClDef extends ClojurePsiElement, PsiNamedElement, NavigationItem {
  @Nullable
  ClSymbol getNameSymbol();

  String getDefinedName();
}
