package org.jetbrains.plugins.clojure.repl;

import com.intellij.codeInsight.completion.CompletionProcess;
import com.intellij.codeInsight.completion.CompletionService;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.EmptyAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.IconLoader;

/**
* @author ilyas
*/
public abstract class ClojureExecuteActionBase extends DumbAwareAction {
  public static final String ACTIONS_EXECUTE_ICON = "/actions/execute.png";

  protected final ClojureConsole myLanguageConsole;
  protected final ProcessHandler myProcessHandler;
  protected final ClojureConsoleExecuteActionHandler myConsoleExecuteActionHandler;

  public ClojureExecuteActionBase(ClojureConsole languageConsole,
                                  ProcessHandler processHandler,
                                  ClojureConsoleExecuteActionHandler consoleExecuteActionHandler,
                                  String actionId) {
    super(null, null, IconLoader.getIcon(ACTIONS_EXECUTE_ICON));
    myLanguageConsole = languageConsole;
    myProcessHandler = processHandler;
    myConsoleExecuteActionHandler = consoleExecuteActionHandler;
    EmptyAction.setupAction(this, actionId, null);
  }

  public void update(final AnActionEvent e) {
    e.getPresentation().setEnabled(isActionEnabled());
  }

  private boolean isActionEnabled() {
    if (myProcessHandler.isProcessTerminated()) {
      return false;
    }

    final Lookup lookup = LookupManager.getActiveLookup(myLanguageConsole.getConsoleEditor());
    if (lookup == null || !lookup.isCompletion()) {
      return true;
    }

    CompletionProcess completion = CompletionService.getCompletionService().getCurrentCompletion();
    if (completion != null && completion.isAutopopupCompletion() && !lookup.isSelectionTouched()) {
      return true;
    }
    return false;
  }

  public ClojureConsoleExecuteActionHandler getExecuteActionHandler() {
    return myConsoleExecuteActionHandler;
  }

}
