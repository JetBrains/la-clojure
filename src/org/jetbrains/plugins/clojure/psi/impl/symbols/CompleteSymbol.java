package org.jetbrains.plugins.clojure.psi.impl.symbols;

import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.MethodSignature;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Function;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.HashMap;
import com.intellij.util.containers.HashSet;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClDefNameIndex;
import org.jetbrains.plugins.clojure.psi.resolve.completion.CompletionProcessor;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.clojure.psi.resolve.ClojureResolveResult;

import java.util.*;

/**
 * @author ilyas
 */
public class CompleteSymbol {

  public static Object[] getVariants(ClSymbol symbol) {
    final CompletionProcessor processor = new CompletionProcessor(symbol);
    ResolveUtil.treeWalkUp(symbol, processor);

    final ClojureResolveResult[] candidates = processor.getCandidates();
    if (candidates.length == 0) return PsiNamedElement.EMPTY_ARRAY;

    Collection<Object> variants = new ArrayList<Object>();

    final PsiElement[] psiElements = ResolveUtil.mapToElements(candidates);
    variants.addAll(Arrays.asList(psiElements));

    if (symbol.getChildren().length == 0 && symbol.getText().startsWith(".")) {
      addJavaMethods(psiElements, variants);
    }

    return variants.toArray(new Object[variants.size()]);
  }

  private static void addJavaMethods(PsiElement[] psiElements, Collection<Object> variants) {
    final HashMap<MethodSignature, HashSet<PsiMethod>> sig2Methods = collectAvailableMethods(psiElements);

    for (Map.Entry<MethodSignature, HashSet<PsiMethod>> entry : sig2Methods.entrySet()) {
      final MethodSignature sig = entry.getKey();
      final String name = sig.getName();

      final StringBuffer buffer = new StringBuffer();
      buffer.append(name).append("(");
      buffer.append(StringUtil.join(ContainerUtil.map2Array(sig.getParameterTypes(), String.class, new Function<PsiType, String>() {
        public String fun(PsiType psiType) {
          return psiType.getPresentableText();
        }
      }), ", ")
      ).append(")");

      final String methodText = buffer.toString();

      final StringBuffer tailBuffer = new StringBuffer();
      tailBuffer.append(" in ");
      final ArrayList<String> list = new ArrayList<String>();
      for (PsiMethod method : entry.getValue()) {
        final PsiClass clazz = method.getContainingClass();
        if (clazz != null) {
          list.add(clazz.getQualifiedName());
        }
      }
      tailBuffer.append(StringUtil.join(list, ", "));

      final LookupItem item = new LookupItem(methodText, "." + name);
      item.setIcon(ClojureIcons.JAVA_METHOD);
      item.setTailText(tailBuffer.toString(), true);

      variants.add(item);
    }
  }

  public static HashMap<MethodSignature, HashSet<PsiMethod>> collectAvailableMethods(PsiElement[] psiElements) {
    final HashMap<MethodSignature, HashSet<PsiMethod>> sig2Methods = new HashMap<MethodSignature, HashSet<PsiMethod>>();
    for (PsiElement element : psiElements) {
      if (element instanceof PsiClass) {
        PsiClass clazz = (PsiClass) element;
        for (PsiMethod method : clazz.getMethods()) {
          if (!method.isConstructor() && method.hasModifierProperty(PsiModifier.PUBLIC)) {
            final MethodSignature sig = method.getSignature(PsiSubstitutor.EMPTY);
            final HashSet<PsiMethod> set = sig2Methods.get(sig);
            if (set == null) {
              final HashSet<PsiMethod> newSet = new HashSet<PsiMethod>();
              newSet.add(method);
              sig2Methods.put(sig, newSet);
            } else {
              set.add(method);
            }
          }
        }
      }
    }
    return sig2Methods;
  }

  private static Collection<LookupItem> getDefVariants(final boolean first) {
    // Completion only for first symbols
    final Collection<String> keys = StubIndex.getInstance().getAllKeys(ClDefNameIndex.KEY);
    return ContainerUtil.map(keys, new Function<String, LookupItem>() {
      public LookupItem fun(String s) {
        final LookupItem item = new LookupItem(s, s);
        item.setIcon(ClojureIcons.FUNCTION);
        if (first) {
          item.setBold();
        }
        return item;
      }
    });
  }

  private static List<Object> getJavaCompletionVariants(ClSymbol symbol, final boolean isFirst) {
    //Processing Java methods
    List<Object> list = new ArrayList<Object>();
    final JavaPsiFacade facade = JavaPsiFacade.getInstance(symbol.getProject());

    if (symbol.getChildren().length == 0 && symbol.getText().startsWith(".")) {
      final PsiShortNamesCache shortNamesCache = facade.getShortNamesCache();
      final List<Object> methods = ContainerUtil.map(shortNamesCache.getAllMethodNames(), new Function<String, Object>() {
        public LookupItem fun(String s) {
          final LookupItem item = new LookupItem(s, "." + s);
          item.setIcon(ClojureIcons.JAVA_METHOD);
          if (isFirst) {
            item.setBold();
          }
          return item;
        }
      });

      list.addAll(methods);


      final List<Object> fields = ContainerUtil.map(shortNamesCache.getAllFieldNames(), new Function<String, Object>() {
        public LookupItem fun(String s) {
          final LookupItem item = new LookupItem(s, "." + s);
          item.setIcon(ClojureIcons.JAVA_FIELD);
          if (isFirst) {
            item.setBold();
          }
          return item;
        }
      });

      list.addAll(fields);
    } else {


    }
    return list;
  }
}
