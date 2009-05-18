package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;

/**
 * @author ilyas, Kurt Christensen
 */
public class RenameReplAction extends ClojureAction {
  public RenameReplAction() {
    getTemplatePresentation().setIcon(IconLoader.getIcon("/diff/applyNotConflicts.png"));
  }

  public void actionPerformed(AnActionEvent e) {
    getReplToolWindow(e).renameCurrentRepl();
  }
}
