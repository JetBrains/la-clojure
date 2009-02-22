package org.jetbrains.plugins.clojure.psi.api;

import com.sun.istack.internal.Nullable;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

/**
 * @author ilyas
 */
public interface ClList extends ClojurePsiElement{
  @Nullable
  String getPresentableText();

  @Nullable
  ClSymbol getFirstSymbol();
}
