package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiFactory;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.repl.ReplPanel;

/**
 * @author Kurt Christensen, ilyas
 */
public class RunSelectedTextAction extends ClojureConsoleAction {

  public RunSelectedTextAction(){
    getTemplatePresentation().setIcon(ClojureIcons.REPL_EVAL);
  }

  @Override
  public void update(final AnActionEvent e) {
    final Presentation presentation = e.getPresentation();
    presentation.setEnabled(getCurrentRepl(e) != null);
  }

  public void actionPerformed(final AnActionEvent e) {
    final Editor editor = e.getData(DataKeys.EDITOR);
    if (editor == null) {
      return;
    }
    final SelectionModel selectionModel = editor.getSelectionModel();
    String selectedText = selectionModel.getSelectedText();
    if (selectedText == null || selectedText.trim().length() == 0) {
      return;
    }
    final String text = selectedText.trim();
    final Project project = editor.getProject();

    final String msg = ClojurePsiFactory.getInstance(project).getErrorMessage(text);
    if (msg != null) {
      Messages.showErrorDialog(project,
              ClojureBundle.message("evaluate.incorrect.form", msg),
              ClojureBundle.message("evaluate.incorrect.cannot.evaluate"));
      return;
    }

    final ReplPanel repl = getCurrentRepl(e);
    if (repl != null) repl.writeToCurrentRepl(text, false);
  }

}
