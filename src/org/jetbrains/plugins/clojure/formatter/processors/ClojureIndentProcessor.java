package org.jetbrains.plugins.clojure.formatter.processors;

import org.jetbrains.plugins.clojure.formatter.ClojureBlock;
import org.jetbrains.plugins.clojure.file.ClojureFile;
import org.jetbrains.plugins.clojure.parser.ClojureElementTypes;
import com.intellij.lang.ASTNode;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiElement;

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
    if (LIST_LIKE_FORMS.contains(node.getElementType())) {
      if (BRACES.contains(child.getElementType())) {
        return Indent.getNoneIndent();
      } else {
        return Indent.getNormalIndent();
      }
    }
    return Indent.getNoneIndent();
  }
}
