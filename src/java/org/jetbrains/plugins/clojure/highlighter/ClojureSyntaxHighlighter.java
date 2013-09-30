package org.jetbrains.plugins.clojure.highlighter;

import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.lexer.Lexer;
import com.intellij.lexer.LookAheadLexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.lexer.ClojureFlexLexer;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.plugins.clojure.parser.ClojureElementType;
import org.jetbrains.plugins.clojure.parser.ClojureElementTypes;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * User: peter
 * Date: Dec 8, 2008
 * Time: 9:00:27 AM
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
public class ClojureSyntaxHighlighter extends SyntaxHighlighterBase implements ClojureTokenTypes {
  private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

  public static final IElementType FIRST_LIST_ELEMENT = new ClojureElementType("first list element");
  
  @NotNull
  public Lexer getHighlightingLexer() {
    return new LookAheadLexer(new ClojureFlexLexer()) {
      @Override
      protected void lookAhead(Lexer baseLexer) {
        if (baseLexer.getTokenType() == ClojureElementTypes.LEFT_PAREN) {
          advanceAs(baseLexer, ClojureElementTypes.LEFT_PAREN);
          if (baseLexer.getTokenType() == ClojureElementTypes.symATOM) {
            advanceAs(baseLexer, FIRST_LIST_ELEMENT);
          }
        } else {
          super.lookAhead(baseLexer);
        }
      }
    };
  }

  @NotNull
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return pack(ATTRIBUTES.get(tokenType));
  }

  @NonNls
  public static final String LINE_COMMENT_ID = "Clojure Line comment";
  @NonNls
  static final String KEY_ID = "Clojure Keyword";
  @NonNls
  static final String DEF_ID = "First symbol in list";
  @NonNls
  static final String ATOM_ID = "Clojure Atom";
  @NonNls
  static final String NUMBER_ID = "Clojure Numbers";
  @NonNls
  static final String STRING_ID = "Clojure Strings";
  @NonNls
  static final String BAD_CHARACTER_ID = "Bad character";
  @NonNls
  static final String BRACES_ID = "Clojure Braces";
  @NonNls
  static final String PAREN_ID = "Clojure Parentheses";
  @NonNls
  static final String LITERAL_ID = "Clojure Literal";
  @NonNls
  static final String CHAR_ID = "Clojure Character";

  public static final TextAttributes ATOM_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();

  static {
    TextAttributesKey.createTextAttributesKey(LINE_COMMENT_ID, DefaultLanguageHighlighterColors.LINE_COMMENT);
    TextAttributesKey.createTextAttributesKey(KEY_ID, HighlightInfoType.STATIC_FIELD.getAttributesKey());
    TextAttributesKey.createTextAttributesKey(DEF_ID, DefaultLanguageHighlighterColors.KEYWORD);
    TextAttributesKey.createTextAttributesKey(NUMBER_ID, DefaultLanguageHighlighterColors.NUMBER);
    TextAttributesKey.createTextAttributesKey(STRING_ID, DefaultLanguageHighlighterColors.STRING);
    TextAttributesKey.createTextAttributesKey(BRACES_ID, DefaultLanguageHighlighterColors.BRACES);
    TextAttributesKey.createTextAttributesKey(PAREN_ID, DefaultLanguageHighlighterColors.PARENTHESES);
    TextAttributesKey.createTextAttributesKey(LITERAL_ID, DefaultLanguageHighlighterColors.KEYWORD);
    TextAttributesKey.createTextAttributesKey(CHAR_ID, DefaultLanguageHighlighterColors.STRING);
    TextAttributesKey.createTextAttributesKey(BAD_CHARACTER_ID, HighlighterColors.BAD_CHARACTER);

    final Color deepBlue = DefaultLanguageHighlighterColors.KEYWORD.getDefaultAttributes().getForegroundColor();
    ATOM_ATTRIB.setForegroundColor(deepBlue);
    TextAttributesKey.createTextAttributesKey(ATOM_ID, ATOM_ATTRIB);
  }

  public static final TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey(LINE_COMMENT_ID);
  public static final TextAttributesKey KEY = TextAttributesKey.createTextAttributesKey(KEY_ID);
  public static final TextAttributesKey DEF = TextAttributesKey.createTextAttributesKey(DEF_ID);
  public static final TextAttributesKey ATOM = TextAttributesKey.createTextAttributesKey(ATOM_ID);
  public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey(NUMBER_ID);
  public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(STRING_ID);
  public static final TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey(BRACES_ID);
  public static final TextAttributesKey PARENTS = TextAttributesKey.createTextAttributesKey(PAREN_ID);
  public static final TextAttributesKey LITERAL = TextAttributesKey.createTextAttributesKey(LITERAL_ID);
  public static final TextAttributesKey CHAR = TextAttributesKey.createTextAttributesKey(CHAR_ID);
  public static final TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(BAD_CHARACTER_ID);


  static {
    fillMap(ATTRIBUTES, LINE_COMMENT, ClojureTokenTypes.LINE_COMMENT);
    fillMap(ATTRIBUTES, KEY, ClojureTokenTypes.COLON_SYMBOL);
    fillMap(ATTRIBUTES, symS, ATOM);
    fillMap(ATTRIBUTES, NUMBER, LONG_LITERAL, BIG_INT_LITERAL, DOUBLE_LITERAL, BIG_DECIMAL_LITERAL, RATIO);
    fillMap(ATTRIBUTES, ClojureTokenTypes.STRINGS, STRING);
    fillMap(ATTRIBUTES, BRACES, 
        ClojureTokenTypes.LEFT_SQUARE, ClojureTokenTypes.RIGHT_SQUARE,
        ClojureTokenTypes.LEFT_CURLY, ClojureTokenTypes.RIGHT_CURLY);
    fillMap(ATTRIBUTES, PARENTS, ClojureTokenTypes.LEFT_PAREN, ClojureTokenTypes.RIGHT_PAREN);
    fillMap(ATTRIBUTES, LITERAL, ClojureTokenTypes.TRUE, ClojureTokenTypes.FALSE, ClojureTokenTypes.NIL);
    fillMap(ATTRIBUTES, CHAR, ClojureTokenTypes.CHAR_LITERAL);
    fillMap(ATTRIBUTES, DEF, FIRST_LIST_ELEMENT);
  }

}
