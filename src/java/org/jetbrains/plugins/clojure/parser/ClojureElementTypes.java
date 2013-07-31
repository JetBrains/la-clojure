package org.jetbrains.plugins.clojure.parser;

import com.intellij.psi.stubs.EmptyStub;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.plugins.clojure.psi.ClStubElementType;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.impl.list.ClListImpl;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClDefStub;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClKeywordStub;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClNsStub;
import org.jetbrains.plugins.clojure.psi.stubs.elements.*;
import org.jetbrains.plugins.clojure.psi.stubs.elements.ns.ClCreateNsElementType;
import org.jetbrains.plugins.clojure.psi.stubs.elements.ns.ClInNsElementType;
import org.jetbrains.plugins.clojure.psi.stubs.elements.ns.ClNsElementType;

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
  final IStubFileElementType FILE = new ClStubFileElementType();

  final IElementType TOPLIST = new ClojureElementType("toplist");
  final ClStubElementType<EmptyStub, ClListImpl> LIST = new ClListElementType();
  final IElementType VECTOR = new ClojureElementType("vector");
  final IElementType MAP = new ClojureElementType("map");
  final IElementType SET = new ClojureElementType("map");

  final ClStubElementType<ClDefStub, ClDef> DEF = new ClDefElementType();
  final ClStubElementType<ClDefStub, ClDef> DEFMETHOD = new ClDefMethodElementType();
  final ClStubElementType<ClKeywordStub, ClKeyword> KEYWORD = new ClKeywordElementType();

  final ClStubElementType<ClNsStub, ClNs> NS = new ClNsElementType();
  final ClStubElementType<ClNsStub, ClNs> IN_NS = new ClInNsElementType();
  final ClStubElementType<ClNsStub, ClNs> CREATE_NS = new ClCreateNsElementType();

  final IElementType MAP_ENTRY = new ClojureElementType("map");
  final IElementType LITERAL = new ClojureElementType("literal");
  final IElementType SYMBOL = new ClojureElementType("symbol");
  final IElementType IMPLICIT_ARG = new ClojureElementType("function argument");

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


  TokenSet LIST_LIKE_FORMS = TokenSet.create(LIST, VECTOR, MAP, SET, DEF, DEFMETHOD, NS, IN_NS, CREATE_NS);

  TokenSet BRACES = TokenSet.create(LEFT_CURLY, LEFT_PAREN, LEFT_SQUARE,
      RIGHT_CURLY, RIGHT_PAREN, RIGHT_SQUARE);

  TokenSet MODIFIERS = TokenSet.create(SHARP, UP, SHARPUP, TILDA, AT, TILDAAT, QUOTE, BACKQUOTE);
}
