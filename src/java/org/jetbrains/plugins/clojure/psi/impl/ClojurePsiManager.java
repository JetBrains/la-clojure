package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

/**
 * @author ilyas
 */
public class ClojurePsiManager implements ProjectComponent {
  private final Project myProject;
  private PsiFile myDummyFile;

  public ClojurePsiManager(Project project) {
    myProject = project;
  }

  public void projectOpened() {
  }

  public void projectClosed() {
  }

  @NotNull
  public String getComponentName() {
    return "ClojurePsiManager";
  }

  public void initComponent() {
    ApplicationManager.getApplication().runReadAction(new Runnable() {
      public void run() {
        final String dummyFn = "dummy." + ClojureFileType.CLOJURE_FILE_TYPE.getDefaultExtension();
        myDummyFile = PsiFileFactory.getInstance(myProject)
            .createFileFromText(dummyFn, ClojureFileType.CLOJURE_FILE_TYPE, "");
      }
    });
  }

  public void disposeComponent() {
  }

  public static ClojurePsiManager getInstance(Project project) {
    return project.getComponent(ClojurePsiManager.class);
  }

  public PsiFile getDummyFile() {
    return myDummyFile;
  }
}
