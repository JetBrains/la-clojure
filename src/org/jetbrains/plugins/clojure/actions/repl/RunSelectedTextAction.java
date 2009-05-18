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
    final CaretModel caretModel = editor.getCaretModel();
    final SelectionModel selectionModel = editor.getSelectionModel();
    String selectedText = selectionModel.getSelectedText();
    if (selectedText == null || selectedText.length() == 0) {
      final int line = caretModel.getLogicalPosition().line;
      final Document document = editor.getDocument();
      final int start = document.getLineStartOffset(line);
      final int end = document.getLineStartOffset(line);
      final String text = document.getText();
      selectedText = (end < text.length() - 1) ? text.substring(start, end) : text.substring(start);
      selectedText = selectedText.trim();
    }

    if (StringUtil.isEmptyOrSpaces(selectedText)) {
      return;
    }
    getReplToolWindow(e).writeToCurrentRepl(selectedText);
  }
}
