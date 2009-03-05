package org.jetbrains.plugins.clojure.highlighter;

import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.lexer.ClojureFlexLexer;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;

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


  static final TokenSet sNUMBERS = TokenSet.create(
      INTEGER_LITERAL, LONG_LITERAL, BIG_INT_LITERAL, FLOAT_LITERAL, DOUBLE_LITERAL, BIG_DECIMAL_LITERAL, RATIO
  );

  static final TokenSet sLINE_COMMENTS = TokenSet.create(
      ClojureTokenTypes.LINE_COMMENT
  );

  static final TokenSet sBAD_CHARACTERS = TokenSet.create(
      ClojureTokenTypes.BAD_CHARACTER
  );

  static final TokenSet sLITERALS = TokenSet.create(ClojureTokenTypes.TRUE, ClojureTokenTypes.FALSE, ClojureTokenTypes.NIL);

  static final TokenSet sSTRINGS = ClojureTokenTypes.STRINGS;

  static final TokenSet sCHARS = TokenSet.create(ClojureTokenTypes.CHAR_LITERAL);

  static final TokenSet sPARENTS = TokenSet.create(
      ClojureTokenTypes.LEFT_PAREN,
      ClojureTokenTypes.RIGHT_PAREN
  );

  static final TokenSet sBRACES = TokenSet.create(
      ClojureTokenTypes.LEFT_SQUARE,
      ClojureTokenTypes.RIGHT_SQUARE,
      ClojureTokenTypes.LEFT_CURLY,
      ClojureTokenTypes.RIGHT_CURLY
  );

  public static final TokenSet sATOMS = symS;

  public static final TokenSet sKEYS = TokenSet.create(
      ClojureTokenTypes.COLON_SYMBOL
  );

  @NotNull
  public Lexer getHighlightingLexer() {
    return new ClojureFlexLexer();
  }

  @NotNull
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return pack(ATTRIBUTES.get(tokenType));
  }

  @NonNls
  static final String LINE_COMMENT_ID = "Clojure Line comment";
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


  // Registering TextAttributes
  static {
    TextAttributesKey.createTextAttributesKey(LINE_COMMENT_ID, SyntaxHighlighterColors.LINE_COMMENT.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(KEY_ID, HighlightInfoType.STATIC_FIELD.getAttributesKey().getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(DEF_ID, SyntaxHighlighterColors.KEYWORD.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(NUMBER_ID, SyntaxHighlighterColors.NUMBER.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(STRING_ID, SyntaxHighlighterColors.STRING.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(BRACES_ID, SyntaxHighlighterColors.BRACES.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(PAREN_ID, SyntaxHighlighterColors.PARENTHS.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(LITERAL_ID, SyntaxHighlighterColors.KEYWORD.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(CHAR_ID, SyntaxHighlighterColors.STRING.getDefaultAttributes());
    TextAttributesKey.createTextAttributesKey(BAD_CHARACTER_ID, HighlighterColors.BAD_CHARACTER.getDefaultAttributes());

    final Color deepBlue = SyntaxHighlighterColors.KEYWORD.getDefaultAttributes().getForegroundColor();
    ATOM_ATTRIB.setForegroundColor(deepBlue);
    TextAttributesKey.createTextAttributesKey(ATOM_ID, ATOM_ATTRIB);
  }

  public static TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey(LINE_COMMENT_ID);
  public static TextAttributesKey KEY = TextAttributesKey.createTextAttributesKey(KEY_ID);
  public static TextAttributesKey DEF = TextAttributesKey.createTextAttributesKey(DEF_ID);
  public static TextAttributesKey ATOM = TextAttributesKey.createTextAttributesKey(ATOM_ID);
  public static TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey(NUMBER_ID);
  public static TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(STRING_ID);
  public static TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey(BRACES_ID);
  public static TextAttributesKey PARENTS = TextAttributesKey.createTextAttributesKey(PAREN_ID);
  public static TextAttributesKey LITERAL = TextAttributesKey.createTextAttributesKey(LITERAL_ID);
  public static TextAttributesKey CHAR = TextAttributesKey.createTextAttributesKey(CHAR_ID);
  public static TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(BAD_CHARACTER_ID);


  static {
    fillMap(ATTRIBUTES, sLINE_COMMENTS, LINE_COMMENT);
    fillMap(ATTRIBUTES, sKEYS, KEY);
    fillMap(ATTRIBUTES, sATOMS, ATOM);
    fillMap(ATTRIBUTES, sNUMBERS, NUMBER);
    fillMap(ATTRIBUTES, sSTRINGS, STRING);
    fillMap(ATTRIBUTES, sBRACES, BRACES);
    fillMap(ATTRIBUTES, sPARENTS, PARENTS);
    fillMap(ATTRIBUTES, sLITERALS, LITERAL);
    fillMap(ATTRIBUTES, sCHARS, CHAR);
  }

}
