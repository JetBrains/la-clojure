package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.console.LanguageConsoleViewImpl;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.openapi.project.Project;

/**
 * @author ilyas
 */
public class ClojureConsoleView extends LanguageConsoleViewImpl {
  public ClojureConsoleView(Project project, String title, ConsoleHistoryModel historyModel,
                            ClojureConsoleExecuteActionHandler executeHandler) {
    super(new ClojureConsole(project, title, historyModel, executeHandler));
  }

  @Override
  public ClojureConsole getConsole() {
    return ((ClojureConsole) super.getConsole());
  }
}
