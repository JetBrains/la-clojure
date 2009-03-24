package org.jetbrains.plugins.clojure.psi.impl.defs;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.impl.list.ClListBaseImpl;
import org.jetbrains.plugins.clojure.psi.stubs.ClDefStub;

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
    return org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil.findNextSiblingByClass(first, ClSymbol.class);
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
        return ClDefImpl.this.getIcon(Iconable.ICON_FLAG_VISIBILITY | Iconable.ICON_FLAG_READ_STATUS);
      }

      @Nullable
      public TextAttributesKey getTextAttributesKey() {
        return null;
      }
    };
  }

  @Override
  public Icon getIcon(int flags) {
    return ClojureIcons.FUNCTION;
  }

  public PsiElement setName(@NotNull @NonNls String name) throws IncorrectOperationException {
    return null;
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
