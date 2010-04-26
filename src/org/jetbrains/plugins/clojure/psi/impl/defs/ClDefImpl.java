package org.jetbrains.plugins.clojure.psi.impl.defs;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClLiteral;
import org.jetbrains.plugins.clojure.psi.api.ClMetadata;
import org.jetbrains.plugins.clojure.psi.api.ClVector;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.impl.list.ClListBaseImpl;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClDefStub;

import javax.swing.*;

/**
 * @author ilyas
 */
public class ClDefImpl extends ClListBaseImpl<ClDefStub> implements ClDef, StubBasedPsiElement<ClDefStub> {
  public ClDefImpl(ClDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public ClDefImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClDef";
  }

  /**
   * @return Name of string symbol defined
   */
  @Nullable
  public ClSymbol getNameSymbol() {
    final ClSymbol first = findChildByClass(ClSymbol.class);
    if (first == null) return null;
    return PsiTreeUtil.getNextSiblingOfType(first, ClSymbol.class);
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

  @Override
  @Nullable
  public String getName() {
    return getDefinedName();
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
    // Do not resolve identifier
    if (lastParent != null && lastParent.getParent() == this && lastParent instanceof ClSymbol) return true;
    //process parameters
    if (lastParent != null && lastParent.getParent() == this) {
      final ClVector paramVector = findChildByClass(ClVector.class);
      if (paramVector != null) {
        for (ClSymbol symbol : paramVector.getAllSymbols()) {
          if (!ResolveUtil.processElement(processor, symbol)) return false;
        }
      }
      // for recursive functions
      if (getNameSymbol() != null && lastParent != getNameSymbol() && !ResolveUtil.processElement(processor, getNameSymbol())) return false;

      // overloaded function
      else if (lastParent instanceof ClList) {
        ClList list = (ClList) lastParent;
        final ClVector params = list.findFirstChildByClass(ClVector.class);
        if (params != null) {
          for (ClSymbol symbol : params.getAllSymbols()) {
            if (!ResolveUtil.processElement(processor, symbol)) return false;
          }
        }
      }

      return true;
    } else {
      return ResolveUtil.processElement(processor, this);
    }
  }

  @Override
  public ItemPresentation getPresentation() {
    return new ItemPresentation() {
      public String getPresentableText() {
        return getPresentationText();
      }

      @Nullable
      public String getLocationString() {
        String name = getContainingFile().getName();
        //todo show namespace
        return "(in " + name + ")";
      }

      @Nullable
      public Icon getIcon(boolean open) {
        return ClDefImpl.this.getIcon(Iconable.ICON_FLAG_VISIBILITY | Iconable.ICON_FLAG_READ_STATUS);
      }

      @Nullable
      public TextAttributesKey getTextAttributesKey() {
        return null;
      }
    };
  }

  public String getPresentationText() {
    final StringBuffer buffer = new StringBuffer();
    final String name = getName();
    if (name == null) return "<undefined>";
    buffer.append(name).append(" ");
    buffer.append(getParameterString());

    return buffer.toString();
  }

  public String getDocString() {
    PsiElement element = getSecondNonLeafElement();
    if (element == null) return null;
    element = element.getNextSibling();
    while (element != null && isWrongElement(element)) {
      element = element.getNextSibling();
    }
    // For doc String
    final String s = processString(element);
    if (s != null) return s;

    final ClMetadata meta = getMeta();
    if (meta == null) return null;
    final ClojurePsiElement value = meta.getValue("doc");
    return processString(value);
  }

  private String processString(PsiElement element) {
    if (element instanceof ClLiteral && element.getFirstChild().getNode().getElementType() == ClojureTokenTypes.STRING_LITERAL) {
      final String rawText = element.getText();
      return StringUtil.trimStart(StringUtil.trimEnd(rawText, "\""), "\"");
    }
    return null;
  }


  @Override
  public Icon getIcon(int flags) {
    return ClojureIcons.FUNCTION;
  }

  public PsiElement setName(@NotNull @NonNls String name) throws IncorrectOperationException {
    final ClSymbol sym = getNameSymbol();
    if (sym != null) sym.setName(name);
    return this;
  }

  @Override
  public int getTextOffset() {
    final ClSymbol symbol = getNameSymbol();
    if (symbol != null) {
      return symbol.getTextRange().getStartOffset();
    }
    return super.getTextOffset();
  }

  public String getParameterString() {
    final ClVector params = findChildByClass(ClVector.class);
    return params == null ? "" : params.getText();
  }

  public ClMetadata getMeta() {
    for (PsiElement element : getChildren()) {
      if (element instanceof ClMetadata) {
        return (ClMetadata) element;
      }
    }
    return null;
  }

  public String getMethodInfo() {
    final ClSymbol sym = getNameSymbol();
    if (sym == null) return "";
    PsiElement next = sym.getNextSibling();
    while (next instanceof LeafPsiElement) next = next.getNextSibling();
    return next == null ? "" : next.getText();
  }
}
