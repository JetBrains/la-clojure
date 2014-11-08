package org.jetbrains.plugins.clojure.editor.braceHighlighter;

import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.*;

/**
 * @author ilyas
 */
public abstract class ClojureBraceAttributes {
  private static final Color[] CLOJURE_BRACE_COLORS =
      {
          new Color(160, 160, 160), // gray
          new Color(255, 160,   0), // orange
          new Color(255, 255,   0), // yellow
          new Color(160, 255,  62), // green
          new Color( 32, 255, 224), // cyan
          new Color( 64, 160, 255), // blue
          new Color(160, 128, 255), // magenta
          new Color(224, 128, 192), // pink
      };

  public static TextAttributes getBraceAttributes(int level, Color background) {
    Color braceColor = CLOJURE_BRACE_COLORS[level % CLOJURE_BRACE_COLORS.length];
    Color adjustedBraceColor = braceColor; // TODO make it preserve the original colors for Darcula: new Color(braceColor.getRGB() ^ background.getRGB() ^ 0xFFFFFF);
    return new TextAttributes(adjustedBraceColor, null, null, null, 1);
  }
}
