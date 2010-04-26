package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;

/**
 * @author ilyas
 */
public interface ClMapEntry extends ClojurePsiElement {

  ClKeyword getKey();

  @Nullable
  ClojurePsiElement getValue();
}
