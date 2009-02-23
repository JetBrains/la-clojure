package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClSet;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
public class ClSetImpl extends ClojurePsiElementImpl implements ClSet {

  public ClSetImpl(ASTNode node) {
    super(node, "ClSet");
  }

}
