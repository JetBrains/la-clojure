package org.jetbrains.plugins.clojure.psi.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
public abstract class ClojurePsiElementFactory {

  public static ClojurePsiElementFactory getInstance(Project project) {
    return ServiceManager.getService(project, ClojurePsiElementFactory.class);
  }

  public abstract ASTNode createSymbolNodeFromText(@NotNull String newName); 

  public abstract boolean hasSyntacticalErrors(@NotNull String text); 
}
