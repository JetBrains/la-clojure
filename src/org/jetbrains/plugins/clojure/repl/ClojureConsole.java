package org.jetbrains.plugins.clojure.repl;

import com.google.common.base.Function;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.clojure.ClojureLanguage;

/**
 * @author ilyas
 */
public class ClojureConsole extends LanguageConsoleImpl {

  private AbstractConsoleRunnerWithHistory.ConsoleExecuteAction myExecuteAction;

  public ClojureConsole(Project project, String title, boolean doSaveErrorsToHistory) {
    super(project, title, ClojureLanguage.getInstance());
  }

  public AbstractConsoleRunnerWithHistory.ConsoleExecuteAction getExecuteAction() {
    return myExecuteAction;
  }

  public void setExecuteAction(AbstractConsoleRunnerWithHistory.ConsoleExecuteAction action) {
    this.myExecuteAction = action;
  }
}
