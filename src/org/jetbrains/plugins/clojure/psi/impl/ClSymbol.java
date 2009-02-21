package org.jetbrains.plugins.clojure.psi.impl;

import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.IncorrectOperationException;

/**
 * @author ilyas
*/
public class ClSymbol extends ClojurePsiElementImpl {
  public ClSymbol(ASTNode node) {
    super(node);
  }

  public PsiElement getDefinition() {
    return getDefinition(getNode().getText());
  }

  class Reference implements PsiReference {

    ClSymbol element;

    Reference(ClSymbol sym) {
      element = sym;
    }

    /**
     * Returns the underlying (referencing) element of the reference.
     *
     * @return the underlying element of the reference.
     */
    public PsiElement getElement() {
      return element;
    }

    /**
     * Returns the part of the underlying element which serves as a reference, or the complete
     * text range of the element if the entire element is a reference.
     *
     * @return Relative range in element
     */
    public TextRange getRangeInElement() {
      return element.getTextRange();
    }

    /**
     * Returns the element which is the target of the reference.
     *
     * @return the target element, or null if it was not possible to resolve the reference to a valid target.
     */
    @Nullable
    public PsiElement resolve() {
      return element.getDefinition();
    }

    /**
     * Returns the name of the reference target element which does not depend on import statements
     * and other context (for example, the full-qualified name of the class if the reference targets
     * a Java class).
     *
     * @return the canonical text of the reference.
     */
    public String getCanonicalText() {
      return element.getText();
    }

    /**
     * Called when the reference target element has been renamed, in order to change the reference
     * text according to the new name.
     *
     * @param newElementName the new name of the target element.
     * @return the new underlying element of the reference.
     * @throws com.intellij.util.IncorrectOperationException
     *          if the rename cannot be handled for some reason.
     */
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
      return null;
    }

    /**
     * Changes the reference so that it starts to point to the specified element. This is called,
     * for example, by the "Create Class from New" quickfix, to bind the (invalid) reference on
     * which the quickfix was called to the newly created class.
     *
     * @param element the element which should become the target of the reference.
     * @return the new underlying element of the reference.
     * @throws com.intellij.util.IncorrectOperationException if the rebind cannot be handled for some reason.
     */
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
      return null;
    }

    /**
     * Checks if the reference targets the specified element.
     *
     * @param target the element to check target for.
     * @return true if the reference targets that element, false otherwise.
     */
    public boolean isReferenceTo(PsiElement target) {
      return element instanceof ClSymbol && target.getText().equals(element.getText()) && ((ClSymbol) element).getDefinition() == this.element;
    }

    /**
     * Returns the array of String, {@link com.intellij.psi.PsiElement} and/or {@link com.intellij.codeInsight.lookup.LookupElement}
     * instances representing all identifiers that are visible at the location of the reference. The contents
     * of the returned array is used to build the lookup list for basic code completion. (The list
     * of visible identifiers may not be filtered by the completion prefix string - the
     * filtering is performed later by IDEA core.)
     *
     * @return the array of available identifiers.
     */
    public Object[] getVariants() {
      return EMPTY_ARRAY;
    }

    /**
     * Returns false if the underlying element is guaranteed to be a reference, or true
     * if the underlying element is a possible reference which should not be reported as
     * an error if it fails to resolve. For example, a text in an XML file which looks
     * like a full-qualified Java class name is a soft reference.
     *
     * @return true if the refence is soft, false otherwise.
     */
    public boolean isSoft() {
      return true;
    }

  }

  /**
   * Returns the reference associated with this PSI element. If the element has multiple
   * associated references (see {@link #getReferences()} for an example), returns the first
   * associated reference.
   *
   * @return the reference instance, or null if the PSI element does not have any
   *         associated references.
   */
  @Nullable
  public PsiReference getReference() {
    return new Reference(this);
  }

  /**
   * Returns all references associated with this PSI element. An element can be associated
   * with multiple references when, for example, the element is a string literal containing
   * multiple sub-strings which are valid full-qualified class names. If an element
   * contains only one text fragment which acts as a reference but the reference has
   * multiple possible targets, {@link com.intellij.psi.PsiPolyVariantReference} should be used instead
   * of returning multiple references.
   *
   * @return the array of references, or an empty array if the element has no associated
   *         references.
   */
  @NotNull
  public PsiReference[] getReferences() {
    return new PsiReference[]{getReference()};
  }
}
