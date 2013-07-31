package org.jetbrains.plugins.clojure.editor.selection;

import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.editor.Editor;

import java.util.List;

import org.jetbrains.plugins.clojure.psi.api.ClLiteral;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;

/**
 * @author ilyas
 */
public class ClojureLiteralSelectioner extends ClojureBasicSelectioner {
  public boolean canSelect(PsiElement e) {
    PsiElement parent = e.getParent();
    return isStringLiteral(e) || isStringLiteral(parent);
  }

  private static boolean isStringLiteral(PsiElement element) {
    if (!(element instanceof ClLiteral)) return false;
    ASTNode node = element.getNode();
    if (node == null) return false;
    ASTNode[] children = node.getChildren(null);
    return children.length == 1 && (children[0].getElementType() == ClojureTokenTypes.STRING_LITERAL);
  }

  public List<TextRange> select(PsiElement e, CharSequence editorText, int cursorOffset, Editor editor) {
    List<TextRange> result = super.select(e, editorText, cursorOffset, editor);

    TextRange range = e.getTextRange();
    if (range.getLength() <= 2) {
      result.add(range);
    } else {
      result.add(new TextRange(range.getStartOffset() + 1, range.getEndOffset() - 1));
    }
    return result;
  }
}