package org.jetbrains.plugins.clojure.compiler.component;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.compiler.ClojureCompiler;
import org.jetbrains.plugins.clojure.compiler.ClojureCompilerSettings;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author ilyas
 */
public class ClojureCompilerProjectComponent implements ProjectComponent {
  private Project myProject;

  public ClojureCompilerProjectComponent(Project project) {
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
    return "ClojureCompilerComponent";
  }

  public void initComponent() {
  }

  public void disposeComponent() {
  }
}
