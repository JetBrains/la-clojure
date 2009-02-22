package org.jetbrains.plugins.clojure.psi.api;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.impl.symbols.ClSymbol;
import com.sun.istack.internal.Nullable;

/**
 * @author ilyas
 */
public interface ClList extends ClojurePsiElement{
  @Nullable
  String getPresentableText();

  @Nullable
  ClSymbol getFirstSymbol();
}
