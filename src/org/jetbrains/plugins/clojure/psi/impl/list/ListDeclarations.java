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
import org.jetbrains.plugins.clojure.psi.impl.symbols.ClSymbolImpl;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;

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
      LET, WITH_OPEN, WITH_LOCAL_VARS, WHEN_LET, WHEN_FIRST, FOR, IF_LET, LOOP, FN
  ));

  public static boolean get(PsiScopeProcessor processor,
                            ResolveState state,
                            PsiElement lastParent,
                            PsiElement place,
                            ClList list,
                            @Nullable String headText) {
    if (headText == null) return true;
    if (headText.equals(FN)) return processFnDeclaration(processor, list, place, lastParent);
    if (headText.equals(IMPORT)) return processImportDeclaration(processor, list, place);
    if (headText.equals(MEMFN)) return processMemFnDeclaration(processor, list, place);
    if (headText.equals(DOT)) return processDotDeclaration(processor, list, place, lastParent);
    if (headText.equals(LOOP)) return processLoopDeclaration(processor, list, place, lastParent);
    if (headText.equals(DECLARE)) return processDeclareDeclaration(processor, list, place, lastParent);
    if (LOCAL_BINDINGS.contains(headText)) return processLetDeclaration(processor, list, place);
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

  private static boolean processImportDeclaration(PsiScopeProcessor processor, ClList list, PsiElement place) {
    final PsiElement[] children = list.getChildren();
    final Project project = list.getProject();
    final JavaPsiFacade facade = JavaPsiFacade.getInstance(project);

    for (PsiElement child : children) {
      if (child instanceof ClSymbol) {
        ClSymbol symbol = (ClSymbol) child;
        final String symbolName = symbol.getNameString();
        final PsiClass clazz = facade.findClass(symbolName, GlobalSearchScope.allScope(project));
        if (clazz != null && !ResolveUtil.processElement(processor, clazz)) {
          return false;
        }
      } else if (child instanceof ClQuotedForm) {
        // process import of form (import '(java.util List Set))
        ClQuotedForm quotedForm = (ClQuotedForm) child;
        final ClojurePsiElement element = quotedForm.getQuotedElement();
        if (element instanceof ClList) {
          if (processImportList(((ClList) element), processor, place, facade)) return false;
        }
      }
    }
    return true;
  }

  private static boolean processNamespaceDeclaration(PsiScopeProcessor processor, ClList list, PsiElement place, PsiElement lastParent) {
    final PsiElement[] children = list.getChildren();
    final Project project = list.getProject();
    final JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
    for (PsiElement child : children) {
      if (child instanceof ClList) {
        ClList clList = (ClList) child;
        final PsiElement first = clList.getFirstNonLeafElement();
        if (first instanceof ClKeyword && ":import".equals(first.getText())) {
          for (PsiElement importExpr : clList.getChildren()) {
            if (importExpr instanceof ClList) {
              ClList importList = (ClList) importExpr;
              if (!processImportList(importList, processor, place, facade)) return false;
            }
          }
        }
      }
    }
    return true;
  }


  private static boolean processImportList(ClList importList, PsiScopeProcessor processor, PsiElement place, JavaPsiFacade facade) {
    final PsiElement first = importList.getFirstNonLeafElement();
    if (first instanceof ClSymbol) {
      final ClSymbol packSym = (ClSymbol) first;

      final PsiPackage pack = facade.findPackage(packSym.getNameString());
      if (pack != null) {
        if (place.getParent() == importList && place != packSym) {
          pack.processDeclarations(processor, ResolveState.initial(), null, place);
        } else {
          PsiElement next = packSym.getNextSibling();
          while (next != null) {
            if (next instanceof ClSymbol) {
              ClSymbol clazzSym = (ClSymbol) next;
              final PsiClass clazz = facade.findClass(pack.getQualifiedName() + "." + clazzSym.getNameString(),
                  GlobalSearchScope.allScope(importList.getProject()));
              if (clazz != null) {
                if (!ResolveUtil.processElement(processor, clazz)) return false;
                for (PsiMethod method : clazz.getAllMethods()) {
                  if (!ResolveUtil.processElement(processor, method)) return false;
                }
                for (PsiField field : clazz.getAllFields()) {
                  if (!ResolveUtil.processElement(processor, field)) return false;
                }
              }
            }
            next = next.getNextSibling();
          }
        }
      }
    }
    return false;
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
    if ((second instanceof ClSymbol) && place != second && !ResolveUtil.processElement(processor, ((ClSymbol) second)))
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
