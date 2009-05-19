package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.clojure.repl.ReplToolWindow;

/**
 * @author Kurt Christensen, ilyas
 */
public abstract class ClojureAction extends AnAction {
  private static final String REPL_TOOL_WINDOW_ID = "repl.toolWindow";

  protected ReplToolWindow getReplToolWindow(AnActionEvent e) {
    Project project = e.getData(DataKeys.PROJECT);
    return (ReplToolWindow) project.getComponent(REPL_TOOL_WINDOW_ID);
  }

  protected String getFilePath(AnActionEvent e) {
    return e.getData(DataKeys.VIRTUAL_FILE).getPath();
  }
}
