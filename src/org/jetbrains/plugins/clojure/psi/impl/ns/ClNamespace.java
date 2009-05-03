package org.jetbrains.plugins.clojure.psi.impl.ns;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClNsNameIndex;

import java.util.Collection;

/**
 * @author ilyas
 */
public class ClNamespace {

  public static PsiElement[] getDeclaredElements(String nsFqn, Project project) {
    final Collection<ClNs> nses = StubIndex.getInstance().get(ClNsNameIndex.KEY, nsFqn, project, GlobalSearchScope.allScope(project));
    for (ClNs ns : nses) {

    }
    return PsiElement.EMPTY_ARRAY;
  }

}
