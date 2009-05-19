package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;

/**
 * @author Kurt Christensen, ilyas
 */
public class RemoveReplAction extends ClojureAction {
  public RemoveReplAction() {
    getTemplatePresentation().setIcon(IconLoader.getIcon("/actions/cancel.png"));
  }

  public void actionPerformed(AnActionEvent e) {
    getReplToolWindow(e).removeCurrentRepl();
  }
}