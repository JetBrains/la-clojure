package org.jetbrains.plugins.clojure.psi.api.defs;

import com.sun.istack.internal.Nullable;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

/**
 * @author ilyas
 */
public interface ClDef extends ClojurePsiElement {
  @Nullable
  ClSymbol getNameSymbol();

  String getDefinedName();
}
