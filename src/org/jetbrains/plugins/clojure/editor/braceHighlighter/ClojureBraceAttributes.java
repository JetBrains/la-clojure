package org.jetbrains.plugins.clojure.editor.braceHighlighter;

import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.*;

/**
 * @author ilyas
 */
public abstract class ClojureBraceAttributes {
  public static TextAttributes[] CLOJURE_BRACE_ATTRIBUTES =
      {
          new TextAttributes(Color.BLUE, null, null, null, 1), // 0
          new TextAttributes(new Color(139, 0, 0), null, null, null, 1), // 10 red
          new TextAttributes(new Color(47, 79, 47), null, null, null, 1),     // 1
          new TextAttributes(new Color(199, 21, 133), null, null, null, 1), // 7 MediumVioletRed
          new TextAttributes(new Color(85, 26, 139), null, null, null, 1), // 2 purple
          new TextAttributes(Color.DARK_GRAY, null, null, null, 1),   // 3
          new TextAttributes(new Color(0, 0, 128), null, null, null, 1), // 8 navy - blue
          new TextAttributes(Color.RED, null, null, null, 1),             // 5
          new TextAttributes(new Color(47, 79, 47), null, null, null, 1), // 6 Dark green
          new TextAttributes(new Color(255, 100, 0), null, null, null, 1),   // 1 orange
          new TextAttributes(new Color(139, 101, 8), null, null, null, 1), // 9 Dark golden

      };

}
