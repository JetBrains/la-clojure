package org.jetbrains.plugins.clojure.editor;

import com.intellij.codeInsight.editorActions.QuoteHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;

/**
 * @author ilyas
 */
public class ClojureQuoteHandler implements QuoteHandler {

  public boolean isClosingQuote(HighlighterIterator iterator, int offset) {
    final IElementType tokenType = iterator.getTokenType();

    if (tokenType == ClojureTokenTypes.STRING_LITERAL) {
      int start = iterator.getStart();
      int end = iterator.getEnd();
      return end - start >= 1 && offset == end - 1;
    }
    return false;
  }

  public boolean isOpeningQuote(HighlighterIterator iterator, int offset) {
    final IElementType tokenType = iterator.getTokenType();

    if (tokenType == ClojureTokenTypes.WRONG_STRING_LITERAL ||
        tokenType == ClojureTokenTypes.STRING_LITERAL) {
      int start = iterator.getStart();
      return offset == start;
    }
    return false;
  }

  public boolean hasNonClosedLiteral(Editor editor, HighlighterIterator iterator, int offset) {
    return true;
  }

  public boolean isInsideLiteral(HighlighterIterator iterator) {
    final IElementType tokenType = iterator.getTokenType();
    return tokenType == ClojureTokenTypes.STRING_LITERAL;
  }
}
