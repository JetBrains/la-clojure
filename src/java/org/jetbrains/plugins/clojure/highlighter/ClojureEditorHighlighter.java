package org.jetbrains.plugins.clojure.highlighter;

import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter;

/**
 * @author ilyas
 */
public class ClojureEditorHighlighter extends LexerEditorHighlighter{
  public ClojureEditorHighlighter(EditorColorsScheme scheme) {
    super(new ClojureSyntaxHighlighter(), scheme);
  }
}
