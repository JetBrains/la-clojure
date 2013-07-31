package org.jetbrains.plugins.clojure.gotoclass;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.PsiClass;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.Function;

import java.util.Collection;
import java.util.List;

import org.jetbrains.plugins.clojure.psi.stubs.index.ClojureClassNameIndex;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.compiler.ClojureCompilerSettings;

/**
 * @author ilyas
 */
public class ClojureGoToClassContributor implements ChooseByNameContributor {
  public String[] getNames(Project project, boolean includeNonProjectItems) {
    if (!ClojureCompilerSettings.getInstance(project).getState().COMPILE_CLOJURE) return new String[0];
    
    final Collection<String> classNames = StubIndex.getInstance().getAllKeys(ClojureClassNameIndex.KEY, project);
    return classNames.toArray(new String[classNames.size()]);
  }

  public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
    if (!ClojureCompilerSettings.getInstance(project).getState().COMPILE_CLOJURE) return NavigationItem.EMPTY_NAVIGATION_ITEM_ARRAY;

    final GlobalSearchScope scope = includeNonProjectItems ? null : GlobalSearchScope.projectScope(project);
    Collection<ClojureFile> files = StubIndex.getInstance().get(ClojureClassNameIndex.KEY, name, project, scope);
    List<PsiClass> scriptClasses = ContainerUtil.map(files, new Function<ClojureFile, PsiClass>() {
      public PsiClass fun(ClojureFile clojureFile) {
        assert clojureFile.isClassDefiningFile();
        return clojureFile.getDefinedClass();
      }
    });
    return scriptClasses.toArray(new NavigationItem[scriptClasses.size()]);
  }

}