/*
 * Copyright 2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.plugins.clojure.settings;

import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 */
public final class ClojureProjectSettingsForm {

  private final ClojureProjectSettings mySettings;

  private JPanel myPanel;
  private JCheckBox rainbowParenthesesCheckBox;
  private JPanel myAppearancePanel;

  public ClojureProjectSettingsForm(Project project) {
    mySettings = ClojureProjectSettings.getInstance(project);
  }

  JComponent getComponent() {
    return myPanel;
  }

  boolean isColoredParentheses() {
    return rainbowParenthesesCheckBox.isSelected();
  }

  boolean isModified() {
    final boolean coloredParentheses = mySettings.coloredParentheses;
    return (rainbowParenthesesCheckBox.isSelected() != coloredParentheses);
  }

  void reset() {
    rainbowParenthesesCheckBox.setSelected(mySettings.coloredParentheses);
  }

}
