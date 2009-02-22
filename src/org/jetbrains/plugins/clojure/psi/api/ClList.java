package org.jetbrains.plugins.clojure.psi.api;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import com.sun.istack.internal.Nullable;

/**
 * @author ilyas
 */
public interface ClList extends ClojurePsiElement{
  @Nullable
  String getPresentableText();
}
