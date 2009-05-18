package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.Icons;

/**
 * @author ilyas, Kurt Christensen
 */
public class AddReplAction extends ClojureAction {
  public AddReplAction() {
    getTemplatePresentation().setIcon(Icons.ADD_ICON);
  }

  public void actionPerformed(AnActionEvent e) {
    getReplToolWindow(e).createRepl();
    getReplToolWindow(e).requestFocus();
  }
}
