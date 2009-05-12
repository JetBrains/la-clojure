package org.jetbrains.plugins.clojure.structure;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNamedElement;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.navigation.NavigationItem;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Iconable;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ilyas
 */
public class ClojureStructureViewElement implements StructureViewTreeElement {
  private PsiElement myElement;

  public ClojureStructureViewElement(PsiElement element) {
    myElement = element;
  }

  public PsiElement getValue() {
    return myElement;
  }

  public void navigate(boolean requestFocus) {
    ((NavigationItem) myElement).navigate(requestFocus);
  }

  public boolean canNavigate() {
    return ((NavigationItem) myElement).canNavigate();
  }

  public boolean canNavigateToSource() {
    return ((NavigationItem) myElement).canNavigateToSource();
  }

  public StructureViewTreeElement[] getChildren() {
    final List<ClojurePsiElement> childrenElements = new ArrayList<ClojurePsiElement>();
    myElement.acceptChildren(new PsiElementVisitor() {
      public void visitElement(PsiElement element) {
        if (isBrowsableElement(element)) {
          childrenElements.add((ClojurePsiElement) element);
        } else {
          element.acceptChildren(this);
        }
      }
    });

    StructureViewTreeElement[] children = new StructureViewTreeElement[childrenElements.size()];
    for (int i = 0; i < children.length; i++) {
      children[i] = new ClojureStructureViewElement(childrenElements.get(i));
    }

    return children;
  }

  private boolean isBrowsableElement(PsiElement element) {
    return element instanceof ClDef &&
            ((ClDef) element).getNameSymbol() != null;
  }

  public ItemPresentation getPresentation() {
    return new ItemPresentation() {
      public String getPresentableText() {
        if (myElement instanceof ClDef) {
          return ((ClDef) myElement).getPresentationText();
        }
        return ((PsiNamedElement) myElement).getName();
      }

      public TextAttributesKey getTextAttributesKey() {
        return null;
      }

      public String getLocationString() {
        return null;
      }

      public Icon getIcon(boolean open) {
        return myElement.getIcon(Iconable.ICON_FLAG_OPEN);
      }
    };
  }
}