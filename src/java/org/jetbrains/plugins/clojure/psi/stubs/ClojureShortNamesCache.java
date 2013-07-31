package org.jetbrains.plugins.clojure.psi.stubs;

import com.intellij.psi.*;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Processor;
import com.intellij.util.containers.HashSet;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.Function;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClojureClassNameIndex;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClojureFullScriptNameIndex;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClojureNsNameIndex;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.compiler.ClojureCompilerSettings;

import java.util.Collection;
import java.util.ArrayList;

/**
 * @author ilyas
 */
public class ClojureShortNamesCache extends PsiShortNamesCache {

  Project myProject;

  public static ClojureShortNamesCache getInstance(Project project) {
    return new ClojureShortNamesCache(project);
  }

  public ClojureShortNamesCache(Project project) {
    myProject = project;
  }

  public ClNs[] getNsByQualifiedName(String qualifiedName, GlobalSearchScope scope) {
    final Collection<? extends PsiElement> clNses = StubIndex.getInstance().get(ClojureNsNameIndex.KEY, qualifiedName, myProject, scope);
    ArrayList<ClNs> result = new ArrayList<ClNs>();
    for (PsiElement clNs : clNses) {
      if (clNs instanceof ClNs) {
        if (((ClNs) clNs).getName().equals(qualifiedName)) {
          result.add((ClNs) clNs);
        }
      }
    }
    return result.toArray(new ClNs[result.size()]);
  }


  @NotNull
  public PsiFile[] getFilesByName(@NotNull String name) {
    return new PsiFile[0];
  }

  @NotNull
  public String[] getAllFileNames() {
    return FilenameIndex.getAllFilenames(myProject);
  }

  private boolean areClassesCompiled() {
    ClojureCompilerSettings settings = ClojureCompilerSettings.getInstance(myProject);
    return settings.getState().COMPILE_CLOJURE;
  }

  @NotNull
  public PsiClass[] getClassesByName(@NotNull String name, @NotNull GlobalSearchScope scope) {
    if (!areClassesCompiled()) return PsiClass.EMPTY_ARRAY;

    Collection<PsiClass> allClasses = getAllScriptClasses(name, scope);
    if (allClasses.isEmpty()) return PsiClass.EMPTY_ARRAY;
    return allClasses.toArray(new PsiClass[allClasses.size()]);
  }

  private Collection<PsiClass> getAllScriptClasses(String name, GlobalSearchScope scope) {
    if (!areClassesCompiled()) return new ArrayList<PsiClass>();

    Collection<ClojureFile> files = StubIndex.getInstance().get(ClojureClassNameIndex.KEY, name, myProject, scope);
    files = ContainerUtil.findAll(files, new Condition<ClojureFile>() {
      public boolean value(ClojureFile clojureFile) {
        return clojureFile.isClassDefiningFile();
      }
    });
    return ContainerUtil.map(files, new Function<ClojureFile, PsiClass>() {
      public PsiClass fun(ClojureFile clojureFile) {
        assert clojureFile.isClassDefiningFile();
        return clojureFile.getDefinedClass();
      }
    });
  }

  @NotNull
  public String[] getAllClassNames() {
    if (!areClassesCompiled()) return new String[0];

    final Collection<String> classNames = StubIndex.getInstance().getAllKeys(ClojureClassNameIndex.KEY, myProject);
    return classNames.toArray(new String[classNames.size()]);
  }

  public void getAllClassNames(@NotNull HashSet<String> dest) {
    if (!areClassesCompiled()) return;

    final Collection<String> classNames = StubIndex.getInstance().getAllKeys(ClojureClassNameIndex.KEY, myProject);
    dest.addAll(classNames);
  }

  @NotNull
  public PsiMethod[] getMethodsByName(@NonNls String name, @NotNull GlobalSearchScope scope) {
    return new PsiMethod[0];
  }

  @NotNull
  public PsiMethod[] getMethodsByNameIfNotMoreThan(@NonNls String name, @NotNull GlobalSearchScope scope, int maxCount) {
    return new PsiMethod[0];
  }

  @Override
  public boolean processMethodsWithName(@NonNls @NotNull String s, @NotNull GlobalSearchScope globalSearchScope, @NotNull Processor<PsiMethod> psiMethodProcessor) {
    return false;
  }

  @NotNull
  public String[] getAllMethodNames() {
    return new String[0];
  }

  public void getAllMethodNames(@NotNull HashSet<String> set) {
  }

  @NotNull
  public PsiField[] getFieldsByName(@NotNull String name, @NotNull GlobalSearchScope scope) {
    return new PsiField[0];
  }

  @NotNull
  public String[] getAllFieldNames() {
    return new String[0];
  }

  public void getAllFieldNames(@NotNull HashSet<String> set) {
  }

  @NotNull
  @Override
  public PsiField[] getFieldsByNameIfNotMoreThan(@NonNls @NotNull String s, @NotNull GlobalSearchScope globalSearchScope, int i) {
    return new PsiField[0];
  }
}
