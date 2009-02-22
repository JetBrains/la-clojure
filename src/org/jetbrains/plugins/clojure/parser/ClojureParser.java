package org.jetbrains.plugins.clojure.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import static org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes.*;
import static org.jetbrains.plugins.clojure.parser.ClojureElementTypes.*;
import org.jetbrains.plugins.clojure.parser.ClojureSpecialFormTokens;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.annotations.NotNull;


/**
 * User: peter
 * Date: Nov 21, 2008
 * Time: 9:45:41 AM
 * Copyright 2007, 2008, 2009 Red Shark Technology
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ClojureParser implements PsiParser, ClojureSpecialFormTokens {

  @NotNull
  public ASTNode parse(IElementType root, PsiBuilder builder) {
    //builder.setDebugMode(true);
    PsiBuilder.Marker marker = builder.mark();
    for (IElementType token = builder.getTokenType(); token != null; token = builder.getTokenType()) {
      parseTopLevelExpression(builder);
    }
    marker.done(FILE);
    return builder.getTreeBuilt();
  }

  /**
   * Enter: Lexer is pointed at the left paren
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseTopLevelExpression(PsiBuilder builder) {

    IElementType token = builder.getTokenType();
    if (LEFT_PAREN == token) {
      parseTopLevelList(builder);
    } else {
      parseExpression(builder);
    }
  }

  /*
  Parse global function, variable or simple list
   */
  private void parseTopLevelList(PsiBuilder builder) {
    if (builder.getTokenType() != LEFT_PAREN) internalError(ClojureBundle.message("expected.lparen"));
    PsiBuilder.Marker marker = markAndAdvance(builder);
    final String tokenText = builder.getTokenText();
    if (builder.getTokenType() == SYMBOL && tDEF.equals(tokenText)) {
      parseDef(builder, marker);
    } else if (builder.getTokenType() == SYMBOL && tDEFN.equals(tokenText)) {
      parseDefn(builder, marker);
    } else if (builder.getTokenType() == SYMBOL && tDEFN_DASH.equals(tokenText)) {
      parseDefnDash(builder, marker);
    } else {
      parseExpressions(RIGHT_PAREN, builder);
      marker.done(LIST);
    }
  }

  private void parseExpression(PsiBuilder builder) {
    IElementType token = builder.getTokenType();
    if (LEFT_PAREN == token) {
      parseList(builder);
    } else if (LEFT_SQUARE == token) {
      parseVector(builder);
    } else if (LEFT_CURLY == token) {
      parseMap(builder);
    } else if (QUOTE == token) {
      parseQuotedForm(builder);
    } else if (BACKQUOTE == token) {
      parseBackQuote(builder);
    } else if (SHARP == token) {
      parseSharp(builder);
    } else if (UP == token) {
      parseUp(builder);
    } else if (SHARP_CURLY == token) {
      parseSet(builder);
    } else if (SHARPUP == token) {
      parseMetadata(builder);
    } else if (TILDA == token) {
      parseTilda(builder);
    } else if (AT == token) {
      parseAt(builder);
    } else if (TILDAAT == token) {
      parseTildaAt(builder);
    } else if (SYMBOL == token) {
      parseSymbol(builder);
    } else if (PERCENT == token) {
      parseSymbol(builder);
    } else if (COLON_SYMBOL == token) {
      parseKey(builder);

    } else if (LITERALS.contains(token)) {
      parseLiteral(builder);
    } else {
      syntaxError(builder, ClojureBundle.message("expected.left.paren.symbol.or.literal"));
    }
  }

  private void parseExpressions(IElementType endToken, PsiBuilder builder) {
    for (IElementType token = builder.getTokenType(); token != endToken && token != null; token = builder.getTokenType()) {
      parseExpression(builder);
    }
    if (builder.getTokenType() != endToken) {
      builder.error(ClojureBundle.message("expected.token", endToken.toString()));
    } else {
      builder.advanceLexer();
    }
  }

  private void syntaxError(PsiBuilder builder, String msg) {
    String e = msg + ": " + builder.getTokenText();
    builder.error(e);
    advanceLexerOrEOF(builder);
  }

  private void advanceLexerOrEOF(PsiBuilder builder) {
    if (builder.getTokenType() != null) builder.advanceLexer();
  }

  private PsiBuilder.Marker markAndAdvance(PsiBuilder builder) {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    return marker;
  }

  private void markAndAdvance(PsiBuilder builder, IElementType type) {
    markAndAdvance(builder).done(type);
  }

  private void internalError(String msg) {
    throw new Error(msg);
  }

  /**
   * Enter: Lexer is pointed at symbol
   * Exit: Lexer is pointed immediately after symbol
   *
   * @param builder
   */
  private void parseSymbol(PsiBuilder builder) {
    markAndAdvance(builder, VARIABLE);
  }

  /**
   * Enter: Lexer is pointed at symbol
   * Exit: Lexer is pointed immediately after symbol
   *
   * @param builder
   */
  private void parseKey(PsiBuilder builder) {
    markAndAdvance(builder, KEY);
  }

  /**
   * Enter: Lexer is pointed at literal
   * Exit: Lexer is pointed immediately after literal
   *
   * @param builder
   */
  private void parseLiteral(PsiBuilder builder) {
    PsiBuilder.Marker marker = builder.mark();
    final boolean isWrong = builder.getTokenType() == WRONG_STRING_LITERAL;
    builder.advanceLexer();
    if (isWrong) {
      marker.error(ClojureBundle.message("uncompleted.string.literal"));
    } else {
      marker.done(LITERAL);
    }
  }

  /**
   * Enter: Lexer is pointed at '
   * Exit: Lexer is pointed immediately after quoted value
   */
  private void parseQuotedForm(PsiBuilder builder) {
    if (builder.getTokenType() != QUOTE) internalError(ClojureBundle.message("expected.quote"));
    final PsiBuilder.Marker mark = builder.mark();
    builder.advanceLexer();
    parseExpression(builder);
    mark.done(QUOTED_FORM);
  }

  /**
   * Enter: Lexer is pointed at `
   * Exit: Lexer is pointed immediately after quoted value
   */
  private void parseBackQuote(PsiBuilder builder) {
    if (builder.getTokenType() != BACKQUOTE) internalError(ClojureBundle.message("expected.backquote"));
    final PsiBuilder.Marker mark = builder.mark();
    builder.advanceLexer();
    parseExpression(builder);
    mark.done(BACKQUOTED_EXPRESSION);
  }

  /**
   * Enter: Lexer is pointed at #
   * Exit: Lexer is pointed immediately after closing }
   */
  private void parseSharp(PsiBuilder builder) {
    if (builder.getTokenType() != SHARP) internalError(ClojureBundle.message("expected.sharp"));
    PsiBuilder.Marker mark = builder.mark();
    builder.advanceLexer();
    parseExpression(builder);
    mark.done(SHARP_EXPRESSION);
  }

  private void parseSet(PsiBuilder builder) {
    if (builder.getTokenType() != SHARP_CURLY) internalError(ClojureBundle.message("expected.sharp.lcurly"));
    PsiBuilder.Marker marker = markAndAdvance(builder);
    for (IElementType token = builder.getTokenType(); token != RIGHT_CURLY && token != null; token = builder.getTokenType()) {
      parseExpression(builder); //entry
    }
    advanceLexerOrEOF(builder);
    marker.done(SET);

  }

  /**
   * Enter: Lexer is pointed at ^
   * Exit: Lexer is pointed immediately after closing }
   */
  private void parseUp(PsiBuilder builder) {
    if (builder.getTokenType() != UP) internalError(ClojureBundle.message("expected.cup"));
    PsiBuilder.Marker mark = builder.mark();
    builder.advanceLexer();
    parseExpression(builder);
    mark.done(META_FORM);
  }

  /**
   * Enter: Lexer is pointed at ^
   * Exit: Lexer is pointed immediately after closing }
   */
  private void parseMetadata(PsiBuilder builder) {
    //todo add expression with metadata
    if (builder.getTokenType() != SHARPUP) internalError(ClojureBundle.message("expected.sharp.cup"));
    PsiBuilder.Marker mark = builder.mark();
    builder.advanceLexer();
    parseExpression(builder);
    mark.done(METADATA);
  }

  /**
   * Enter: Lexer is pointed at ~
   * Exit: Lexer is pointed immediately after closing }
   */
  private void parseTilda(PsiBuilder builder) {
    if (builder.getTokenType() != TILDA) internalError(ClojureBundle.message("expected.tilde"));
    PsiBuilder.Marker mark = builder.mark();
    builder.advanceLexer();
    parseExpression(builder);
    mark.done(TILDA_EXPRESSION);
  }

  /**
   * Enter: Lexer is pointed at @
   * Exit: Lexer is pointed immediately after closing }
   */
  private void parseAt(PsiBuilder builder) {
    if (builder.getTokenType() != AT) internalError(ClojureBundle.message("expected.at"));
    PsiBuilder.Marker mark = builder.mark();
    builder.advanceLexer();
    parseExpression(builder);
    mark.done(AT_EXPRESSION);
  }

  /**
   * Enter: Lexer is pointed at ^
   * Exit: Lexer is pointed immediately after closing }
   */
  private void parseTildaAt(PsiBuilder builder) {
    if (builder.getTokenType() != TILDAAT) internalError(ClojureBundle.message("expected.tilde.at"));
    PsiBuilder.Marker mark = builder.mark();
    builder.advanceLexer();
    parseExpression(builder);
    mark.done(TILDAAT_EXPRESSION);
  }

  /**
   * Enter: Lexer is pointed at the opening left paren
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseList(PsiBuilder builder) {
    PsiBuilder.Marker marker = markAndAdvance(builder);
    parseExpressions(RIGHT_PAREN, builder);
    marker.done(LIST);
  }

  /**
   * Enter: Lexer is pointed at the opening left square
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseVector(PsiBuilder builder) {
    PsiBuilder.Marker marker = markAndAdvance(builder);
    parseExpressions(RIGHT_SQUARE, builder);
    marker.done(VECTOR);
  }

  /**
   * Enter: Lexer is pointed at the opening left paren
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseMap(PsiBuilder builder) {
    if (builder.getTokenType() != LEFT_CURLY) internalError(ClojureBundle.message("expected.lcurly"));
    PsiBuilder.Marker marker = markAndAdvance(builder);
    for (IElementType token = builder.getTokenType(); token != RIGHT_CURLY && token != null; token = builder.getTokenType()) {
      PsiBuilder.Marker entry = builder.mark();
      parseExpression(builder); // key
      parseExpression(builder); // value
      entry.done(MAP_ENTRY);
    }
    if (builder.getTokenType() != RIGHT_CURLY) {
      builder.error(ClojureBundle.message("expected.token", RIGHT_CURLY.toString()));
    } else {
      advanceLexerOrEOF(builder);
    }
    marker.done(MAP);
  }

  /**
   * Enter: Lexer is pointed at the def
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseDef(PsiBuilder builder, PsiBuilder.Marker marker) {
    if (!tDEF.equals(builder.getTokenText()) || builder.getTokenType() != SYMBOL) {
      internalError(ClojureBundle.message("expected.element"));
    }

    advanceLexerOrEOF(builder);
    for (IElementType token = builder.getTokenType(); token != RIGHT_PAREN && token != null; token = builder.getTokenType()) {
      parseExpression(builder);
    }

    if (builder.getTokenType() != RIGHT_PAREN) {
      builder.error(ClojureBundle.message("expected.token", RIGHT_PAREN.toString()));
    } else {
      advanceLexerOrEOF(builder);
    }
    marker.done(ClojureElementTypes.DEF);
  }

  /**
   * Enter: Lexer is pointed at the defn
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseDefn(PsiBuilder builder, PsiBuilder.Marker marker) {
    if (builder.getTokenType() != SYMBOL || !ClojureSpecialFormTokens.tDEFN.equals(builder.getTokenText())) {
      internalError(ClojureBundle.message("expected.defn"));
    }

    advanceLexerOrEOF(builder);
    for (IElementType token = builder.getTokenType(); token != RIGHT_PAREN && token != null; token = builder.getTokenType()) {
      parseExpression(builder);
    }

    if (builder.getTokenType() != RIGHT_PAREN) {
      builder.error(ClojureBundle.message("expected.token", RIGHT_PAREN.toString()));
    } else {
      advanceLexerOrEOF(builder);
    }
    marker.done(ClojureElementTypes.DEFN);
  }

  /**
   * Enter: Lexer is pointed at the defn-
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseDefnDash(PsiBuilder builder, PsiBuilder.Marker marker) {
    if (builder.getTokenType() != SYMBOL || !tDEFN_DASH.equals(builder.getTokenText()))
      internalError(ClojureBundle.message("expected.defndash"));
    advanceLexerOrEOF(builder);
    for (IElementType token = builder.getTokenType(); token != RIGHT_PAREN && token != null; token = builder.getTokenType()) {
      parseExpression(builder);
    }
    if (builder.getTokenType() != RIGHT_PAREN) {
      builder.error(ClojureBundle.message("expected.token", RIGHT_PAREN.toString()));
    } else {
      advanceLexerOrEOF(builder);
    }
    marker.done(DEFNDASH);
  }

  /**
   * Enter: Lexer is pointed at the opening left square
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseBindings(PsiBuilder builder) {
    if (builder.getTokenType() != LEFT_SQUARE) internalError(ClojureBundle.message("expected.lsquare"));

    PsiBuilder.Marker marker = markAndAdvance(builder);
    for (IElementType token = builder.getTokenType(); token != RIGHT_SQUARE && token != null; token = builder.getTokenType()) {
      parseExpression(builder); // variables being defined, values or metadata
    }
    advanceLexerOrEOF(builder);
    marker.done(BINDINGS);
  }

  /**
   * Enter: Lexer is pointed after the 'let'
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseList(PsiBuilder builder, PsiBuilder.Marker marker) {
    parseExpressions(RIGHT_PAREN, builder);
    marker.done(LIST);
  }
}
