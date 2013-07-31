package org.jetbrains.plugins.clojure.psi;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.PsiComment;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.psi.api.ClQuotedForm;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

import java.util.List;

/**
 * @author ilyas
 */
public abstract class ClojureBaseElementImpl <T extends StubElement> extends StubBasedPsiElementBase<T> implements ClojurePsiElement {

  protected boolean isWrongElement(PsiElement element) {
    return element == null ||
        (element instanceof LeafPsiElement || element instanceof PsiWhiteSpace || element instanceof PsiComment);
  }

  public PsiElement getFirstNonLeafElement() {
    PsiElement first = getFirstChild();
    while (first != null && isWrongElement(first)) {
      first = first.getNextSibling();
    }
    return first;
  }

  public PsiElement getNonLeafElement(int k) {
    final List<PsiElement> elements = ContainerUtil.filter(getChildren(), new Condition<PsiElement>() {
      public boolean value(PsiElement psiElement) {
        return !isWrongElement(psiElement);
      }
    });
    if (k - 1 >= elements.size()) return  null;
    return elements.get(k-1);
  }

  public PsiElement getLastNonLeafElement() {
    PsiElement lastChild = getLastChild();
    while (lastChild != null && isWrongElement(lastChild)) {
      lastChild = lastChild.getPrevSibling();
    }
    return lastChild;
  }

  public <T> T findFirstChildByClass(Class<T> aClass) {
    PsiElement element = getFirstChild();
    while (element != null && !aClass.isInstance(element)) {
      element = element.getNextSibling();
    }
    return (T)element;
  }

  public ClojureBaseElementImpl(T stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public ClojureBaseElementImpl(ASTNode node) {
    super(node);
  }

  protected void commitDocument() {
    final Project project = getProject();
    final Document document = PsiDocumentManager.getInstance(project).getDocument(getContainingFile());
    if (document != null) {
      PsiDocumentManager.getInstance(project).commitDocument(document);
    }
  }

  public ClSymbol[] getAllSymbols() {
    return findChildrenByClass(ClSymbol.class);
  }

  public ClSymbol[] getAllQuotedSymbols() {
    final ClQuotedForm[] quoteds = findChildrenByClass(ClQuotedForm.class);
    final List<ClQuotedForm> quotedSymbols = ContainerUtil.filter(quoteds, new Condition<ClQuotedForm>() {
      public boolean value(ClQuotedForm clQuotedForm) {
        final ClojurePsiElement element = clQuotedForm.getQuotedElement();
        return element instanceof ClSymbol;
      }
    });

    return ContainerUtil.map(quotedSymbols, new Function<ClQuotedForm, Object>() {
      public Object fun(ClQuotedForm clQuotedForm) {
        return ((ClSymbol) clQuotedForm.getQuotedElement());
      }
    }).toArray(new ClSymbol[0]);
  }
  
}
