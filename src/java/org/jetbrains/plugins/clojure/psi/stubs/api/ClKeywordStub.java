package org.jetbrains.plugins.clojure.psi.stubs.api;

import com.intellij.psi.stubs.NamedStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;

/**
 * @author ilyas
 */
public interface ClKeywordStub extends NamedStub<ClKeyword>  {
  @NotNull
  String getName();
}
