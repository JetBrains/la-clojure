package org.jetbrains.plugins.clojure.formatter.codeStyle;

import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
public class ClojureCodeStyleSettingsProvider extends CodeStyleSettingsProvider{
  @NotNull
  public Configurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
    return new ClojureFormatConfigurable(settings, originalSettings);
  }

  @Override
  public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
    return new ClojureCodeStyleSettings(settings);
  }

}
