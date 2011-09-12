package org.jetbrains.plugins.clojure.psi.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;

/**
 * @author ilyas
 */
public abstract class ClojurePsiFactory {

  public static ClojurePsiFactory getInstance(Project project) {
    return ServiceManager.getService(project, ClojurePsiFactory.class);
  }

  public abstract ASTNode createSymbolNodeFromText(@NotNull String newName); 

  public abstract boolean hasSyntacticalErrors(@NotNull String text);

  public abstract String getErrorMessage(@NotNull String text);

  public abstract ClojureFile createClojureFileFromText(@NotNull String text);

  public abstract ClList createListFromText(@NotNull String text);

  @Nullable
  public abstract ClList createJavaImportForClass(PsiClass clazz);
}
