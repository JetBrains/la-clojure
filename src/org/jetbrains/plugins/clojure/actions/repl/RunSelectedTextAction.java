package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.Document;

/**
 * @author ilyas, Kurt Christensen
 */
public class RunSelectedTextAction extends ClojureAction {

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
    getReplToolWindow(e).writeToCurrentRepl(selectedText.trim());
  }
}
