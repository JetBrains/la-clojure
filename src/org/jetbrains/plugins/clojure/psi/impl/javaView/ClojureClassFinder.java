package org.jetbrains.plugins.clojure.psi.impl.javaView;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.impl.ClojurePsiManager;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.compiler.ClojureCompilerSettings;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author ilyas
 */
public class ClojureClassFinder implements ProjectComponent, PsiElementFinder {
  private final Project myProject;

  public ClojureClassFinder(Project project) {
    myProject = project;
  }

  @Nullable
  public PsiClass findClass(@NotNull String qualifiedName, @NotNull GlobalSearchScope scope) {
    return ClojurePsiManager.getInstance(myProject).getNamesCache().getClassByFQName(qualifiedName, scope);
  }

  @NotNull
  public PsiClass[] findClasses(@NotNull String qualifiedName, @NotNull GlobalSearchScope scope) {
    return ClojurePsiManager.getInstance(myProject).getNamesCache().getClassesByFQName(qualifiedName, scope);
  }

  @Nullable
  public PsiPackage findPackage(@NotNull String qualifiedName) {
    return null;
  }

  @NotNull
  public PsiPackage[] getSubPackages(@NotNull PsiPackage psiPackage, @NotNull GlobalSearchScope scope) {
    return new PsiPackage[0];
  }

  @NotNull
  public PsiClass[] getClasses(@NotNull PsiPackage psiPackage, @NotNull GlobalSearchScope scope) {
    if (!ClojureCompilerSettings.getInstance(psiPackage.getProject()).COMPILE_CLOJURE) return PsiClass.EMPTY_ARRAY;

    List<PsiClass> result = new ArrayList<PsiClass>();
    for (final PsiDirectory dir : psiPackage.getDirectories(scope)) {
      for (final PsiFile file : dir.getFiles()) {
        if (file instanceof ClojureFile) {
          ClojureFile clojureFile = (ClojureFile) file;
          if (clojureFile.isClassDefiningFile() && clojureFile.getPackageName().equals(psiPackage.getQualifiedName())) {
            result.add(clojureFile.getDefinedClass());
          }
        }
      }
    }

    return result.toArray(new PsiClass[result.size()]);
  }

  public void projectOpened() {
  }

  public void projectClosed() {
  }

  @NonNls
  @NotNull
  public String getComponentName() {
    return "ClojureClassFinder";
  }

  public void initComponent() {
  }

  public void disposeComponent() {
  }
}