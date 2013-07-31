package org.jetbrains.plugins.clojure.psi.api;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.impl.ClMapEntry;

import java.util.List;

/**
 * @author ilyas
 */
public interface ClMap extends ClojurePsiElement, ClBraced {
  List<ClMapEntry> getEntries();

  @Nullable
  ClojurePsiElement getValue(final String key);
}
