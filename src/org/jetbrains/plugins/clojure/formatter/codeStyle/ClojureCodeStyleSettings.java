package org.jetbrains.plugins.clojure.formatter.codeStyle;

import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NonNls;

/**
 * @author ilyas
 */
public class ClojureCodeStyleSettings extends CustomCodeStyleSettings{

  public boolean ALIGN_CLOJURE_FORMS = false;

  protected ClojureCodeStyleSettings(CodeStyleSettings container) {
    super("ClojureCodeStyleSettings", container);
  }
}
