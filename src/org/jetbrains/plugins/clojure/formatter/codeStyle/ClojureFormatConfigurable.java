package org.jetbrains.plugins.clojure.formatter.codeStyle;

import com.intellij.openapi.options.Configurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.ClojureIcons;

import javax.swing.*;

/**
 * @author ilyas
 */
public class ClojureFormatConfigurable extends CodeStyleAbstractConfigurable {
  public ClojureFormatConfigurable(CodeStyleSettings settings, CodeStyleSettings cloneSettings) {
    super(settings, cloneSettings, ClojureBundle.message("title.clojure.code.style.settings"));
  }

  protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
    return new ClojureCodeStylePanel(settings);
  }

  @Override
  public Icon getIcon() {
    return ClojureIcons.CLOJURE_ICON_16x16;
  }

  public String getHelpTopic() {
    return "reference.settingsdialog.IDE.globalcodestyle.spaces";
  }
}
