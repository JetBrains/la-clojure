package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.plugins.clojure.repl.ReplManager;
import org.jetbrains.plugins.clojure.repl.ReplPanel;

/**
 * @author Kurt Christensen, ilyas
 */
public class RenameReplAction extends ClojureAction {

  @Override
  public void update(final AnActionEvent e) {
    final Presentation presentation = e.getPresentation();
    presentation.setEnabled(getCurrentRepl(e) != null);
  }

  public void actionPerformed(AnActionEvent e) {
    final ReplManager replManager = getReplManager(e);
    if (replManager != null) {
      replManager.renameCurrentRepl();
    }
  }
}
