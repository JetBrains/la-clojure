package org.jetbrains.plugins.clojure.psi.api.defs;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.impl.symbols.ClSymbol;
import com.sun.istack.internal.Nullable;

/**
 * @author ilyas
 */
public interface ClDef extends ClojurePsiElement {
  @Nullable
  ClSymbol getNameSymbol();

  String getDefinedName();
}
