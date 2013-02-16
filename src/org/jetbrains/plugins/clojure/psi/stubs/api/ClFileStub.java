package org.jetbrains.plugins.clojure.psi.stubs.api;

import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.util.io.StringRef;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;

/**
 * @author ilyas
 */
public interface ClFileStub extends PsiFileStub<ClojureFile> {
  StringRef getPackageName();

  StringRef getClassName();

  boolean isClassDefinition();
}

