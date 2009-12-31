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

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 */
public final class ClojureProjectSettingsForm {

  private final ClojureProjectSettings mySettings;

  private JPanel myPanel;
  private JTextField myCommandLineArgs;
  private JCheckBox myAutoStartRepl;

  public ClojureProjectSettingsForm(Project project) {
    mySettings = ClojureProjectSettings.getInstance(project);
  }

  JComponent getComponent() {
    return myPanel;
  }

  String getCommandLineArguments() {
    return myCommandLineArgs.getText();
  }

  boolean isAutoStartRepl() {
    return myAutoStartRepl.isSelected();
  }

  boolean isModified() {
    final String args = mySettings.commandLineArgs;
    return (args == null || args.length() == 0)
        || myAutoStartRepl.isSelected();
  }

  void reset() {
    myCommandLineArgs.setText(mySettings.commandLineArgs);
    myAutoStartRepl.setSelected(mySettings.autoStartRepl);
  }

}
