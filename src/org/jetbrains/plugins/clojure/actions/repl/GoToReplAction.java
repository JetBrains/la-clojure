package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import org.jetbrains.plugins.clojure.repl.ReplManager;
import org.jetbrains.plugins.clojure.ClojureIcons;

/**
 * @author Kurt Christensen, ilyas
 */
public class GoToReplAction extends ClojureConsoleAction {

  public GoToReplAction(){
    getTemplatePresentation().setIcon(ClojureIcons.REPL_GO);
  }

  @Override
  public void update(final AnActionEvent e) {
    final Presentation presentation = e.getPresentation();
    final ReplManager replManager = getReplManager(e);
    presentation.setEnabled(replManager != null && replManager.hasActiveRepls());
  }

  public void actionPerformed(final AnActionEvent e) {
    final ReplManager replManager = getReplManager(e);
    if (replManager != null) {
      replManager.activate();
    }
  }
}