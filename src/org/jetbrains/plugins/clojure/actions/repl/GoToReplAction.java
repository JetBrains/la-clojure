package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.plugins.clojure.repl.ReplToolWindow;
import org.jetbrains.plugins.clojure.ClojureIcons;

/**
 * @author Kurt Christensen, ilyas
 */
public class GoToReplAction extends ClojureAction {

  public GoToReplAction(){
    getTemplatePresentation().setIcon(ClojureIcons.REPL_GO);
  }
  public void actionPerformed(final AnActionEvent e) {
    final ReplToolWindow window = getReplToolWindow(e);
    window.requestFocus();
  }
}