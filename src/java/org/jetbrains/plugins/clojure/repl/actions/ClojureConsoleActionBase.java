package org.jetbrains.plugins.clojure.repl.actions;

import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.NotNullFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.repl.ClojureConsole;
import org.jetbrains.plugins.clojure.repl.ClojureConsoleExecuteActionHandler;
import org.jetbrains.plugins.clojure.repl.ClojureConsoleProcessHandler;

import java.util.Collection;

/**
 * @author ilyas
 */
public abstract class ClojureConsoleActionBase extends AnAction {

  private static final Logger LOG = Logger.getInstance("#org.jetbrains.plugins.clojure.repl.actions.LoadClojureFileInConsoleAction");

  protected static ClojureConsoleProcessHandler findRunningClojureConsole(Project project) {
    final Collection<RunContentDescriptor> descriptors = ExecutionHelper.findRunningConsole(project, new ClojureConsoleMatcher());
    for (RunContentDescriptor descriptor : descriptors) {
      final ProcessHandler handler = descriptor.getProcessHandler();
      if (handler instanceof ClojureConsoleProcessHandler) {
        return (ClojureConsoleProcessHandler) handler;
      }
    }
    return null;
  }

  protected static void executeCommand(final Project project, String command) {
    final ClojureConsoleProcessHandler processHandler = findRunningClojureConsole(project);

    LOG.assertTrue(processHandler != null);

    // implement a command
    final LanguageConsoleImpl languageConsole = processHandler.getLanguageConsole();
    languageConsole.setInputText(command);

    final Editor editor = languageConsole.getCurrentEditor();
    final CaretModel caretModel = editor.getCaretModel();
    caretModel.moveToOffset(command.length());


    LOG.assertTrue(languageConsole instanceof ClojureConsole);

    final ClojureConsole console = (ClojureConsole) languageConsole;
    final ClojureConsoleExecuteActionHandler handler = console.getExecuteHandler();

    handler.runExecuteAction(console, true);
  }

  private static class ClojureConsoleMatcher implements NotNullFunction<RunContentDescriptor, Boolean> {
    @NotNull
    public Boolean fun(RunContentDescriptor descriptor) {
      return descriptor != null && (descriptor.getExecutionConsole() instanceof ClojureConsole);
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

    final LanguageConsoleImpl console = handler.getLanguageConsole();
    if (!(console instanceof ClojureConsole)) {
      presentation.setEnabled(false);
      return;
    }

    presentation.setEnabled(true);

  }

  protected static void showError(String msg) {
    Messages.showErrorDialog(msg, ClojureBundle.message("clojure.repl.actions.load.text.title"));
  }


}
