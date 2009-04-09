package org.jetbrains.plugins.clojure.psi.api;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.impl.source.PsiFileWithStubSupport;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NonNls;

/**
 * @author ilyas
 */
public interface ClojureFile extends PsiFile, ClojurePsiElement, PsiFileWithStubSupport {

  boolean isClassDefiningFile();

  @Nullable
  String getNamespace();

  @Nullable
  ClList getNamespaceElement();

  @NotNull
  String getPackageName();

  @Nullable
  String getClassName();

  PsiElement setClassName(@NonNls String s);

  @Nullable
  PsiClass getDefinedClass();
}
