package org.jetbrains.plugins.clojure.compiler;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.plugins.clojure.ClojureIcons;

import javax.swing.*;

/**
 * ilyas
 */
public class ClojureCompilerConfigurable implements Configurable {
  private JPanel myPanel;
  private JCheckBox myClojureBeforeCheckBox;
  private JCheckBox myCompileTaggedCb;
  private JCheckBox myCopySourcesCb;
  private ClojureCompilerSettings mySettings;
  private Project myProject;

  public ClojureCompilerConfigurable(ClojureCompilerSettings settings, Project project) {
    myProject = project;
    mySettings = settings;
  }

  @Nls
  public String getDisplayName() {
    return "Clojure Compiler";
  }

  public Icon getIcon() {
    return ClojureIcons.CLOJURE_ICON_16x16;
  }

  public String getHelpTopic() {
    return null;
  }

  public JComponent createComponent() {
    return myPanel;
  }

  public boolean isModified() {
    return mySettings.getState().CLOJURE_BEFORE != myClojureBeforeCheckBox.isSelected() ||
        mySettings.getState().COMPILE_CLOJURE != myCompileTaggedCb.isSelected() ||
        mySettings.getState().COPY_CLJ_SOURCES != myCopySourcesCb.isSelected();
  }

  public void apply() throws ConfigurationException {
    mySettings.getState().CLOJURE_BEFORE = myClojureBeforeCheckBox.isSelected();
    mySettings.getState().COMPILE_CLOJURE = myCompileTaggedCb.isSelected();
    mySettings.getState().COPY_CLJ_SOURCES = myCopySourcesCb.isSelected();
  }

  public void reset() {
    myClojureBeforeCheckBox.setSelected(mySettings.getState().CLOJURE_BEFORE);
    myCompileTaggedCb.setSelected(mySettings.getState().COMPILE_CLOJURE);
    myCopySourcesCb.setSelected(mySettings.getState().COPY_CLJ_SOURCES);
  }

  public void disposeUIResources() {
  }
}
