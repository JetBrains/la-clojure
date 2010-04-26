package org.jetbrains.plugins.clojure.psi.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;

import java.util.List;

/**
 * @author ilyas
 */
public interface ClMetadata extends ClojurePsiElement {

  @NotNull
  List<ClKeyword> getKeys();

  @Nullable
  ClojurePsiElement getValue(String key);

}
