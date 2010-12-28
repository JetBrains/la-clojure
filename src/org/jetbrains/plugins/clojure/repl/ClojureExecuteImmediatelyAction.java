package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author ilyas
 */
public class ClojureExecuteImmediatelyAction extends ClojureExecuteActionBase {

  public ClojureExecuteImmediatelyAction(ClojureConsole languageConsole,
                                         ProcessHandler processHandler,
                                         ClojureConsoleExecuteActionHandler consoleExecuteActionHandler) {
    super(languageConsole, processHandler, consoleExecuteActionHandler, ClojureConsoleRunner.EXECUTE_ACTION_IMMEDIATELY_ID);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    getExecuteActionHandler().runExecuteAction(myLanguageConsole, true);
  }
}
