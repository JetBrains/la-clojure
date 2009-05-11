package org.jetbrains.plugins.clojure.psi.stubs;

import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiField;
import com.intellij.psi.stubs.StubIndex;
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
public class ClojureShortNamesCache implements PsiShortNamesCache {

  Project myProject;

  public ClojureShortNamesCache(Project project) {
    myProject = project;
  }


  public void runStartupActivity() {
  }

  @NotNull
  public PsiFile[] getFilesByName(@NotNull String name) {
    return new PsiFile[0];
  }

  @NotNull
  public String[] getAllFileNames() {
    return FilenameIndex.getAllFilenames();
  }

  private boolean areClassesCompiled() {
    ClojureCompilerSettings settings = ClojureCompilerSettings.getInstance(myProject);
    return settings.COMPILE_CLOJURE;
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

  private Collection<PsiClass> getScriptClassesByFQName(final String name, final GlobalSearchScope scope) {
    Collection<ClojureFile> scripts = StubIndex.getInstance().get(ClojureFullScriptNameIndex.KEY, name.hashCode(), myProject, scope);

    scripts = ContainerUtil.findAll(scripts, new Condition<ClojureFile>() {
      public boolean value(final ClojureFile clojureFile) {
        final PsiClass clazz = clojureFile.getDefinedClass();
        return clojureFile.isClassDefiningFile() && clazz != null && name.equals(clazz.getQualifiedName());
      }
    });
    return ContainerUtil.map(scripts, new Function<ClojureFile, PsiClass>() {
      public PsiClass fun(final ClojureFile clojureFile) {
        return clojureFile.getDefinedClass();
      }
    });
  }

/*
  public Collection<ClNs> getAllNameSpaces(final GlobalSearchScope scope) {
    final Collection<String> nsNames = StubIndex.getInstance().getAllKeys(ClojureNsNameIndex.KEY);

    StubIndex.getInstance().get(ClojureNsNameIndex.KEY, name.hashCode(), myProject, scope)
  }
*/


  @NotNull
  public String[] getAllClassNames() {
    if (!areClassesCompiled()) return new String[0];

    final Collection<String> classNames = StubIndex.getInstance().getAllKeys(ClojureClassNameIndex.KEY);
    return classNames.toArray(new String[classNames.size()]);
  }

  public void getAllClassNames(@NotNull HashSet<String> dest) {
    if (!areClassesCompiled()) return;

    final Collection<String> classNames = StubIndex.getInstance().getAllKeys(ClojureClassNameIndex.KEY);
    dest.addAll(classNames);
  }

  @Nullable
  public PsiClass getClassByFQName(@NotNull @NonNls String name, @NotNull GlobalSearchScope scope) {
    if (!areClassesCompiled()) return null;

    final Collection<PsiClass> scriptClasses = getScriptClassesByFQName(name, scope);
    for (PsiClass clazz : scriptClasses) {
      if (name.equals(clazz.getQualifiedName())) return clazz;
    }
    return null;
  }

  @NotNull
  public PsiClass[] getClassesByFQName(@NotNull @NonNls String fqn, @NotNull GlobalSearchScope scope) {
    if (!areClassesCompiled()) return PsiClass.EMPTY_ARRAY;

    final Collection<PsiClass> result = getScriptClassesByFQName(fqn, scope);
    ArrayList<PsiClass> filtered = new ArrayList<PsiClass>();
    for (PsiClass clazz : result) {
      if (fqn.equals(clazz.getQualifiedName())) {
        filtered.add(clazz);
      }
    }
    return filtered.isEmpty() ? PsiClass.EMPTY_ARRAY : filtered.toArray(new PsiClass[filtered.size()]);
  }


  @NotNull
  public PsiMethod[] getMethodsByName(@NonNls String name, @NotNull GlobalSearchScope scope) {
    return new PsiMethod[0];
  }

  @NotNull
  public PsiMethod[] getMethodsByNameIfNotMoreThan(@NonNls String name, @NotNull GlobalSearchScope scope, int maxCount) {
    return new PsiMethod[0];
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

}
