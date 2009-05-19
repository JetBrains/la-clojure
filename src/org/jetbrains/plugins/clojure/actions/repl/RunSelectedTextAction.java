package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiElementFactory;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.ClojureIcons;

/**
 * @author Kurt Christensen, ilyas
 */
public class RunSelectedTextAction extends ClojureAction {

  public RunSelectedTextAction(){
    getTemplatePresentation().setIcon(ClojureIcons.REPL_EVAL);
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

    if (ClojurePsiElementFactory.getInstance(project).hasSyntacticalErrors(text)) {
      Messages.showErrorDialog(project,
              ClojureBundle.message("evaluate.incorrect.form"),
              ClojureBundle.message("evaluate.incorrect.cannot.evaluate"));
      return;
    }

    getReplToolWindow(e).writeToCurrentRepl(text);
  }
}
