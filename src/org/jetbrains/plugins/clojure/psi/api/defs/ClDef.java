package org.jetbrains.plugins.clojure.psi.api.defs;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClMetadata;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

/**
 * @author ilyas
 */
public interface ClDef extends ClList, PsiNamedElement, NavigationItem {
  @Nullable
  ClSymbol getNameSymbol();

  String getDefinedName();

  String getPresentationText();

  @Nullable
  String getDocString();

  String getParameterString();

  @Nullable
  ClMetadata getMeta();
}
