package org.jetbrains.plugins.clojure.psi.impl.list;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.HashSet;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClQuotedForm;
import org.jetbrains.plugins.clojure.psi.api.ClVector;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.impl.ImportOwner;
import org.jetbrains.plugins.clojure.psi.impl.ns.ClSyntheticNamespace;
import org.jetbrains.plugins.clojure.psi.impl.ns.NamespaceUtil;
import org.jetbrains.plugins.clojure.psi.impl.symbols.ClSymbolImpl;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.clojure.psi.util.ClojureKeywords;

import java.util.Arrays;
import java.util.Set;

/**
 * @author ilyas
 */
public class ListDeclarations {

  public static final String LET = "let";
  public static final String WITH_OPEN = "with-open";
  public static final String WITH_LOCAL_VARS = "with-local-vars";
  public static final String WHEN_LET = "when-let";
  public static final String WHEN_FIRST = "when-let";
  public static final String FOR = "for";
  public static final String IF_LET = "if-let";
  public static final String LOOP = "loop";
  public static final String DOSEQ = "doseq";
  public static final String DECLARE = "declare";
  public static final String FN = "fn";

  public static final String NS = "ns";

  public static final String DEFN = "defn";
  public static final String DEFN_ = "defn-";
  public static final String IMPORT = "import";
  private static final String MEMFN = "memfn";
  public static final String USE = "use";
  public static final String REFER = "refer";
  public static final String REQUIRE = "require";

  private static final String DOT = ".";

  private static final Set<String> LOCAL_BINDINGS = new HashSet<String>(Arrays.asList(
      LET, WITH_OPEN, WITH_LOCAL_VARS, WHEN_LET, WHEN_FIRST, FOR, IF_LET, LOOP, FN, DOSEQ
  ));

  public static boolean get(PsiScopeProcessor processor,
                            ResolveState state,
                            PsiElement lastParent,
                            PsiElement place,
                            ClList list,
                            @Nullable String headText) {
    if (headText == null) return true;
    if (headText.equals(FN)) return processFnDeclaration(processor, list, place, lastParent);
    if (headText.equals(IMPORT)) return ImportOwner.processImports(processor, place, list, headText);
    if (headText.equals(USE)) return ImportOwner.processUses(processor, place, list, headText);
    if (headText.equals(REFER)) return ImportOwner.processRefer(processor, place, list, headText);
    if (headText.equals(REQUIRE)) return ImportOwner.processRequires(processor, place, list, headText);
    if (headText.equals(MEMFN)) return processMemFnDeclaration(processor, list, place);
    if (headText.equals(DOT)) return processDotDeclaration(processor, list, place, lastParent);
    if (headText.equals(LOOP)) return processLoopDeclaration(processor, list, place, lastParent);
    if (headText.equals(DOSEQ)) return processDoseqDeclaration(processor, list, place, lastParent);
    if (headText.equals(DECLARE)) return processDeclareDeclaration(processor, list, place, lastParent);
    if (LOCAL_BINDINGS.contains(headText)) return processLetDeclaration(processor, list, place);

    final PsiElement parent = list.getParent();
    if (parent != null && parent instanceof ClList) {
      return getWithParentContext(processor, list, ((ClList) parent), state, lastParent, place);
    }

    return true;
  }

  private static boolean getWithParentContext(PsiScopeProcessor processor, ClList list, ClList parent,
                                              ResolveState state, PsiElement lastParent, PsiElement place) {
    final String parentHead = parent.getHeadText();
    final PsiElement first = list.getFirstNonLeafElement();
    if (processUseParent(processor, place, parentHead, first, state, lastParent)) return true;
    return true;
  }

  /*
    Handle (:use ...) parent, e.g.

    (ns my-namespace
     (:use (clojure <caret> data inspector)))

   */
  private static boolean processUseParent(PsiScopeProcessor processor, PsiElement place, String parentHead,
                                          PsiElement first, ResolveState state, PsiElement lastParent) {
    if ((USE.equals(parentHead) || ClojureKeywords.USE.equals(parentHead)) &&
        first instanceof ClSymbol) {
      final ClSymbol symbol = (ClSymbol) first;
      final ClSyntheticNamespace namespace = NamespaceUtil.getNamespace(symbol.getNameString(), place.getProject());
      return namespace == null? true : namespace.processDeclarations(processor, state, lastParent, place);
    }
    return true;
  }

  private static boolean processDeclareDeclaration(PsiScopeProcessor processor, ClList list, PsiElement place, PsiElement lastParent) {
    final ClVector paramVector = list.findFirstChildByClass(ClVector.class);
    if (paramVector != null) {
      for (ClSymbol symbol : paramVector.getOddSymbols()) {
        if (!ResolveUtil.processElement(processor, symbol)) return false;
      }
    }
    return true;
  }

  private static boolean processLoopDeclaration(PsiScopeProcessor processor, ClList list, PsiElement place, PsiElement lastParent) {
    if (lastParent != null && lastParent.getParent() == list) {
      final ClVector paramVector = list.findFirstChildByClass(ClVector.class);
      if (paramVector != null) {
        for (ClSymbol symbol : paramVector.getOddSymbols()) {
          if (!ResolveUtil.processElement(processor, symbol)) return false;
        }
      }
      return true;
    }
    return true;
  }


  private static boolean processDoseqDeclaration(PsiScopeProcessor processor, ClList list, PsiElement place, PsiElement lastParent) {
    if (lastParent != null && lastParent.getParent() == list) {
      final ClVector paramVector = list.findFirstChildByClass(ClVector.class);
      if (paramVector != null) {
        for (ClSymbol symbol : paramVector.getOddSymbols()) {
          if (!ResolveUtil.processElement(processor, symbol)) return false;
        }
      }
      return true;
    }
    return true;
  }

  private static boolean processDotDeclaration(PsiScopeProcessor processor, ClList list, PsiElement place, PsiElement lastParent) {
    final PsiElement parent = place.getParent();
    if (parent == null || list == parent) return true;

    final PsiElement second = list.getSecondNonLeafElement();
    if (second instanceof ClSymbol && place != second) {
      ClSymbol symbol = (ClSymbol) second;
      for (ResolveResult result : symbol.multiResolve(false)) {
        final PsiElement element = result.getElement();
        if (element instanceof PsiNamedElement && !ResolveUtil.processElement(processor, (PsiNamedElement) element)) {
          return false;
        }
      }

      if (lastParent == null || lastParent == list) {
        return true;
      }
      if (parent.getParent() == list) {
        if (place instanceof ClSymbol && ((ClSymbol) place).getQualifierSymbol() == null) {
          ClSymbol symbol2 = (ClSymbol) place;
          ResolveResult[] results = ClSymbolImpl.MyResolver.resolveJavaMethodReference(symbol2, list, true);
          for (ResolveResult result : results) {
            final PsiElement element = result.getElement();
            if (element instanceof PsiNamedElement && !ResolveUtil.processElement(processor, (PsiNamedElement) element)) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }

  private static boolean processMemFnDeclaration(PsiScopeProcessor processor, ClList list, PsiElement place) {
    if (place instanceof ClSymbol && place.getParent() == list && ((ClSymbol) place).getQualifierSymbol() == null) {
      ClSymbol symbol = (ClSymbol) place;
      ResolveResult[] results = ClSymbolImpl.MyResolver.resolveJavaMethodReference(symbol, list.getParent(), true);
      for (ResolveResult result : results) {
        final PsiElement element = result.getElement();
        if (element instanceof PsiNamedElement && !ResolveUtil.processElement(processor, (PsiNamedElement) element)) {
          return false;
        }
      }
    }

    return true;
  }

  private static boolean processLetDeclaration(PsiScopeProcessor processor, ClList list, PsiElement place) {
    if (PsiTreeUtil.findCommonParent(place, list) == list) {
      final ClVector paramVector = list.findFirstChildByClass(ClVector.class);
      if (paramVector != null) {
        for (ClSymbol symbol : paramVector.getOddSymbols()) {
          if (!ResolveUtil.processElement(processor, symbol)) return false;
        }
      }
      return true;
    }
    return true;
  }

  private static boolean processFnDeclaration(PsiScopeProcessor processor, ClList list, PsiElement place, PsiElement lastParent) {
    final PsiElement second = list.getSecondNonLeafElement();

    if (lastParent == second) return true;

    if ((second instanceof ClSymbol) && !ResolveUtil.processElement(processor, ((ClSymbol) second)))
      return false;

    if (PsiTreeUtil.findCommonParent(place, list) == list) {
      ClVector paramVector = list.findFirstChildByClass(ClVector.class);
      if (paramVector == null && lastParent instanceof ClList) {
        paramVector = ((ClList) lastParent).findFirstChildByClass(ClVector.class);
      }

      if (paramVector != null) {
        for (ClSymbol symbol : paramVector.getAllSymbols()) {
          if (!ResolveUtil.processElement(processor, symbol)) return false;
        }
      }
      return true;
    }
    return true;

  }

  public static boolean isLocal(PsiElement element) {
    if (element instanceof ClSymbol) {
      ClSymbol symbol = (ClSymbol) element;
      final PsiElement parent = symbol.getParent();

      if (parent instanceof ClList) {
        ClList list = (ClList) parent;
        if (FN.equals(list.getHeadText())) return true;
      } else if (parent instanceof ClVector) {
        final PsiElement par = parent.getParent();
        if (par instanceof ClDef && ((ClDef) par).getSecondNonLeafElement() == element) return true;
        if (par instanceof ClList) {
          final String ht = ((ClList) par).getHeadText();
          if (LOCAL_BINDINGS.contains(ht)) return true;
        }
      }
    }

    return false;
  }
}
