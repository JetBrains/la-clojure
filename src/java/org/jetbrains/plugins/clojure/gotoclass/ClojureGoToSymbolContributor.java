package org.jetbrains.plugins.clojure.gotoclass;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import org.jetbrains.plugins.clojure.psi.stubs.index.ClDefNameIndex;

/**
 * @author ilyas
 */
public class ClojureGoToSymbolContributor implements ChooseByNameContributor {
  public String[] getNames(Project project, boolean includeNonProjectItems) {
    Set<String> symbols = new HashSet<String>();
    symbols.addAll(StubIndex.getInstance().getAllKeys(ClDefNameIndex.KEY, project));
    return symbols.toArray(new String[symbols.size()]);

  }

  public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
    final GlobalSearchScope scope = includeNonProjectItems ? null : GlobalSearchScope.projectScope(project);

    List<NavigationItem> symbols = new ArrayList<NavigationItem>();
    symbols.addAll(StubIndex.getInstance().get(ClDefNameIndex.KEY, name, project, scope));
    return symbols.toArray(new NavigationItem[symbols.size()]);
  }
}
