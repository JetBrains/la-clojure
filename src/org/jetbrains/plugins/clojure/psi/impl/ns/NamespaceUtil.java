package org.jetbrains.plugins.clojure.psi.impl.ns;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClojureNsNameIndex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

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
          for (ClDef elem : ((ClojureFile) file).getFileDefinitions()) {
            if (StringUtil.isNotEmpty(elem.getName()) && ns.getTextOffset() < elem.getTextOffset()) {
              result.add(elem);
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

  public static ClSyntheticNamespace[] getTopLevelNamespaces(@NotNull Project project) {
    ArrayList<ClSyntheticNamespace> result = new ArrayList<ClSyntheticNamespace>();
    for (String fqn : StubIndex.getInstance().getAllKeys(ClojureNsNameIndex.KEY, project)) {
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

      ClNs navigationElement = null;
      for (ClNs clNs : nsWithPrefix) {
        if (fqn.equals(clNs.getName())) {
          navigationElement = clNs;
        }
      }
      return new MyClSyntheticNamespace(project, refName, synthName, navigationElement);
    }
    return null;
  }

  private static class MyClSyntheticNamespace extends ClSyntheticNamespace {

    private final Project project;
    private final ClNs navigationElement;

    public MyClSyntheticNamespace(Project project, String refName, String synthName, ClNs navigationElement) {
      super(PsiManager.getInstance(project), refName, synthName, navigationElement);
      this.project = project;
      this.navigationElement = navigationElement;
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
      return navigationElement != null ? navigationElement : super.getNavigationElement();
    }

    @Override
    public boolean canNavigateToSource() {
      return navigationElement != null;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
      final HashSet<String> innerNamespaces = new HashSet<String>();

      // Add inner namespaces
      final String outerName = getQualifiedName();
      for (String fqn : StubIndex.getInstance().getAllKeys(ClojureNsNameIndex.KEY, project)) {
        if (fqn.startsWith(outerName) && !fqn.equals(outerName) &&
                !StringUtil.trimStart(fqn, outerName + ".").contains(".")) {
          final ClSyntheticNamespace inner = getNamespace(fqn, project);
          innerNamespaces.add(fqn);
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

      final String qualifiedName = getQualifiedName();
      final PsiPackage aPackage = JavaPsiFacade.getInstance(getProject()).findPackage(qualifiedName);
      if (aPackage != null) {
        for (PsiClass clazz : aPackage.getClasses(place.getResolveScope())) {
          if (!ResolveUtil.processElement(processor, clazz)) return false;
        }
        for (PsiPackage pack : aPackage.getSubPackages(place.getResolveScope())) {
          if (!innerNamespaces.contains(pack.getQualifiedName()) &&
              !ResolveUtil.processElement(processor, getNamespaceElement(pack))) {
            return false;
          }
        }
      }

      return true;
    }

  }

  public static ClSyntheticNamespace getNamespaceElement(PsiPackage pack) {
    return new MyClSyntheticNamespace(pack.getProject(), pack.getName(), pack.getQualifiedName(), null);
  }
}
