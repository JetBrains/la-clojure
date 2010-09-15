package org.jetbrains.plugins.clojure.refactoring.rename;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenameInputValidator;
import com.intellij.util.ProcessingContext;
import org.jetbrains.plugins.clojure.lexer.ClojureFlexLexer;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;

/**
 * @author ilyas
 */

public class ClojureRenameInputValidator implements RenameInputValidator {
  public ElementPattern<? extends PsiElement> getPattern() {
    return new ClojureSymbolPattern();
  }

  public boolean isInputValid(String newName, PsiElement element, ProcessingContext context) {
    final ClojureFlexLexer lexer = new ClojureFlexLexer();
    lexer.start(newName, 0, newName.length(), 0);
    if (lexer.getTokenType() != ClojureTokenTypes.symATOM) return false;
    lexer.advance();
    return lexer.getTokenType() == null;
  }
}
