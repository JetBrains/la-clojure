package org.jetbrains.plugins.clojure.refactoring.rename;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
public class RenameClojureFileProcessor extends RenamePsiElementProcessor {
  @Override
  public boolean canProcessElement(@NotNull PsiElement element) {
    return false; //todo: ?
  }
}
