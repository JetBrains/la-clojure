package org.jetbrains.plugins.clojure.psi.impl.ns;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.*;
import com.intellij.psi.scope.NameHint;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClListLike;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.impl.list.ClListBaseImpl;
import org.jetbrains.plugins.clojure.psi.impl.list.ListDeclarations;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.clojure.psi.resolve.processors.ResolveProcessor;
import org.jetbrains.plugins.clojure.psi.resolve.processors.SymbolResolveProcessor;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClNsStub;
import org.jetbrains.plugins.clojure.psi.util.ClojureKeywords;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiFactory;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;

/**
 * @author ilyas
 */
public class ClNsImpl extends ClListBaseImpl<ClNsStub> implements ClNs, StubBasedPsiElement<ClNsStub> {

  public ClNsImpl(ClNsStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public ClNsImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClNs";
  }

  @Override
  @Nullable
  public String getName() {
    return getDefinedName();
  }

  /**
   * @return Name of string symbol defined
   */
  @Nullable
  public ClSymbol getNameSymbol() {
    PsiElement element = getSecondNonLeafElement();
    while (element != null && !(element instanceof ClSymbol)) {
      element = element.getNextSibling();
    }
    if (element != null) {
      return (ClSymbol) element;
    }
    return null;
  }

  public String getDefinedName() {
    ClSymbol sym = getNameSymbol();
    if (sym != null) {
      String name = sym.getText();
      assert name != null;
      return name;
    }
    return "";
  }

  public PsiElement setName(@NonNls String name) throws IncorrectOperationException {
    //todo implement me
    return this;
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
    final JavaPsiFacade facade = JavaPsiFacade.getInstance(getProject());
    for (PsiElement element : getChildren()) {
      if (element instanceof ClList) {
        ClList directive = (ClList) element;
        final PsiElement first = directive.getFirstNonLeafElement();
        if (first == null) {
          return false;
        }
        final String headText = first.getText();
        if (processImports(processor, place, facade, directive, headText)) {
          return false;
        }
        if (processUses(processor, place, facade, directive, headText)) {
          return false;
        }
        if (processRequires(processor, place, facade, directive, headText)) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean processRequires(PsiScopeProcessor processor, PsiElement place, JavaPsiFacade facade, ClList directive, String headText) {
    if (!ClojureKeywords.REQUIRE.equals(headText) &&
        !ListDeclarations.REQUIRE.equals(headText)) {
      return false;
    }

    final ClListLike[] clauses = PsiTreeUtil.getChildrenOfType(directive, ClListLike.class);
    if (clauses == null) return false;

    // process :as aliases for namespaces
    for (ClListLike clause : clauses) {
      final PsiElement first = clause.getNonLeafElement(1);
      final PsiElement second = clause.getNonLeafElement(2);
      final PsiElement third = clause.getNonLeafElement(3);
      if (first instanceof ClSymbol && third instanceof ClSymbol &&
          second instanceof ClKeyword && ClojureKeywords.AS.equals(second.getText())) {
        final ClSymbol from = (ClSymbol) first;
        final ClSymbol to = (ClSymbol) third;
        NameHint nameHint = processor.getHint(NameHint.KEY);
        String alias = nameHint == null ? null : nameHint.getName(ResolveState.initial());
        if (alias != null && alias.equals(to.getName())) {
          for (ResolveResult result : from.multiResolve(false)) {
            final PsiElement element = result.getElement();
            if (element instanceof PsiNamedElement) {
              PsiNamedElement namedElement = (PsiNamedElement) element;
              return processor.execute(namedElement, ResolveState.initial());
            }
          }
        } else if (nameHint == null) {
          processor.execute(to, ResolveState.initial());
        }
      }
    }
    return false;
  }

  private boolean processUses(PsiScopeProcessor processor, PsiElement place, JavaPsiFacade facade, ClList directive, String headText) {
    if (!ClojureKeywords.USE.equals(headText) &&
        !ListDeclarations.USE.equals(headText)) {
      return false;
    }

    for (ClSymbol symbol : directive.getAllSymbols()) {
      for (PsiNamedElement element : NamespaceUtil.getDeclaredElements(symbol.getNameString(), directive.getProject())) {
        if (element != null && !ResolveUtil.processElement(processor, element)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean processImports(PsiScopeProcessor processor, PsiElement place, JavaPsiFacade facade, ClList child, String headText) {
    if (!ClojureKeywords.IMPORT.equals(headText) && !ListDeclarations.IMPORT.equals(headText)) {
      return false;
    }

    for (PsiElement stmt : child.getChildren()) {
      if (stmt instanceof ClListLike) {
        final ClListLike listLike = (ClListLike) stmt;
        final PsiElement fst = listLike.getFirstNonLeafElement();
        if (fst instanceof ClSymbol) {
          final PsiPackage pack = facade.findPackage(((ClSymbol) fst).getNameString());
          if (pack != null) {
            if (place.getParent() == listLike && place != fst) {
              pack.processDeclarations(processor, ResolveState.initial(), null, place);
            } else {
              PsiElement next = fst.getNextSibling();
              while (next != null) {
                if (next instanceof ClSymbol) {
                  ClSymbol clazzSym = (ClSymbol) next;
                  final PsiClass clazz = facade.findClass(pack.getQualifiedName() + "." + clazzSym.getNameString(), GlobalSearchScope.allScope(getProject()));
                  if (clazz != null && !ResolveUtil.processElement(processor, clazz)) {
                    return false;
                  }
                  if (clazz != null) {
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
      }
    }
    return false;
  }

  @Override
  public int getTextOffset() {
    final ClSymbol symbol = getNameSymbol();
    if (symbol != null) {
      return symbol.getTextRange().getStartOffset();
    }
    return super.getTextOffset();
  }

  public ClList findImportClause(@Nullable final PsiElement place) {
    final PsiElement element = ContainerUtil.find(getChildren(), new Condition<PsiElement>() {
      public boolean value(PsiElement psiElement) {
        return psiElement instanceof ClList &&
            (place == null || ClojurePsiUtil.isStrictlyBefore(psiElement, place)) &&
            ClojureKeywords.IMPORT.equals(((ClList) psiElement).getHeadText());
      }
    });
    return (ClList) element;
  }

  @NotNull
  public ClList findOrCreateImportClause(@Nullable PsiElement place) {
    final ClList imports = findImportClause(place);
    if (imports != null) return imports;
    return addFreshImportClause();
  }

  public ClList findImportClause() {
    return findImportClause(null);
  }

  @NotNull
  public ClList findOrCreateImportClause() {
    return findOrCreateImportClause(null);
  }

  public ClListLike addImportForClass(PsiElement place, PsiClass clazz) {
    commitDocument();
    final ClojurePsiFactory factory = ClojurePsiFactory.getInstance(getProject());
    final ClList importClause = findOrCreateImportClause(place);
    return factory.findOrCreateJavaImportForClass(clazz, importClause);
  }

  @NotNull
  protected ClList addFreshImportClause() {
    commitDocument();
    final ClSymbol first = getFirstSymbol();
    final ClSymbol nsSymbol = getNameSymbol();
    final PsiElement preamble = findGenClassPreamble();

    final PsiElement anchor = (preamble != null ? preamble :
        nsSymbol != null ? nsSymbol : first);
    assert first != null;

    final ClojurePsiFactory factory = ClojurePsiFactory.getInstance(getProject());
    final ClList list = factory.createListFromText(":import ");
    return (ClList) addAfter(list, anchor);
  }

  protected PsiElement findGenClassPreamble() {
    return ContainerUtil.find(getChildren(), new Condition<PsiElement>() {
      public boolean value(PsiElement psiElement) {
        return (psiElement instanceof ClList) &&
            (ClojureKeywords.GEN_CLASS.equals(((ClList) psiElement).getHeadText()));
      }
    });
  }

}