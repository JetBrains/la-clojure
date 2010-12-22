package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.console.LanguageConsoleViewImpl;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ConsoleExecuteActionHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;

/**
 * @author ilyas
 */
public class ClojureConsoleExecuteActionHandler extends ConsoleExecuteActionHandler {

  private LanguageConsoleViewImpl myConsoleView;
  private ProcessHandler myProcessHandler;
  private Project myProject;

  public ClojureConsoleExecuteActionHandler(LanguageConsoleViewImpl consoleView,
                                            ProcessHandler processHandler,
                                            Project project) {
    super(processHandler, true);
    myConsoleView = consoleView;
    myProcessHandler = processHandler;
    myProject = project;
  }

  @Override
  public void processLine(String text) {
    super.processLine(text);
    final LanguageConsoleImpl console = myConsoleView.getConsole();
    final Editor editor = console.getCurrentEditor();
    scrollDown(editor);
  }

  @Override
  public void runExecuteAction(LanguageConsoleImpl languageConsole, ConsoleHistoryModel consoleHistoryModel) {
    // Process input and add to history
    final Editor editor = languageConsole.getCurrentEditor();
    final Document document = editor.getDocument();
    final String text = document.getText();

    final String candidate = text.trim();

    if (ClojurePsiUtil.isValidClojureExpression(candidate, myProject) || "".equals(candidate)) { // S-expression contains no syntax errors
      super.runExecuteAction(languageConsole, consoleHistoryModel);
    } else {
      languageConsole.setInputText(text + "\n");
    }

  }

  private void scrollDown(final Editor currentEditor) {
    ApplicationManager.getApplication().invokeLater(new Runnable() {
      public void run() {
        currentEditor.getCaretModel().moveToOffset(currentEditor.getDocument().getTextLength());
      }
    });
  }
}
