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
import java.util.Set;

/**
 * @author ilyas
 */
public class ClojureCompilerProjectComponent implements ProjectComponent {
  private Project myProject;

  public ClojureCompilerProjectComponent(Project project) {
    myProject = project;
  }

  public void projectOpened() {
    /* add clojure as compilable files */
    CompilerManager compilerManager = CompilerManager.getInstance(myProject);
    compilerManager.addCompilableFileType(ClojureFileType.CLOJURE_FILE_TYPE);

    ClojureCompilerSettings settings = ClojureCompilerSettings.getInstance(myProject);
    for (ClojureCompiler compiler : CompilerManager.getInstance(myProject).getCompilers(ClojureCompiler.class)) {
      compilerManager.removeCompiler(compiler);
    }
    if (settings.CLOJURE_BEFORE) {
      Set<FileType> inputSet = new HashSet<FileType>(Arrays.asList(ClojureFileType.CLOJURE_FILE_TYPE, StdFileTypes.JAVA));
      Set<FileType> outputSet = new HashSet<FileType>(Arrays.asList(StdFileTypes.JAVA, StdFileTypes.CLASS));
      compilerManager.addTranslatingCompiler(new ClojureCompiler(myProject), inputSet, outputSet);
    } else {
      compilerManager.addTranslatingCompiler(new ClojureCompiler(myProject),
          new HashSet<FileType>(Arrays.asList(ClojureFileType.CLOJURE_FILE_TYPE, StdFileTypes.CLASS)),
          new HashSet<FileType>(Arrays.asList(StdFileTypes.CLASS)));
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
