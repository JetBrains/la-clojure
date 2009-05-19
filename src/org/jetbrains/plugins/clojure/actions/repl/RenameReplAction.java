package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;

/**
 * @author Kurt Christensen, ilyas
 */
public class RenameReplAction extends ClojureAction {

  public void actionPerformed(AnActionEvent e) {
    getReplToolWindow(e).renameCurrentRepl();
  }
}
