package org.jetbrains.plugins.clojure.psi.impl.ns;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Trinity;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClojureNsNameIndex;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClojureClassNameIndex;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
      if (nsFqn.equals(ns.getName())) {
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

  public static ClSyntheticNamespace[] getTopLevelNamespaces(@NotNull Project project) {
    ArrayList<ClSyntheticNamespace> result = new ArrayList<ClSyntheticNamespace>();
    for (String fqn : StubIndex.getInstance().getAllKeys(ClojureNsNameIndex.KEY)) {
      if (!fqn.contains(".")) {
        result.add(getNamespace(fqn, project));
      }
    }
    return result.toArray(new ClSyntheticNamespace[result.size()]);
  }

  @Nullable
  public static ClSyntheticNamespace getNamespace(@NotNull String fqn, @NotNull final Project project) {
    final Collection<ClNs> nsWithPrefix = StubIndex.getInstance().get(ClojureNsNameIndex.KEY, fqn, project, GlobalSearchScope.allScope(project));
    if (!nsWithPrefix.isEmpty()) {
      final ClNs ns = nsWithPrefix.iterator().next();
      final String nsName = ns.getName();
      assert nsName != null;
      final String synthName = nsName.equals(fqn) ? nsName : fqn;
      final String refName = StringUtil.getShortName(synthName);

      return new ClSyntheticNamespace(PsiManager.getInstance(project), refName, synthName) {
        @Override
        public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {

          // Add inner namespaces
          for (String fqn : StubIndex.getInstance().getAllKeys(ClojureNsNameIndex.KEY)) {
            final String outerName = getQualifiedName();
            if (fqn.startsWith(outerName) && !fqn.equals(outerName) &&
                    !StringUtil.trimStart(fqn, outerName + ".").contains(".")) {
              final ClSyntheticNamespace inner = getNamespace(fqn, project);
              if (!ResolveUtil.processElement(processor, inner)) {
                return false;
              }

            }
          }

          // Add declared elements
          for (PsiNamedElement element : getDeclaredElements(getQualifiedName(), getProject())) {
            if (!ResolveUtil.processElement(processor, element)) {
              return false;
            }
          }

          return true;
        }

      };

    }
    return null;
  }


}
