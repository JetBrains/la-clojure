package org.jetbrains.plugins.clojure.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.clojure.highlighter.ClojureSyntaxHighlighter;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

/**
 * @author ilyas
 */
public class ClojureAnnotator implements Annotator {
  public void annotate(PsiElement element, AnnotationHolder holder) {
    if (element instanceof ClList) {
      annotateList((ClList) element, holder);
    }
  }

  private void annotateList(ClList list, AnnotationHolder holder) {
    final ClSymbol first = list.getFirstSymbol();
    if (first != null) {
      Annotation annotation = holder.createInfoAnnotation(first, null);
      annotation.setTextAttributes(ClojureSyntaxHighlighter.DEF);
    }
  }
}
