package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author ilyas
 */
public class ClojureConsoleEnterAction extends ClojureExecuteActionBase {

  public ClojureConsoleEnterAction(ClojureConsole languageConsole,
                                   ProcessHandler processHandler,
                                   ClojureConsoleExecuteActionHandler consoleExecuteActionHandler) {
    super(languageConsole, processHandler, consoleExecuteActionHandler, ClojureConsoleRunner.EXECUTE_ACTION_ID);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    getExecuteActionHandler().runExecuteAction(myLanguageConsole, false);
  }
}