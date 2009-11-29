package org.jetbrains.plugins.clojure.refactoring.rename;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.lang.LanguageExtensionPoint;
import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.clojure.lexer.ClojureFlexLexer;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;

/**
 * @author ilyas
 */
public class ClojureNameValidator extends LanguageExtensionPoint<NamesValidator> implements NamesValidator {

  public boolean isKeyword(String name, Project project) {
    final ClojureFlexLexer lexer = new ClojureFlexLexer();
    lexer.start(name, 0, name.length(), 0);
    if (!ClojureTokenTypes.KEYWORDS.contains(lexer.getTokenType())) return false;
    lexer.advance();
    return lexer.getTokenType() == null;
  }

  public boolean isIdentifier(String name, Project project) {
    final ClojureFlexLexer lexer = new ClojureFlexLexer();
    lexer.start(name, 0, name.length(), 0);
    if (!ClojureTokenTypes.IDENTIFIERS.contains(lexer.getTokenType())) return false;
    lexer.advance();
    return lexer.getTokenType() == null;
  }
}
