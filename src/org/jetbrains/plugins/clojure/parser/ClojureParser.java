package org.jetbrains.plugins.clojure.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import static org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes.*;
import static org.jetbrains.plugins.clojure.parser.ClojureElementTypes.*;
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
public class ClojureParser implements PsiParser {

    @NotNull
    public ASTNode parse(IElementType root, PsiBuilder builder) {
        //builder.setDebugMode(true);
        PsiBuilder.Marker marker = builder.mark();
        for (IElementType token = builder.getTokenType(); token != null && token != null; token = builder.getTokenType()) {
            parseTopExpression(builder);
        }
        marker.done(FILE);
        return builder.getTreeBuilt();
    }

    /**
     * Enter: Lexer is pointed at the left paren
     * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
     */
    private void parseTopExpression(PsiBuilder builder) {

        IElementType token = builder.getTokenType();
        if (LEFT_PAREN == token) {
            parseTopList(builder);
        } else if (LEFT_SQUARE == token) {
            parseVector(builder);
        } else if (LEFT_CURLY == token) {
            parseMap(builder);
        } else if (QUOTE == token) {
            parseQuote(builder);
        } else if (POUND == token) {
            parsePound(builder);
        } else if (UP == token) {
            parseUp(builder);
        } else if (POUNDUP == token) {
            parsePoundUp(builder);
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
            syntaxError(builder, "Expected Left Paren, Symbol or Literal");
        }
    }

    private void parseTopList(PsiBuilder builder) {
        if (builder.getTokenType() != LEFT_PAREN) internalError("Expected (");
        PsiBuilder.Marker marker = markAndAdvance(builder);
        if (builder.getTokenType() == SYMBOL && builder.getTokenText().equals("def")) {
            parseDef(builder, marker);
        } else if (builder.getTokenType() == SYMBOL && builder.getTokenText().equals("defn")) {
            parseDefn(builder, marker);
        } else if (builder.getTokenType() == SYMBOL && builder.getTokenText().equals("defn-")) {
            parseDefnDash(builder, marker);
        } else {
            parseExpressions(RIGHT_PAREN, builder);
            marker.done(TOPLIST);
        }
    }

    private void parseExpressions(IElementType endToken, PsiBuilder builder) {
        for (IElementType token = builder.getTokenType(); token != endToken && token != null; token = builder.getTokenType()) {
            parseExpression(builder);
        }
        builder.advanceLexer();
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
            parseQuote(builder);
        } else if (BACKQUOTE == token) {
            parseBackQuote(builder);
        } else if (POUND == token) {
            parsePound(builder);
        } else if (UP == token) {
            parseUp(builder);
        } else if (POUNDUP == token) {
            parsePoundUp(builder);
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
            syntaxError(builder, "Expected Left Paren, Symbol or Literal");
        }
    }

    private void syntaxError(PsiBuilder builder, String msg) {
        String e = msg + ": " + builder.getTokenText();
        //System.out.println(e);
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
     */
    private void parseSymbol(PsiBuilder builder) {
        markAndAdvance(builder, VARIABLE);
    }

    /**
     * Enter: Lexer is pointed at symbol
     * Exit: Lexer is pointed immediately after symbol
     */
    private void parseKey(PsiBuilder builder) {
        markAndAdvance(builder, KEY);
    }

    /**
     * Enter: Lexer is pointed at literal
     * Exit: Lexer is pointed immediately after literal
     */
    private void parseLiteral(PsiBuilder builder) {
        markAndAdvance(builder, LITERAL);
    }

    /**
     * Enter: Lexer is pointed at '
     * Exit: Lexer is pointed immediately after quoted value
     */
    private void parseQuote(PsiBuilder builder) {
        if (builder.getTokenType() != QUOTE) internalError("Expected '");
        final PsiBuilder.Marker mark = builder.mark();
        builder.advanceLexer();
        parseExpression(builder);
        mark.done(QUOTED_EXPRESSION);
    }

    /**
     * Enter: Lexer is pointed at `
     * Exit: Lexer is pointed immediately after quoted value
     */
    private void parseBackQuote(PsiBuilder builder) {
        if (builder.getTokenType() != BACKQUOTE) internalError("Expected `");
        final PsiBuilder.Marker mark = builder.mark();
        builder.advanceLexer();
        parseExpression(builder);
        mark.done(BACKQUOTED_EXPRESSION);
    }

    /**
     * Enter: Lexer is pointed at #
     * Exit: Lexer is pointed immediately after closing }
     */
    private void parsePound(PsiBuilder builder) {
        if (builder.getTokenType() != POUND) internalError("Expected #");
        PsiBuilder.Marker mark = builder.mark();
        builder.advanceLexer();
        parseExpression(builder);
        mark.done(POUND_EXPRESSION);
    }

    /**
     * Enter: Lexer is pointed at ^
     * Exit: Lexer is pointed immediately after closing }
     */
    private void parseUp(PsiBuilder builder) {
        if (builder.getTokenType() != UP) internalError("Expected ^");
        PsiBuilder.Marker mark = builder.mark();
        builder.advanceLexer();
        parseExpression(builder);
        mark.done(UP_EXPRESSION);
    }

    /**
     * Enter: Lexer is pointed at ^
     * Exit: Lexer is pointed immediately after closing }
     */
    private void parsePoundUp(PsiBuilder builder) {
        if (builder.getTokenType() != POUNDUP) internalError("Expected #^");
        PsiBuilder.Marker mark = builder.mark();
        builder.advanceLexer();
        parseExpression(builder);
        mark.done(POUNDUP_EXPRESSION);
    }

    /**
     * Enter: Lexer is pointed at ~
     * Exit: Lexer is pointed immediately after closing }
     */
    private void parseTilda(PsiBuilder builder) {
        if (builder.getTokenType() != TILDA) internalError("Expected ~");
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
        if (builder.getTokenType() != AT) internalError("Expected @");
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
        if (builder.getTokenType() != TILDAAT) internalError("Expected ~@");
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
        if (builder.getTokenType() != LEFT_CURLY) internalError("Expected {");
        PsiBuilder.Marker marker = markAndAdvance(builder);
        for (IElementType token = builder.getTokenType(); token != RIGHT_CURLY && token != null; token = builder.getTokenType()) {
            parseExpression(builder); // key
            parseExpression(builder); // value
        }
        advanceLexerOrEOF(builder);
        marker.done(MAP);
    }

    /**
     * Enter: Lexer is pointed at the def
     * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
     */
    private void parseDef(PsiBuilder builder, PsiBuilder.Marker marker) {
        if (builder.getTokenType() != SYMBOL || !builder.getTokenText().equals("def")) internalError("Expected element");

        advanceLexerOrEOF(builder);
        for (IElementType token = builder.getTokenType(); token != RIGHT_PAREN && token != null; token = builder.getTokenType()) {
            parseExpression(builder);
        }
        advanceLexerOrEOF(builder);
        marker.done(DEF);
    }

    /**
     * Enter: Lexer is pointed at the defn
     * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
     */
    private void parseDefn(PsiBuilder builder, PsiBuilder.Marker marker) {
        if (builder.getTokenType() != SYMBOL || !builder.getTokenText().equals("defn")) internalError("Expected defn");

        advanceLexerOrEOF(builder);
        for (IElementType token = builder.getTokenType(); token != RIGHT_PAREN && token != null; token = builder.getTokenType()) {
            parseExpression(builder);
        }
        advanceLexerOrEOF(builder);
        marker.done(DEFN);
    }

    /**
     * Enter: Lexer is pointed at the defn-
     * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
     */
    private void parseDefnDash(PsiBuilder builder, PsiBuilder.Marker marker) {
        if (builder.getTokenType() != SYMBOL || !builder.getTokenText().equals("defn-"))
            internalError("Expected defn-");

        advanceLexerOrEOF(builder);
        for (IElementType token = builder.getTokenType(); token != RIGHT_PAREN && token != null; token = builder.getTokenType()) {
            parseExpression(builder);
        }
        advanceLexerOrEOF(builder);
        marker.done(DEFNDASH);
    }

    /**
     * Enter: Lexer is pointed at the opening left square
     * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
     */
    private void parseBindings(PsiBuilder builder) {
        if (builder.getTokenType() != LEFT_SQUARE) internalError("Expected [");

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
