package org.jetbrains.plugins.clojure.psi.api;

import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiFileWithStubSupport;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * @author ilyas
 */
public interface ClojureFile extends PsiFile, ClojurePsiElement, PsiFileWithStubSupport {

  boolean isClassDefiningFile();

  @Nullable
  String getNamespace();

}
