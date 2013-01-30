package org.jetbrains.plugins.clojure.formatter.processors;

import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.plugins.clojure.formatter.ClojureBlock;
import org.jetbrains.plugins.clojure.parser.ClojureElementTypes;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;

/**
 * @author ilyas
 */
public class ClojureIndentProcessor implements ClojureElementTypes{
  public static Indent getChildIndent(ClojureBlock parent, ASTNode prevChildNode, ASTNode child) {
    ASTNode astNode = parent.getNode();
    final PsiElement psiParent = astNode.getPsi();

    // For Groovy file
    if (psiParent instanceof ClojureFile) {
      return Indent.getNoneIndent();
    }

    ASTNode node = parent.getNode();
    final TokenSet L_BRACES = TokenSet.create(LEFT_CURLY, LEFT_PAREN, LEFT_SQUARE);
    if (LIST_LIKE_FORMS.contains(node.getElementType())) {
      if (L_BRACES.contains(child.getElementType())) {
        return Indent.getNoneIndent();
      } else {
        return Indent.getNormalIndent(true);
      }
    } 
    return Indent.getNoneIndent();
  }
}
