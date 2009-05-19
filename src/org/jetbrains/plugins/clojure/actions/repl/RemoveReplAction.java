package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.plugins.clojure.ClojureIcons;

/**
 * @author Kurt Christensen, ilyas
 */
public class RemoveReplAction extends ClojureAction {
  public RemoveReplAction() {
    getTemplatePresentation().setIcon(ClojureIcons.REPL_CLOSE);
  }

  public void actionPerformed(AnActionEvent e) {
    getReplToolWindow(e).removeCurrentRepl();
  }
}