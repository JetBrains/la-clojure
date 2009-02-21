package org.jetbrains.plugins.clojure.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;

/**
 * User: peter
 * Date: Nov 21, 2008
 * Time: 9:46:12 AM
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
public interface ClojureElementTypes extends ClojureTokenTypes {
  final IFileElementType FILE = new IFileElementType(ClojureFileType.CLOJURE_LANGUAGE);

  final IElementType TOPLIST = new ClojureElementType("toplist");
  final IElementType LIST = new ClojureElementType("list");
  final IElementType VECTOR = new ClojureElementType("vector");
  final IElementType MAP = new ClojureElementType("map");
  final IElementType SET = new ClojureElementType("map");


  final IElementType DEFN = new ClojureElementType("defn");
  final IElementType MAP_ENTRY = new ClojureElementType("map");
  final IElementType DEFNDASH = new ClojureElementType("defn-");
  final IElementType DEF = new ClojureElementType("element");

  final IElementType LITERAL = new ClojureElementType("literal");
  final IElementType VARIABLE = new ClojureElementType("variable");
  final IElementType KEY = new ClojureElementType("key definition");

  final IElementType BINDINGS = new ClojureElementType("bindings");
  final IElementType REST = new ClojureElementType("rest");
  final IElementType AS = new ClojureElementType("as");

  final IElementType EXPRESSION = new ClojureElementType("expression");
  final IElementType QUOTED_FORM = new ClojureElementType("quoted expression");
  final IElementType BACKQUOTED_EXPRESSION = new ClojureElementType("backquoted expression");

  final IElementType SHARP_EXPRESSION = new ClojureElementType("pound expression");
  final IElementType META_FORM = new ClojureElementType("up expression");
  final IElementType METADATA = new ClojureElementType("poundup expression");
  final IElementType TILDA_EXPRESSION = new ClojureElementType("tilda expression");
  final IElementType AT_EXPRESSION = new ClojureElementType("at expression");
  final IElementType TILDAAT_EXPRESSION = new ClojureElementType("tildaat expression");


  TokenSet LIST_LIKE_FORMS = TokenSet.create(LIST,
      TOPLIST,
      VECTOR,
      MAP,
      SET);

  TokenSet BRACES = TokenSet.create(LEFT_CURLY, LEFT_PAREN, LEFT_SQUARE,
      RIGHT_CURLY, RIGHT_PAREN, RIGHT_SQUARE);

  TokenSet MODIFIERS = TokenSet.create(
      SHARP,
      UP,
      SHARPUP,
      SHARP_CURLY,
      TILDA,
      AT,
      TILDAAT,
      PERCENT,
      QUOTE,
      BACKQUOTE
  );
}
