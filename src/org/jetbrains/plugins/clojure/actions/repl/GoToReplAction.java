package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author ilyas, Kurt Christensen
 */
public class GoToReplAction extends ClojureAction {

  public void actionPerformed(final AnActionEvent e) {
    getReplToolWindow(e).requestFocus();
  }
}