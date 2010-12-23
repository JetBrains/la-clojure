package org.jetbrains.plugins.clojure.repl.actions;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.repl.ClojureConsoleProcessHandler;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author ilyas
 */
public class LoadClojureFileInConsoleAction extends ClojureConsoleActionBase {

  private static final Logger LOG = Logger.getInstance("#org.jetbrains.plugins.clojure.repl.actions.LoadClojureFileInConsoleAction");

  public LoadClojureFileInConsoleAction() {
    getTemplatePresentation().setIcon(ClojureIcons.REPL_LOAD);
  }

  @Override
  public void update(AnActionEvent e) {
    super.update(e);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    final Editor editor = e.getData(DataKeys.EDITOR);

    if (editor == null) return;
    final Project project = editor.getProject();
    if (project == null) return;

    final Document document = editor.getDocument();
    final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if (psiFile == null || !(psiFile instanceof ClojureFile)) return;

    final VirtualFile virtualFile = psiFile.getVirtualFile();
    if (virtualFile == null) return;
    final String filePath = virtualFile.getPath();
    if (filePath == null) return;

    final String command = "(load-file \"" + filePath + "\")\n";

    PsiDocumentManager.getInstance(project).commitAllDocuments();
    FileDocumentManager.getInstance().saveAllDocuments();

    executeCommand(project, command);
  }

  private static void executeCommand(Project project, String command) {
    final ClojureConsoleProcessHandler processHandler = findRunningClojureConsole(project);

    LOG.assertTrue(processHandler != null);

    final LanguageConsoleImpl languageConsole = processHandler.getLanguageConsole();
    languageConsole.setInputText(command);
    final TextRange range = new TextRange(0, command.length());

    languageConsole.getCurrentEditor().getSelectionModel().setSelection(range.getStartOffset(), range.getEndOffset());
    languageConsole.addCurrentToHistory(range, false, false);
    languageConsole.setInputText("");

    // write to process input
    final Ref<Boolean> isSuccess = new Ref<Boolean>(false);
    String error = null;
    try {
      final OutputStream processInput = processHandler.getProcessInput();
      if (processInput != null) {
        processInput.write((command).getBytes());
        processInput.flush();
        isSuccess.set(true);
      }
    }
    catch (IOException e1) {
      error = e1.getMessage();
    }
    if (!isSuccess.get()) {
      showError(ClojureBundle.message("clojure.repl.unable.load.to.console") + (error != null ? "(" + error + ")" : ""));
    }
  }

  private static void showError(String msg) {
    Messages.showErrorDialog(msg, ClojureBundle.message("clojure.repl.actions.load.text.title"));
  }


}
