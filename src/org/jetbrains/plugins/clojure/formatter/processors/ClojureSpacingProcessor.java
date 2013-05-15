package org.jetbrains.plugins.clojure.formatter.processors;

import com.intellij.formatting.Block;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.plugins.clojure.formatter.ClojureBlock;
import org.jetbrains.plugins.clojure.lexer.TokenSets;
import org.jetbrains.plugins.clojure.parser.ClojureElementTypes;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;
import org.jetbrains.plugins.clojure.psi.api.ClListLike;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiCheckers;

/**
 * @author ilyas
 */
public class ClojureSpacingProcessor implements ClojureElementTypes {

  private static final Spacing NO_SPACING = Spacing.createSpacing(0, 0, 0, false, 0);
  private static final Spacing NO_SPACING_WITH_NEWLINE = Spacing.createSpacing(0, 0, 0, true, 1);
  private static final Spacing MANDATORY_NEWLINE = Spacing.createSpacing(1, 1, 1, true, 100);
  private static final Spacing NS_SPACING = Spacing.createSpacing(1, 1, 2, true, 100);
  private static final Spacing COMMON_SPACING = Spacing.createSpacing(1, 1, 0, true, 100);
  private static final Spacing NO_NEWLINE = Spacing.createSpacing(1, 1, 0, false, 0);

  public static Spacing getSpacing(Block child1, Block child2) {
    if (!(child1 instanceof ClojureBlock) || !(child2 instanceof ClojureBlock)) return null;
    ClojureBlock block1 = (ClojureBlock) child1;
    ClojureBlock block2 = (ClojureBlock) child2;

    ASTNode node1 = block1.getNode();
    ASTNode node2 = block2.getNode();

    IElementType type1 = node1.getElementType();
    IElementType type2 = node2.getElementType();

    final Spacing psiBased = psiBasedSpacing(node1.getPsi(), node2.getPsi());
    if (psiBased != null) {
      return psiBased;
    }

    if (MODIFIERS.contains(type1)) {
      return NO_SPACING;
    }

    if (ClojureTokenTypes.ATOMS.contains(type2)) {
      return NO_SPACING;
    }

    String text1 = node1.getText();
    String text2 = node2.getText();

    if (text1.trim().startsWith(",") || text2.trim().startsWith(",")) {
      return null;
    }

    if (BRACES.contains(type1) || BRACES.contains(type2)) {
      return NO_SPACING_WITH_NEWLINE;
    }

    return COMMON_SPACING;
  }

  private static Spacing psiBasedSpacing(PsiElement psi1, PsiElement psi2) {
    final IElementType rightElementType = psi2.getNode().getElementType();
    // Namespace declaration
    if (ClojurePsiCheckers.isNs(psi1)) {
      return NS_SPACING;
    }

    if (ClojurePsiCheckers.isImportingClause(psi2)) {
      return MANDATORY_NEWLINE;
    }

    // todo questionable: should be adjustable
    if (psi1 instanceof ClKeyword) {
      if (TokenSets.RIGHT_PARENTHESES.contains(rightElementType)) return null;
      return NO_NEWLINE;
    }

    // formatting imports
    if (psi1 instanceof ClListLike &&
        psi2 instanceof ClListLike &&
        psi1.getParent() == psi2.getParent() &&
        ClojurePsiCheckers.isImportingClause(psi1.getParent())) {
      return MANDATORY_NEWLINE;
    }

    // todo add more cases

    return null;
  }

}
