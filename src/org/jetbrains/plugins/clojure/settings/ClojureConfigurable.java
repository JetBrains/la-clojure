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

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Project specific settings.
 * <p>
 * This is the extension implementation.
 *
 * @see ClojureProjectSettings
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 */
public final class ClojureConfigurable extends AbstractProjectComponent implements Configurable {

  private ClojureProjectSettingsForm mySettingsForm;

  public ClojureConfigurable(Project project) {
    super(project);
  }

  // Configurable =============================================================

  @Nls
  public String getDisplayName() {
    return "Clojure";
  }

  public Icon getIcon() {
    return null;
  }

  public String getHelpTopic() {
    return null;
  }

  public JComponent createComponent() {
    if (mySettingsForm == null) {
      mySettingsForm = new ClojureProjectSettingsForm(myProject);
    }
    return mySettingsForm.getComponent();
  }

  public boolean isModified() {
    return mySettingsForm.isModified();
  }

  public void apply() throws ConfigurationException {
    ClojureProjectSettings settings = ClojureProjectSettings.getInstance(myProject);
    settings.commandLineArgs = mySettingsForm.getCommandLineArguments();
    settings.autoStartRepl = mySettingsForm.isAutoStartRepl();
    settings.coloredParentheses = mySettingsForm.isColoredParentheses();
  }

  public void reset() {
    if (mySettingsForm != null) {
      mySettingsForm.reset();
    }
  }

  public void disposeUIResources() {
    mySettingsForm = null;
  }

  // ProjectComponent =========================================================

  @NotNull
  public String getComponentName() {
    return "ClojureProjectSettings";
  }

}