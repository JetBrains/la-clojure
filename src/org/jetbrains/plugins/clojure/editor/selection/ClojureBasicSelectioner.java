package org.jetbrains.plugins.clojure.editor.selection;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.util.text.CharArrayUtil;
import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandler;

import java.util.List;
import java.util.ArrayList;

/**
 * @author ilyas
 */
public abstract class ClojureBasicSelectioner implements ExtendWordSelectionHandler {

  public List<TextRange> select(PsiElement e, CharSequence editorText, int cursorOffset, Editor editor) {

    final TextRange originalRange = e.getTextRange();
    List<TextRange> ranges = expandToWholeLine(editorText, originalRange, true);

    if (ranges.size() == 1 && ranges.contains(originalRange)) {
      ranges = expandToWholeLine(editorText, originalRange, false);
    }

    List<TextRange> result = new ArrayList<TextRange>();
    result.addAll(ranges);
    return result;
  }

  static List<TextRange> expandToWholeLine(CharSequence text, TextRange range, boolean isSymmetric) {
    int textLength = text.length();
    List<TextRange> result = new ArrayList<TextRange>();

    if (range == null) {
      return result;
    }

    boolean hasNewLines = false;

    for (int i = range.getStartOffset(); i < range.getEndOffset(); i++) {
      char c = text.charAt(i);

      if (c == '\r' || c == '\n') {
        hasNewLines = true;
        break;
      }
    }

    if (!hasNewLines) {
      result.add(range);
    }


    int startOffset = range.getStartOffset();
    int endOffset = range.getEndOffset();
    int index1 = CharArrayUtil.shiftBackward(text, startOffset - 1, " \t");
    if (endOffset > startOffset && text.charAt(endOffset - 1) == '\n' || text.charAt(endOffset - 1) == '\r') {
      endOffset--;
    }
    int index2 = Math.min(textLength, CharArrayUtil.shiftForward(text, endOffset, " \t"));

    if (index1 < 0
        || text.charAt(index1) == '\n'
        || text.charAt(index1) == '\r'
        || index2 == textLength
        || text.charAt(index2) == '\n'
        || text.charAt(index2) == '\r') {

      if (!isSymmetric) {
        if (index1 < 0 || text.charAt(index1) == '\n' || text.charAt(index1) == '\r') {
          startOffset = index1 + 1;
        }

        if (index2 == textLength || text.charAt(index2) == '\n' || text.charAt(index2) == '\r') {
          endOffset = index2;
          if (endOffset < textLength) {
            endOffset++;
            if (endOffset < textLength && text.charAt(endOffset - 1) == '\r' && text.charAt(endOffset) == '\n') {
              endOffset++;
            }
          }
        }

        result.add(new TextRange(startOffset, endOffset));
      } else {
        if ((index1 < 0 || text.charAt(index1) == '\n' || text.charAt(index1) == '\r') &&
            (index2 == textLength || text.charAt(index2) == '\n' || text.charAt(index2) == '\r')) {
          startOffset = index1 + 1;
          endOffset = index2;
          if (endOffset < textLength) {
            endOffset++;
            if (endOffset < textLength && text.charAt(endOffset - 1) == '\r' && text.charAt(endOffset) == '\n') {
              endOffset++;
            }
          }
          result.add(new TextRange(startOffset, endOffset));
        } else {
          result.add(range);
        }
      }
    } else {
      result.add(range);
    }

    return result;
  }
}
