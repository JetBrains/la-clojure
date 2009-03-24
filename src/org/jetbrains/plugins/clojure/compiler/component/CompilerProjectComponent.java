package org.jetbrains.plugins.clojure.compiler.component;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;

import java.util.HashSet;
import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.compiler.ClojureCompilerSettings;
import org.jetbrains.plugins.clojure.compiler.ClojureCompiler;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

/**
 * @author ilyas
 */
public class CompilerProjectComponent implements ProjectComponent {
  private Project myProject;

  public CompilerProjectComponent(Project project) {
    myProject = project;
  }

  public void projectOpened() {
    ClojureCompilerSettings settings = ClojureCompilerSettings.getInstance(myProject);
    if (settings.CLOJURE_BEFORE) {
      for (ClojureCompiler compiler : CompilerManager.getInstance(myProject).getCompilers(ClojureCompiler.class)) {
        CompilerManager.getInstance(myProject).removeCompiler(compiler);
      }
      HashSet<FileType> inputSet = new HashSet<FileType>(Arrays.asList(ClojureFileType.CLOJURE_FILE_TYPE, StdFileTypes.JAVA));
      HashSet<FileType> outputSet = new HashSet<FileType>(Arrays.asList(StdFileTypes.JAVA, StdFileTypes.CLASS));
      CompilerManager.getInstance(myProject).addTranslatingCompiler(new ClojureCompiler(myProject), inputSet, outputSet);
    } else {
      for (ClojureCompiler compiler : CompilerManager.getInstance(myProject).getCompilers(ClojureCompiler.class)) {
        CompilerManager.getInstance(myProject).removeCompiler(compiler);
      }
      CompilerManager.getInstance(myProject).addCompiler(new ClojureCompiler(myProject));
    }
  }

  public void projectClosed() {
  }

  @NotNull
  public String getComponentName() {
    return "Component to change compilers order";
  }

  public void initComponent() {
  }

  public void disposeComponent() {
  }
}
