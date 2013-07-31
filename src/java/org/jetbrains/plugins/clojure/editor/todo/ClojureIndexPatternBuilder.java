package org.jetbrains.plugins.clojure.editor.todo;

import com.intellij.lexer.Lexer;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.search.IndexPatternBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.plugins.clojure.lexer.ClojureFlexLexer;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;

/**
 * @author ilyas
 */
public class ClojureIndexPatternBuilder implements IndexPatternBuilder {
  public Lexer getIndexingLexer(PsiFile file) {
    if (file instanceof ClojureFile) {
      return new ClojureFlexLexer();
    }
    return null;
  }

  public TokenSet getCommentTokenSet(PsiFile file) {
    if (file instanceof ClojureFile) {
      return ClojureTokenTypes.COMMENTS;
    }
    return null;
  }

  public int getCommentStartDelta(IElementType tokenType) {
    return 0;
  }

  public int getCommentEndDelta(IElementType tokenType) {
    return 0;
  }
}
