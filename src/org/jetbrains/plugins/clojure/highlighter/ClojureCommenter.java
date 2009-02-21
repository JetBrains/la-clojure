package org.jetbrains.plugins.clojure.highlighter;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiComment;
import com.intellij.lang.CodeDocumentationAwareCommenter;

/**
 * @author ilyas
 */
public class ClojureCommenter implements CodeDocumentationAwareCommenter, ClojureTokenTypes {
  public String getLineCommentPrefix() {
    return ";";
  }

  public String getBlockCommentPrefix() {
    return null;
  }

  public String getBlockCommentSuffix() {
    return null;
  }

  @Nullable
  public IElementType getLineCommentTokenType() {
    return LINE_COMMENT;
  }

  @Nullable
  public IElementType getBlockCommentTokenType() {
    return null;
  }

  @Nullable
  public IElementType getDocumentationCommentTokenType() {
    return null;
  }

  @Nullable
  public String getDocumentationCommentPrefix() {
    return null;
  }

  @Nullable
  public String getDocumentationCommentLinePrefix() {
    return null;
  }

  @Nullable
  public String getDocumentationCommentSuffix() {
    return null;
  }

  public boolean isDocumentationComment(PsiComment element) {
    return false;
  }
  

}
