package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.clojure.ClojureLanguage;

/**
 * @author ilyas
 */
public class ClojureConsole extends LanguageConsoleImpl {

  private final ConsoleHistoryModel myHistoryModel;
  private ClojureConsoleExecuteActionHandler myExecuteHandler;

  public ClojureConsole(Project project,
                        String title,
                        ConsoleHistoryModel historyModel,
                        ClojureConsoleExecuteActionHandler handler) {
    super(project, title, ClojureLanguage.getInstance());
    myHistoryModel = historyModel;
  }

  public ConsoleHistoryModel getHistoryModel() {
    return myHistoryModel;
  }

  public ClojureConsoleExecuteActionHandler getExecuteHandler() {
    return myExecuteHandler;
  }

  public void setExecuteHandler(ClojureConsoleExecuteActionHandler handler) {
    this.myExecuteHandler = handler;
  }
}
