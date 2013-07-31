package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;

/**
 * @author ilyas
 */
public class ClMapEntryImpl extends ClojurePsiElementImpl implements ClMapEntry {
  public ClMapEntryImpl(@NotNull ASTNode astNode) {
    super(astNode, "ClMapEntry");
  }


  public ClKeyword getKeywordKey() {
    return findChildByClass(ClKeyword.class);
  }

  public ClojurePsiElement getKey() {
    final PsiElement child = getFirstChild();
    if (child instanceof ClojurePsiElement) {
      return (ClojurePsiElement) child;
    }
    return null;
  }

  public ClojurePsiElement getValue() {
    final PsiElement child = getLastChild();
    if (child instanceof ClojurePsiElement) {
      return (ClojurePsiElement) child;
    }
    return null;
  }
}
