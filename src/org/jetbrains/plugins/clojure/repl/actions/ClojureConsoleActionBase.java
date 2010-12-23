package org.jetbrains.plugins.clojure.repl.actions;

import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.NotNullFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.repl.ClojureConsoleProcessHandler;
import org.jetbrains.plugins.clojure.utils.ClojureUtils;

/**
 * @author ilyas
 */
public abstract class ClojureConsoleActionBase extends AnAction {


  protected static ClojureConsoleProcessHandler findRunningClojureConsole(Project project) {
    final ProcessHandler handler = ExecutionHelper.findRunningConsole(project, new ClojureConsoleMatcher());
    if (handler instanceof ClojureConsoleProcessHandler) {
      return (ClojureConsoleProcessHandler) handler;
    }
    return null;
  }

  private static class ClojureConsoleMatcher implements NotNullFunction<String, Boolean> {
    @NotNull
    public Boolean fun(String cmdLine) {
      return cmdLine != null && cmdLine.contains(ClojureUtils.CLOJURE_MAIN);
    }
  }

  @Override
  public void update(AnActionEvent e) {
    final Presentation presentation = e.getPresentation();

    final Editor editor = e.getData(DataKeys.EDITOR);

    if (editor == null) {
      presentation.setEnabled(false);
      return;
    }
    final Project project = editor.getProject();
    if (project == null) {
      presentation.setEnabled(false);
      return;
    }

    final Document document = editor.getDocument();
    final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if (psiFile == null || !(psiFile instanceof ClojureFile)) {
      presentation.setEnabled(false);
      return;
    }

    final VirtualFile virtualFile = psiFile.getVirtualFile();
    if (virtualFile == null || virtualFile instanceof LightVirtualFile) {
      presentation.setEnabled(false);
      return;
    }
    final String filePath = virtualFile.getPath();
    if (filePath == null) {
      presentation.setEnabled(false);
      return;
    }

    final ClojureConsoleProcessHandler handler = findRunningClojureConsole(project);
    if (handler == null) {
      presentation.setEnabled(false);
      return;
    }

    presentation.setEnabled(true);

  }
}
