package org.jetbrains.plugins.clojure.psi;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

/**
 * @author ilyas
 */
public abstract class ClStubElementType<S extends StubElement, T extends ClojurePsiElement> extends IStubElementType<S, T> {

  public ClStubElementType(@NonNls @NotNull String debugName) {
    super(debugName, ClojureFileType.CLOJURE_LANGUAGE);
  }

  public abstract PsiElement createElement(final ASTNode node);

  public void indexStub(final S stub, final IndexSink sink) {
  }

  public String getExternalId() {
    return "clj." + super.toString();
  }

}
