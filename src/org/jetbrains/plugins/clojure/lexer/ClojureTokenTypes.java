package org.jetbrains.plugins.clojure.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.plugins.clojure.parser.ClojureElementType;

/**
 * User: peter
 * Date: Nov 20, 2008
 * Time: 1:50:48 PM
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
public interface ClojureTokenTypes {

  // Special characters
  IElementType LEFT_PAREN = new ClojureElementType("(");
  IElementType RIGHT_PAREN = new ClojureElementType(")");

  IElementType LEFT_CURLY = new ClojureElementType("{");
  IElementType RIGHT_CURLY = new ClojureElementType("}");

  IElementType LEFT_SQUARE = new ClojureElementType("[");
  IElementType RIGHT_SQUARE = new ClojureElementType("]");

  IElementType SHARP = new ClojureElementType("#");
  IElementType UP = new ClojureElementType("^");
  IElementType SHARPUP = new ClojureElementType("#^");
  IElementType TILDA = new ClojureElementType("~");
  IElementType AT = new ClojureElementType("@");
  IElementType TILDAAT = new ClojureElementType("~@");
  IElementType QUOTE = new ClojureElementType("'");
  IElementType BACKQUOTE = new ClojureElementType("`");
  // Comments
  IElementType LINE_COMMENT = new ClojureElementType("line comment");

  TokenSet COMMENTS = TokenSet.create(LINE_COMMENT);
  // Literals
  IElementType STRING_LITERAL = new ClojureElementType("string literal");
  IElementType WRONG_STRING_LITERAL = new ClojureElementType("wrong string literal");

  IElementType LONG_LITERAL = new ClojureElementType("long literal");
  IElementType BIG_INT_LITERAL = new ClojureElementType("big integer literal");
  IElementType DOUBLE_LITERAL = new ClojureElementType("double literal");
  IElementType BIG_DECIMAL_LITERAL = new ClojureElementType("big deciamel literal");
  IElementType RATIO = new ClojureElementType("ratio literal");

  IElementType CHAR_LITERAL = new ClojureElementType("character literal");
  IElementType NIL = new ClojureElementType("nil");

  IElementType TRUE = new ClojureElementType("true");
  IElementType FALSE = new ClojureElementType("false");
  TokenSet BOOLEAN_LITERAL = TokenSet.create(TRUE, FALSE, NIL);
  TokenSet LITERALS = TokenSet.create(STRING_LITERAL, WRONG_STRING_LITERAL,
      LONG_LITERAL, BIG_INT_LITERAL, DOUBLE_LITERAL, BIG_DECIMAL_LITERAL, RATIO,
      CHAR_LITERAL, TRUE, FALSE, NIL);

  TokenSet READABLE_TEXT = TokenSet.create(STRING_LITERAL, LINE_COMMENT, WRONG_STRING_LITERAL);

  IElementType COLON_SYMBOL = new ClojureElementType("key");  // :foo
  // Symbol parts
  IElementType symATOM = new ClojureElementType("atom"); // foo
  IElementType symDOT = new ClojureElementType("dot"); // foo
  IElementType symNS_SEP = new ClojureElementType("ns-sep"); // foo
  IElementType symIMPLICIT_ARG = new ClojureElementType("implicit function argument");

  // Control characters
  IElementType EOL = new ClojureElementType("end of line");
  IElementType EOF = new ClojureElementType("end of file");
  IElementType WHITESPACE = TokenType.WHITE_SPACE;
  IElementType COMMA = new ClojureElementType(",");
  IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;


  // Useful token sets
  TokenSet WHITESPACE_SET = TokenSet.create(EOL, EOF, WHITESPACE, COMMA);
  TokenSet symS = TokenSet.create(symATOM,  symDOT, symNS_SEP, symIMPLICIT_ARG);
  TokenSet ATOMS = TokenSet.create(symATOM,  symDOT, symNS_SEP);
  TokenSet SEPARATORS = TokenSet.create(symDOT, symNS_SEP);

  TokenSet IDENTIFIERS = TokenSet.create(symATOM);
  TokenSet KEYWORDS = TokenSet.create(NIL, TRUE, FALSE);
  TokenSet STRINGS = TokenSet.create(STRING_LITERAL, WRONG_STRING_LITERAL);
}
