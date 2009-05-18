package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.plugins.clojure.repl.ReplToolWindow;

/**
 * @author ilyas, Kurt Christensen
 */
public class GoToReplAction extends ClojureAction {

  public void actionPerformed(final AnActionEvent e) {
    final ReplToolWindow window = getReplToolWindow(e);
    window.requestFocus();
  }
}