package org.jetbrains.plugins.clojure.psi.impl.symbols;

import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.TailType;
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
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClDefNameIndex;
import org.jetbrains.plugins.clojure.psi.resolve.completion.CompletionProcessor;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.clojure.psi.resolve.ClojureResolveResult;
import org.jetbrains.plugins.clojure.psi.impl.list.ListDeclarations;
import org.jetbrains.plugins.clojure.psi.impl.ns.ClSyntheticNamespace;

import java.util.*;

/**
 * @author ilyas
 */
public class CompleteSymbol {

  public static Object[] getVariants(ClSymbol symbol) {
    Collection<Object> variants = new ArrayList<Object>();

    ClSymbol qualifier = symbol.getQualifierSymbol();
    final CompletionProcessor processor = new CompletionProcessor(symbol);
    if (qualifier == null) {
      ResolveUtil.treeWalkUp(symbol, processor);
    } else {
      for (ResolveResult result : qualifier.multiResolve(false)) {
        final PsiElement element = result.getElement();
        if (element != null) {
          final PsiElement sep = symbol.getSeparatorToken();
          if (sep != null) {
            if ("/".equals(sep.getText()) && isNamespaceLike(element)) {
              element.processDeclarations(processor, ResolveState.initial(), null, symbol);
            } else if (".".equals(sep.getText())) {
              element.processDeclarations(processor, ResolveState.initial(), null, symbol);
            }
          }
        }
      }
    }

    final ClojureResolveResult[] candidates = processor.getCandidates();
    if (candidates.length == 0) return PsiNamedElement.EMPTY_ARRAY;

    // Add everything resolved
    final PsiElement[] psiElements = ResolveUtil.mapToElements(candidates);
    variants.addAll(Arrays.asList(mapToLookupItems(psiElements)));

    // Add Java methods for all imported classes
    final boolean withoutDot = mayBeMethodReference(symbol);
    if (symbol.getChildren().length == 0 && symbol.getText().startsWith(".") ||
            withoutDot) {
      addJavaMethods(psiElements, variants, withoutDot);
    }

    return variants.toArray(new Object[variants.size()]);
  }

  private static boolean isNamespaceLike(PsiElement element) {
    return element instanceof PsiClass || element instanceof ClSyntheticNamespace;
  }

  private static boolean mayBeMethodReference(ClSymbol symbol) {
    final PsiElement parent = symbol.getParent();
    if (parent == null) return false;

//    if (parent.getParent() instanceof ClList && ".".equals(((ClList) parent.getParent()).getHeadText())) return true;
    return false;
  }

  private static LookupItem[] mapToLookupItems(PsiElement[] elements) {
    final List<LookupItem> list = ContainerUtil.map(elements, new Function<PsiElement, LookupItem>() {
      public LookupItem fun(PsiElement element) {
        final LookupItem item = new LookupItem<PsiElement>(element, element instanceof PsiNamedElement ? ((PsiNamedElement) element).getName() : element.toString());
        if (element instanceof ClDef) {
          ClDef def = (ClDef) element;
          item.setTailType(TailType.SPACE);
          item.setAttribute(LookupItem.TAIL_TEXT_ATTR, " " + def.getParameterString());
          item.setAttribute(LookupItem.TYPE_TEXT_ATTR, def.getContainingFile().getName());
        }
        return item;
      }
    });
    return list.toArray(LookupItem.EMPTY_ARRAY);
  }

  private static void addJavaMethods(PsiElement[] psiElements, Collection<Object> variants, boolean withoutDot) {
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

      final LookupItem item = new LookupItem(methodText, (!withoutDot ? "." : "") + name);
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
        if (clazz.getName().equals("Pattern")) {
          System.out.println("");
        }
        for (PsiMethod method : clazz.getAllMethods()) {
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

}
