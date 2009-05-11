package org.jetbrains.plugins.clojure.psi.impl.symbols;

import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import com.intellij.psi.util.MethodSignature;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.HashSet;
import com.intellij.util.containers.HashMap;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiElementFactoryImpl;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiElementFactory;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;
import org.jetbrains.plugins.clojure.psi.resolve.processors.SymbolResolveProcessor;
import org.jetbrains.plugins.clojure.psi.resolve.processors.ResolveProcessor;
import org.jetbrains.plugins.clojure.psi.resolve.ClojureResolveResult;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.clojure.psi.resolve.ClojureResolveResultImpl;
import org.jetbrains.plugins.clojure.psi.resolve.completion.CompletionProcessor;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.plugins.clojure.lexer.TokenSets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
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

  public PsiElement getElement() {
    return this;
  }

  public TextRange getRangeInElement() {
    final PsiElement refNameElement = getReferenceNameElement();
    if (refNameElement != null) {
      final int offsetInParent = refNameElement.getStartOffsetInParent();
      return new TextRange(offsetInParent, offsetInParent + refNameElement.getTextLength());
    }
    return new TextRange(0, getTextLength());
  }

  @Nullable
  public PsiElement getReferenceNameElement() {
    final ASTNode lastChild = getNode().getLastChildNode();
    if (lastChild == null) return null;
    for (IElementType elementType : TokenSets.REFERENCE_NAMES.getTypes()) {
      if (lastChild.getElementType() == elementType) return lastChild.getPsi();
    }

    return null;
  }

  @Nullable
  public String getReferenceName() {
    PsiElement nameElement = getReferenceNameElement();
    if (nameElement != null) {
      if (nameElement.getNode().getElementType() == ClojureTokenTypes.symATOM)
        return nameElement.getText();
    }
    return null;
  }

  @NotNull
  public ResolveResult[] multiResolve(boolean incomplete) {
    return getManager().getResolveCache().resolveWithCaching(this, RESOLVER, true, incomplete);
  }

  public PsiElement setName(@NotNull @NonNls String newName) throws IncorrectOperationException {
    final ASTNode newNode = ClojurePsiElementFactory.getInstance(getProject()).createSymbolNodeFromText(newName);
    getParent().getNode().replaceChild(getNode(), newNode);
    return newNode.getPsi();
  }

  private static class MyResolver implements ResolveCache.PolyVariantResolver<ClSymbol> {
    public ResolveResult[] resolve(ClSymbol symbol, boolean incompleteCode) {
      final String name = symbol.getReferenceName();
      if (name == null) return null;

      if (ClojurePsiUtil.isDefinitionSymbol(symbol)) return null;

      // Resolve Java methods invocations
      ClSymbol qualifier = symbol.getQualifierSymbol();
      if (qualifier == null && symbol.getNameString().startsWith(".")) {
        return resolveJavaMethodReference(symbol);
      }

      ResolveProcessor processor = new SymbolResolveProcessor(name, symbol, incompleteCode);

      resolveImpl(symbol, processor);

      ClojureResolveResult[] candidates = processor.getCandidates();
      if (candidates.length > 0) return candidates;

      return ClojureResolveResult.EMPTY_ARRAY;
    }

    private ResolveResult[] resolveJavaMethodReference(final ClSymbol symbol) {
      final CompletionProcessor processor = new CompletionProcessor(symbol);
      ResolveUtil.treeWalkUp(symbol, processor);
      final String name = symbol.getReferenceName();
      assert name != null;

      final String originalName = StringUtil.trimStart(name, ".");
      final PsiElement[] elements = ResolveUtil.mapToElements(processor.getCandidates());
      final HashMap<MethodSignature, HashSet<PsiMethod>> sig2Method = CompleteSymbol.collectAvailableMethods(elements);
      final List<MethodSignature> goodSignatures = ContainerUtil.findAll(sig2Method.keySet(), new Condition<MethodSignature>() {
        public boolean value(MethodSignature methodSignature) {
          return originalName.equals(methodSignature.getName());
        }
      });

      final HashSet<ClojureResolveResult> results = new HashSet<ClojureResolveResult>();
      for (MethodSignature signature : goodSignatures) {
        final HashSet<PsiMethod> methodSet = sig2Method.get(signature);
        for (PsiMethod method : methodSet) {
          results.add(new ClojureResolveResultImpl(method, true));
        }
      }

      return results.toArray(new ClojureResolveResult[results.size()]);
    }

    private void resolveImpl(ClSymbol symbol, ResolveProcessor processor) {
      final ClSymbol qualifier = symbol.getQualifierSymbol();
      if (qualifier == null) {
        ResolveUtil.treeWalkUp(symbol, processor);
      } else {
        for (ResolveResult result : qualifier.multiResolve(false)) {
          final PsiElement element = result.getElement();
          if (element != null) {
            element.processDeclarations(processor, ResolveState.initial(), null, symbol);
          }
        }
      }
    }
  }

  public ClSymbol getQualifierSymbol() {
    return findChildByClass(ClSymbol.class);
  }

  public boolean isQualified() {
    return getQualifierSymbol() != null;
  }

  @Override
  public String getName() {
    return getNameString();
  }

  @Nullable
  public PsiElement getSeparatorToken() {
    return findChildByType(TokenSets.DOTS);
  }

  private static final MyResolver RESOLVER = new MyResolver();

  public PsiElement resolve() {
    ResolveResult[] results = getManager().getResolveCache().resolveWithCaching(this, RESOLVER, false, false);
    return results.length == 1 ? results[0].getElement() : null;
  }

  public String getCanonicalText() {
    return null;
  }

  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    PsiElement nameElement = getReferenceNameElement();
    if (nameElement != null) {
      ASTNode node = nameElement.getNode();
      ASTNode newNameNode = ClojurePsiElementFactory.getInstance(getProject()).createSymbolNodeFromText(newElementName);
      assert newNameNode != null && node != null;
      node.getTreeParent().replaceChild(node, newNameNode);
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

  @Override
  public ItemPresentation getPresentation() {
    return new ItemPresentation() {
      public String getPresentableText() {
        final String name = getName();
        return name == null ? "" : name;
      }

      @Nullable
      public String getLocationString() {
        String name = getContainingFile().getName();
        return "(in " + name + ")";
      }

      @Nullable
      public Icon getIcon(boolean open) {
        return SymbolUtils.getIcon(ClSymbolImpl.this, Iconable.ICON_FLAG_VISIBILITY | Iconable.ICON_FLAG_READ_STATUS);
      }

      @Nullable
      public TextAttributesKey getTextAttributesKey() {
        return null;
      }
    };
  }

}
