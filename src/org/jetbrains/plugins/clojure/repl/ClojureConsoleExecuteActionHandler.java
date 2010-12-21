package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.console.LanguageConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ConsoleExecuteActionHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;

/**
 * @author ilyas
 */
public class ClojureConsoleExecuteActionHandler extends ConsoleExecuteActionHandler {

  private LanguageConsoleViewImpl myConsoleView;
  private ProcessHandler myProcessHandler;

  public ClojureConsoleExecuteActionHandler(LanguageConsoleViewImpl consoleView, ProcessHandler processHandler) {
    super(processHandler);
    myConsoleView = consoleView;
    myProcessHandler = processHandler;
  }

  @Override
  public void processLine(String line) {
    // todo: add smart prompt processing
    super.processLine(line);
    final LanguageConsoleImpl console = myConsoleView.getConsole();
    final Editor editor = console.getCurrentEditor();

    scrollDown(editor);
  }


  private void scrollDown(final Editor currentEditor) {
    ApplicationManager.getApplication().invokeLater(new Runnable() {
      public void run() {
        currentEditor.getCaretModel().moveToOffset(currentEditor.getDocument().getTextLength());
      }
    });
  }
}
