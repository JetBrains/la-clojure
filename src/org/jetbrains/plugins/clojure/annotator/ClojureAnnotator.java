package org.jetbrains.plugins.clojure.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.highlighter.ClojureSyntaxHighlighter;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClQuotedForm;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

/**
 * @author ilyas
 */
public class ClojureAnnotator implements Annotator {

  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element instanceof ClList) {
      annotateList((ClList) element, holder);
    }
    if (element instanceof ClSymbol) {
      ClSymbol symbol = (ClSymbol) element;
      if (symbol.isQualified()) {
        checkNonQualifiedSymbol(symbol, holder);
      }
    }
    if (element instanceof ClDef) {
      annotateDef((ClDef) element, holder);
    }
  }

  private void annotateDef(ClDef element, AnnotationHolder holder) {
    ClSymbol symbol = element.getAllSymbols()[1];
    if (symbol != null) {
      addAnnotation(symbol, ClojureSyntaxHighlighter.GLOBAL_VAR_NAME_DECLARATION, holder);
    }
  }

  private void checkNonQualifiedSymbol(ClSymbol symbol, AnnotationHolder holder) {
    // todo add import fixo
  }

  private void annotateList(ClList list, AnnotationHolder holder) {
    final ClSymbol first = list.getFirstSymbol();
    if (first != null && !listIsQuoted(list)) {
      //if (first.multiResolve(false).length > 0) {
      addAnnotation(first, ClojureSyntaxHighlighter.CALL_OPERATOR, holder);
      annotateSymbolDeclarations(list, holder, first);
    }
  }

  private void annotateSymbolDeclarations(ClList list, AnnotationHolder holder, ClSymbol first) {
    String name = first.getName();
    if (name != null) {
      if (name.equals("declare")) {
        ClSymbol[] symbols = list.getAllSymbols();
        for (int i = 1; i < symbols.length; i++) {
          addAnnotation(symbols[i], ClojureSyntaxHighlighter.GLOBAL_VAR_NAME_DECLARATION, holder);
        }
      } else if (name.equals("intern")) {
        PsiElement[] children = list.getChildren();
        boolean nsFound = false;
        for (PsiElement element : children) {
          if (element instanceof ClQuotedForm) {
            if (nsFound) {
              addAnnotation(element.findElementAt(1), ClojureSyntaxHighlighter.GLOBAL_VAR_NAME_DECLARATION, holder);
              return;
            } else {
              nsFound = true;
            }
          }
        }
      }
    }
  }

  private boolean listIsQuoted(ClList list) {
    PsiElement parent = list.getParent();
    return parent != null && parent instanceof ClQuotedForm;
  }

  private void addAnnotation(PsiElement symbol, TextAttributesKey key, AnnotationHolder annotationHolder) {
    Annotation annotation = annotationHolder.createInfoAnnotation(symbol, null);
    annotation.setTextAttributes(key);
  }
}
