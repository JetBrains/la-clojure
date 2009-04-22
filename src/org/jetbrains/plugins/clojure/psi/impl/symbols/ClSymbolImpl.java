package org.jetbrains.plugins.clojure.psi.impl.symbols;

import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author ilyas
 */
public class ClSymbolImpl extends ClojurePsiElementImpl implements ClSymbol {
  public ClSymbolImpl(ASTNode node) {
    super(node);
  }

  @Override
  public PsiReference getReference() {
    return this;
  }

  @Override
  public String toString() {
    return "ClSymbol";
  }

  @NotNull
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    //todo implement me!
    return new ResolveResult[0];
  }

  public PsiElement getElement() {
    return this;
  }

  public TextRange getRangeInElement() {
    final PsiElement last = getLastChild();
    if (last instanceof LeafPsiElement && last.getNode().getElementType() == ClojureTokenTypes.symATOM) {
      return new TextRange(last.getTextOffset() - getTextOffset(), getTextLength());
    }
    return new TextRange(0, getTextLength());
  }

  public PsiElement resolve() {
    //todo implement me!
    final JavaPsiFacade facade = JavaPsiFacade.getInstance(getProject());
    final PsiClass utils = facade.findClass("org.jetbrains.benchmark.math.java.MathUtils", GlobalSearchScope.allScope(getProject()));
    final String MU = "MathUtils";
    if (getNameString().equals(MU)) {
      return utils;
    }

    final ClSymbol first = findChildByClass(ClSymbol.class);
    if (first != null && MU.equals(first.getNameString())) {
      if (utils != null) {
        final String last = getNameString().substring(MU.length() + 1);
        final PsiMethod[] methods = utils.findMethodsByName(last, false);
        for (PsiMethod method : methods) {
          return method;
        }
      }
    }

    return null;
  }

  public String getCanonicalText() {
    return null;
  }

  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    /*
val text = "package " + (if (!ScalaNamesUtil.isKeyword(name)) name else "`" + name + "`")
val dummyFile = PsiFileFactory.getInstance(manager.getProject()).
createFileFromText(DUMMY + ScalaFileType.SCALA_FILE_TYPE.getDefaultExtension(), text).asInstanceOf[ScalaFile]
return dummyFile.getNode.getLastChildNode.getLastChildNode.getLastChildNode

    */
    final String MU = "MathUtils/";
    if (getNameString().startsWith(MU)) {
      final String newName = MU + newElementName;
      final String text = "(" + newName + ")";
      final ClojureFile dummyFile = (ClojureFile) PsiFileFactory.getInstance(getProject()).createFileFromText("DUMMY.clj", text);
      final ASTNode newNode = dummyFile.getFirstChild().getFirstChild().getNextSibling().getNode();
      getParent().getNode().replaceChild(getNode(), newNode);
      return newNode.getPsi();
    }
    return this;
  }

  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    //todo implement me!
    return this;
  }

  public boolean isReferenceTo(PsiElement element) {
    return resolve() == element;
  }

  public Object[] getVariants() {
    return CompleteSymbol.getVariants(this);
  }

  public boolean isSoft() {
    return false;
  }

  @NotNull
  public String getNameString() {
    return getText();
  }
}
