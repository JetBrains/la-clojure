package org.jetbrains.plugins.clojure.psi.impl.defs;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDefMethod;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClDefStub;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author ilyas
*/
public class ClDefnMethodImpl extends ClDefImpl implements ClDefMethod {

  public ClDefnMethodImpl(ClDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public ClDefnMethodImpl(ASTNode node) {
    super(node);
  }

  protected String getPrefix() {
    return "defmethod";
  }

  @Override
  public String toString() {
    return "ClDefn";
  }

  @Override
  public Icon getIcon(int flags) {
    return ClojureIcons.METHOD;
  }

  public String getPresentationText() {
    final StringBuffer buffer = new StringBuffer();
    final String name = getName();
    if (name == null) return "<undefined>";
    buffer.append(name).append(" ");
    buffer.append(getMethodInfo()).append(" ");
    buffer.append(getParameterString());

    return buffer.toString();
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
        return "(in " + name + ")";
      }

      @Nullable
      public Icon getIcon(boolean open) {
        return ClDefnMethodImpl.this.getIcon(Iconable.ICON_FLAG_VISIBILITY | Iconable.ICON_FLAG_READ_STATUS);
      }

      @Nullable
      public TextAttributesKey getTextAttributesKey() {
        return null;
      }
    };
  }
}
