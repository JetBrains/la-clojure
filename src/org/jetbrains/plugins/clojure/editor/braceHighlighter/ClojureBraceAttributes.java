package org.jetbrains.plugins.clojure.editor.braceHighlighter;

import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.*;

/**
 * @author ilyas
 */
public abstract class ClojureBraceAttributes {
  private static final Color[] CLOJURE_BRACE_COLORS =
      {
          Color.BLUE, // 0
          new Color(139, 0, 0), // 10 red
          new Color(47, 79, 47),     // 1
          new Color(199, 21, 133), // 7 MediumVioletRed
          new Color(85, 26, 139), // 2 purple
          Color.DARK_GRAY,   // 3
          new Color(0, 0, 128), // 8 navy - blue
          Color.RED,             // 5
          new Color(47, 79, 47), // 6 Dark green
          new Color(255, 100, 0),   // 1 orange
          new Color(139, 101, 8), // 9 Dark golden

      };

  public static TextAttributes getBraceAttributes(int level, Color background) {
    Color braceColor = CLOJURE_BRACE_COLORS[level % CLOJURE_BRACE_COLORS.length];
    Color adjustedBraceColor = new Color(braceColor.getRGB() ^ background.getRGB() ^ 0xFFFFFF);
    return new TextAttributes(adjustedBraceColor, null, null, null, 1);
  }
}
