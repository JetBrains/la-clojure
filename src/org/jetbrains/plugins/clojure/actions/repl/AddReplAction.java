package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.actions.repl.util.ReplUtil;
import org.jetbrains.plugins.clojure.repl.ReplManager;

/**
 * @author Kurt Christensen, ilyas
 */
public class AddReplAction extends ClojureReplAction {
  public AddReplAction() {
    getTemplatePresentation().setIcon(ClojureIcons.REPL_ADD);
  }

  @Override
  public void update(AnActionEvent e) {
    final Module m = ReplUtil.getModule(e);
    final Presentation presentation = e.getPresentation();
    if (m == null) {
      presentation.setEnabled(false);
      return;
    }
    presentation.setEnabled(true);
    super.update(e);
  }

  public void actionPerformed(AnActionEvent e) {
    Module module = ReplUtil.getModule(e);
    if (module != null) {
      ReplManager.getInstance(module.getProject()).createNewRepl(module);
    }
  }

}
