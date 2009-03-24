package org.jetbrains.plugins.clojure.compiler;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

import javax.swing.*;
import java.util.HashSet;
import java.util.Arrays;

/**
 * ilyas
 */
public class ClojureCompilerConfigurable implements Configurable {
  private JPanel myPanel;
  private JCheckBox clojureBeforeCheckBox;
  private ClojureCompilerSettings mySettings;
  private Project myProject;

  public ClojureCompilerConfigurable(ClojureCompilerSettings settings, Project project) {
    myProject = project;
    mySettings = settings;
  }

  @Nls
  public String getDisplayName() {
    return "Scala Compiler";
  }

  public Icon getIcon() {
    return null;
  }

  public String getHelpTopic() {
    return null;
  }

  public JComponent createComponent() {
    return myPanel;
  }

  public boolean isModified() {
    return mySettings.CLOJURE_BEFORE != clojureBeforeCheckBox.isSelected();
  }

  public void apply() throws ConfigurationException {
    if (clojureBeforeCheckBox.isSelected() && mySettings.CLOJURE_BEFORE != clojureBeforeCheckBox.isSelected()) {
      for (ClojureCompiler compiler: CompilerManager.getInstance(myProject).getCompilers(ClojureCompiler.class)) {
        CompilerManager.getInstance(myProject).removeCompiler(compiler);
      }
      HashSet<FileType> inputSet = new HashSet<FileType>(Arrays.asList(ClojureFileType.CLOJURE_FILE_TYPE, StdFileTypes.JAVA));
      HashSet<FileType> outputSet = new HashSet<FileType>(Arrays.asList(StdFileTypes.JAVA, StdFileTypes.CLASS));
      CompilerManager.getInstance(myProject).addTranslatingCompiler(new ClojureCompiler(myProject), inputSet, outputSet);
    } else if (!clojureBeforeCheckBox.isSelected() && mySettings.CLOJURE_BEFORE != clojureBeforeCheckBox.isSelected()){
      for (ClojureCompiler compiler: CompilerManager.getInstance(myProject).getCompilers(ClojureCompiler.class)) {
        CompilerManager.getInstance(myProject).removeCompiler(compiler);
      }
      CompilerManager.getInstance(myProject).addCompiler(new ClojureCompiler(myProject));
    }
    mySettings.CLOJURE_BEFORE = clojureBeforeCheckBox.isSelected();
  }

  public void reset() {
    clojureBeforeCheckBox.setSelected(mySettings.CLOJURE_BEFORE);
  }

  public void disposeUIResources() {
  }
}
