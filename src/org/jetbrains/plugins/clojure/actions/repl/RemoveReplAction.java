package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.repl.ReplManager;

/**
 * @author Kurt Christensen, ilyas
 */
public class RemoveReplAction extends ClojureAction {
  public RemoveReplAction() {
    getTemplatePresentation().setIcon(ClojureIcons.REPL_CLOSE);
  }

  @Override
  public void update(final AnActionEvent e) {
    final Presentation presentation = e.getPresentation();
    final ReplManager replManager = getReplManager(e);
    presentation.setEnabled(replManager != null && replManager.hasActiveRepls());
  }

  public void actionPerformed(AnActionEvent e) {
    final Project project = PlatformDataKeys.PROJECT.getData(e.getDataContext());
    if (project != null) {
      ReplManager.getInstance(project).removeCurrentRepl();
    }
  }
}