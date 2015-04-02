package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.console.ConsoleHistoryController;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.plugins.clojure.ClojureLanguage;

/**
 * @author ilyas
 */
public class ClojureConsole extends LanguageConsoleImpl {
  public static final Key<Boolean> CLOJURE_CONSOLE_EDITOR = Key.create("CLOJURE_CONSOLE_EDITOR");

  private final ConsoleHistoryController myHistoryController;
  private final String myNReplHost;
  private final String myNReplPort;
  private ClojureConsoleExecuteActionHandler myExecuteHandler;

  public ClojureConsole(Project project,
                        String title,
                        ConsoleHistoryController historyController,
                        String nReplHost,
                        String nReplPort) {
    super(project, title, ClojureLanguage.getInstance());
    myHistoryController = historyController;
    myNReplHost = nReplHost;
    myNReplPort = nReplPort;
    getConsoleEditor().getDocument().putUserData(CLOJURE_CONSOLE_EDITOR, Boolean.TRUE);
  }

  public ConsoleHistoryController getHistoryController() {
    return myHistoryController;
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
