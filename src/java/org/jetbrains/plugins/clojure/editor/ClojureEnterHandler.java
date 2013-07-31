package org.jetbrains.plugins.clojure.editor;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;

/**
 * @author peter
 */
public class ClojureEnterHandler implements EnterHandlerDelegate {
  public Result preprocessEnter(@NotNull final PsiFile file, @NotNull final Editor editor, @NotNull final Ref<Integer> caretOffsetRef,
                                @NotNull final Ref<Integer> caretAdvance, @NotNull final DataContext dataContext,
                                @Nullable final EditorActionHandler originalHandler) {
    if (file instanceof ClojureFile) {
      Document document = editor.getDocument();
      PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);
      int caret = caretOffsetRef.get().intValue();
      PsiElement leaf = file.findElementAt(caret);
      if (leaf instanceof PsiComment && ClojureTokenTypes.LINE_COMMENT == leaf.getNode().getElementType()) {
        String beforeCaret = leaf.getText().substring(0, caret - leaf.getTextRange().getStartOffset());
        int semicolonCount = 0;
        while (semicolonCount < beforeCaret.length() && beforeCaret.charAt(semicolonCount) == ';') {
          semicolonCount++;
        }
        if (!StringUtil.startsWith(document.getCharsSequence(), caret, ";")) {
          String prefix = StringUtil.repeat(";", semicolonCount);
          if (document.getCharsSequence().charAt(caret) != ' ') {
            prefix += " ";
          }
          document.insertString(caret, prefix);
          editor.getCaretModel().moveToOffset(caret);
          originalHandler.execute(editor, dataContext);
          editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() + prefix.length());
          return Result.Stop;
        }

      }
    }

    return Result.Continue;
  }

  public Result postProcessEnter(@NotNull PsiFile psiFile, @NotNull Editor editor, @NotNull DataContext dataContext) {
    return Result.Continue;
  }
}
