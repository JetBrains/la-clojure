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
  private final String myNReplHost;
  private final String myNReplPort;
  private ClojureConsoleExecuteActionHandler myExecuteHandler;

  public ClojureConsole(Project project,
                        String title,
                        ConsoleHistoryModel historyModel,
                        ClojureConsoleExecuteActionHandler handler,
                        String nReplHost,
                        String nReplPort) {
    super(project, title, ClojureLanguage.getInstance());
    myHistoryModel = historyModel;
    myNReplHost = nReplHost;
    myNReplPort = nReplPort;
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

  public String getNReplPort() {
    return myNReplPort;
  }

  public String getNReplHost() {
    return myNReplHost;
  }
}
