package org.jetbrains.plugins.clojure.psi.api;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;

/**
 * @author ilyas
 */
public interface ClQuotedForm {
  @Nullable
  ClojurePsiElement getQuotedElement();
}
