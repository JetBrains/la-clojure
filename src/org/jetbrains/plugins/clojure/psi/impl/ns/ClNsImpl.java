package org.jetbrains.plugins.clojure.psi.impl.ns;

import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;
import org.jetbrains.plugins.clojure.psi.api.ClListLike;
import org.jetbrains.plugins.clojure.psi.impl.list.ClListBaseImpl;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClNsStub;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;

/**
 * @author ilyas
 */
public class ClNsImpl extends ClListBaseImpl<ClNsStub> implements ClNs, StubBasedPsiElement<ClNsStub> {

  private static final String IMPORT_KEY = ":import";
  private static final String USE_KEY = ":use";

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
    final PsiElement element = getSecondNonLeafElement();
    if (element instanceof ClSymbol) {
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
    final GlobalSearchScope scope = GlobalSearchScope.allScope(getProject());
    for (PsiElement element : getChildren()) {
      if (element instanceof ClList) {
        ClList directive = (ClList) element;
        final PsiElement first = directive.getFirstNonLeafElement();
        if (first instanceof ClKeyword) {
          final String keyText = first.getText();
          if (IMPORT_KEY.equals(keyText) && processImports(processor, place, facade, directive)) return false;
          if (USE_KEY.equals(keyText) && processUses(processor, place, facade, directive)) return false;
        }
      }
    }
    return true;
  }

  private boolean processUses(PsiScopeProcessor processor, PsiElement place, JavaPsiFacade facade, ClList directive) {
    for (ClSymbol symbol : directive.getAllSymbols()) {
      for (PsiNamedElement element : NamespaceUtil.getDeclaredElements(symbol.getNameString(), directive.getProject())) {
        if (element != null && !ResolveUtil.processElement(processor, element)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean processImports(PsiScopeProcessor processor, PsiElement place, JavaPsiFacade facade, ClList child) {
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
                    return true;
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
}