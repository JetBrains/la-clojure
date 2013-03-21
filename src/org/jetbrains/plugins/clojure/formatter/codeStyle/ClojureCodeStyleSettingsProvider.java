package org.jetbrains.plugins.clojure.formatter.codeStyle;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.lang.Language;
import com.intellij.openapi.options.Configurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

/**
 * @author ilyas
 */
public class ClojureCodeStyleSettingsProvider extends CodeStyleSettingsProvider{
  @NotNull
  public Configurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
    return new CodeStyleAbstractConfigurable(settings, originalSettings, ClojureBundle.message("title.clojure.code.style.settings")) {
      protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
        return new ClojureCodeStylePanel(settings);
      }

      public String getHelpTopic() {
        return "reference.settingsdialog.IDE.globalcodestyle.spaces";
      }
    };
  }

  @Nullable
  @Override
  public Language getLanguage() {
    return ClojureFileType.CLOJURE_LANGUAGE;
  }

  @Override
  public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
    return new ClojureCodeStyleSettings(settings);
  }

}
