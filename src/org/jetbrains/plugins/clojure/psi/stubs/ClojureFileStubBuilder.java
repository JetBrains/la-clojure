package org.jetbrains.plugins.clojure.psi.stubs;

import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.psi.stubs.impl.ClFileStubImpl;

/**
 * @author ilyas
 */
public class ClojureFileStubBuilder extends DefaultStubBuilder {
  protected StubElement createStubForFile(final PsiFile file) {
    if (file instanceof ClojureFile && ((ClojureFile) file).isClassDefiningFile()) {
      return new ClFileStubImpl((ClojureFile)file);
    }

    return super.createStubForFile(file);
  }
}