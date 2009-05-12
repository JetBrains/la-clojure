package org.jetbrains.plugins.clojure.psi.impl.ns;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Trinity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClojureNsNameIndex;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ilyas
 */
public class NamespaceUtil {

  public static final String[] DEFAULT_NSES = new String[]{"clojure.core",
//          "clojure.inspector",
//          "clojure.main",
//          "clojure.parallel",
//          "clojure.set",
//          "clojure.zip",
//          "clojure.xml"
  };

  public static PsiNamedElement[] getDeclaredElements(@NotNull String nsFqn, @NotNull Project project) {
    final Collection<ClNs> nses = StubIndex.getInstance().get(ClojureNsNameIndex.KEY, nsFqn, project, GlobalSearchScope.allScope(project));
    ArrayList<PsiNamedElement> result = new ArrayList<PsiNamedElement>();

    for (ClNs ns : nses) {
      final PsiFile file = ns.getContainingFile();
      if (file instanceof ClojureFile) {
        ClojureFile clf = (ClojureFile) file;
        final PsiElement[] elems = PsiTreeUtil.collectElements(clf, new PsiElementFilter() {
          public boolean isAccepted(PsiElement element) {
            return element instanceof ClDef;
          }
        });

        for (PsiElement elem : elems) {
          if (elem instanceof PsiNamedElement &&
                  ((PsiNamedElement) elem).getName() != null &&
                  ((PsiNamedElement) elem).getName().length() > 0 &&
                  suitsByPosition(((PsiNamedElement) elem), ns)) {
            result.add(((PsiNamedElement) elem));
          }
        }
      }
    }
    return result.toArray(PsiNamedElement.EMPTY_ARRAY);
  }

  public static PsiNamedElement[] getDefaultDefinitions(@NotNull Project project) {
    final ArrayList<PsiNamedElement> res = new ArrayList<PsiNamedElement>();
    for (String ns : DEFAULT_NSES) {
      res.addAll(Arrays.asList(getDeclaredElements(ns, project)));
    }
    return res.toArray(PsiNamedElement.EMPTY_ARRAY);
  }

  private static boolean suitsByPosition(PsiNamedElement candidate, ClNs ns) {
    final Trinity<PsiElement, PsiElement, PsiElement> tr = ClojurePsiUtil.findCommonParentAndLastChildren(ns, candidate);
    final PsiElement nsParent = tr.getSecond();
    final PsiElement candParent = tr.getThird();
    return ClojurePsiUtil.lessThan(nsParent, candParent);
  }


}
