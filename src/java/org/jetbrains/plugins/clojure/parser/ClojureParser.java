package org.jetbrains.plugins.clojure.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.HashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import static org.jetbrains.plugins.clojure.parser.ClojureElementTypes.*;
import org.jetbrains.plugins.clojure.parser.util.ParserUtils;
import static org.jetbrains.plugins.clojure.parser.ClojureSpecialFormTokens.DEF_TOKENS;

import java.util.Arrays;
import java.util.Set;


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
public class ClojureParser implements PsiParser, ClojureTokenTypes {

  private static final String CREATE_NS = "create-ns";
  private static final String IN_NS = "in-ns";
  private static final String NS = "ns";
  public static final Set<String> NS_TOKENS = new HashSet<String>();

  static {
    NS_TOKENS.addAll(Arrays.asList(NS, IN_NS, CREATE_NS));
  }

  @NotNull
  public ASTNode parse(IElementType root, PsiBuilder builder) {
    //builder.setDebugMode(true);
    PsiBuilder.Marker marker = builder.mark();
    for (IElementType token = builder.getTokenType(); token != null; token = builder.getTokenType()) {
      parseExpression(builder);
    }
    marker.done(FILE);
    return builder.getTreeBuilt();
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
    } else if (ParserUtils.lookAhead(builder, SHARP, LEFT_CURLY)) {
      parseSet(builder);
    } else if (SHARP == token) {
      parseSharp(builder);
    } else if (UP == token) {
      parseUp(builder);
    } else if (SHARPUP == token) {
      parseMetadata(builder);
    } else if (TILDA == token) {
      parseTilda(builder);
    } else if (AT == token) {
      parseAt(builder);
    } else if (TILDAAT == token) {
      parseTildaAt(builder);
    } else if (symS.contains(token)) {
      parseSymbol(builder);
    } else if (COLON_SYMBOL == token) {
      parseKeyword(builder);
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
    final PsiBuilder.Marker marker = builder.mark();
    //parse implicit
    if (builder.getTokenType() == symIMPLICIT_ARG) {
      builder.advanceLexer();
      marker.done(IMPLICIT_ARG);
      return;
    }
    builder.advanceLexer(); // eat atom
    if (SEPARATORS.contains(builder.getTokenType())) {
      final PsiBuilder.Marker pred = marker.precede();
      marker.done(SYMBOL);
      parseSymbol1(builder, pred);
    } else {
      marker.done(SYMBOL);
    }
  }

  private void parseSymbol1(PsiBuilder builder, PsiBuilder.Marker marker) {
    if (SEPARATORS.contains(builder.getTokenType())) {
      builder.advanceLexer(); //eat separator
      if (builder.getTokenType() == symATOM) {
        builder.advanceLexer(); //eat atom
      }
      if (SEPARATORS.contains(builder.getTokenType())) {
        final PsiBuilder.Marker pred = marker.precede();
        marker.done(SYMBOL);
        parseSymbol1(builder, pred);
      } else {
        marker.done(SYMBOL);
      }
    } else {
      marker.drop();
    }
  }

  /**
   * Enter: Lexer is pointed at symbol
   * Exit: Lexer is pointed immediately after symbol
   *
   * @param builder
   */
  private void parseKeyword(PsiBuilder builder) {
    markAndAdvance(builder, KEYWORD);
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
    if (!ParserUtils.lookAhead(builder, SHARP, LEFT_CURLY)) {
      internalError(ClojureBundle.message("expected.sharp.lcurly"));
    }
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    assert builder.getTokenType() != null;
    builder.eof();
    builder.advanceLexer();
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
    if (builder.getTokenType() != LEFT_PAREN) internalError(ClojureBundle.message("expected.lparen"));
    PsiBuilder.Marker marker = markAndAdvance(builder);
    final String tokenText = builder.getTokenText();
    if (builder.getTokenType() == symATOM && DEF_TOKENS.contains(tokenText)) {
      parseDef(builder, marker);
    } else if (builder.getTokenType() == symATOM && NS_TOKENS.contains(tokenText)) {
      parseNs(builder, marker);
    } else {
      parseExpressions(RIGHT_PAREN, builder);
      marker.done(LIST);
    }
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
    final String text = builder.getTokenText();
    if (!DEF_TOKENS.contains(text) || builder.getTokenType() != symATOM) {
      internalError(ClojureBundle.message("expected.element"));
    }

    parseSymbol(builder);
    for (IElementType token = builder.getTokenType(); token != RIGHT_PAREN && token != null; token = builder.getTokenType()) {
      parseExpression(builder);
    }

    if (builder.getTokenType() != RIGHT_PAREN) {
      builder.error(ClojureBundle.message("expected.token", RIGHT_PAREN.toString()));
    } else {
      advanceLexerOrEOF(builder);
    }
    marker.done("defmethod".equals(text) ? ClojureElementTypes.DEFMETHOD : ClojureElementTypes.DEF);
  }

  private void parseNs(PsiBuilder builder, PsiBuilder.Marker marker) {
    final String text = builder.getTokenText();
    if (!NS_TOKENS.contains(text) || builder.getTokenType() != symATOM) {
      internalError(ClojureBundle.message("expected.element"));
    }

    parseSymbol(builder);
    for (IElementType token = builder.getTokenType(); token != RIGHT_PAREN && token != null; token = builder.getTokenType()) {
      parseExpression(builder);
    }

    if (builder.getTokenType() != RIGHT_PAREN) {
      builder.error(ClojureBundle.message("expected.token", RIGHT_PAREN.toString()));
    } else {
      advanceLexerOrEOF(builder);
    }
    if (CREATE_NS.equals(text)) marker.done(ClojureElementTypes.CREATE_NS);
    else if (IN_NS.equals(text)) marker.done(ClojureElementTypes.IN_NS);
    else marker.done(ClojureElementTypes.NS);
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
