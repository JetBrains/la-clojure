package org.jetbrains.plugins.clojure.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

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
public class ClojureElementTypes {
  public static final IFileElementType FILE = new IFileElementType(ClojureFileType.CLOJURE_LANGUAGE);

  public static final IElementType TOPLIST = new ClojureElementType("toplist");
  public static final IElementType LIST = new ClojureElementType("list");
  public static final IElementType VECTOR = new ClojureElementType("vector");
  public static final IElementType MAP = new ClojureElementType("map");
  public static final IElementType SET = new ClojureElementType("map");


  public static final IElementType DEFN = new ClojureElementType("defn");
  public static final IElementType MAP_ENTRY = new ClojureElementType("map");
  public static final IElementType DEFNDASH = new ClojureElementType("defn-");
  public static final IElementType DEF = new ClojureElementType("element");

  public static final IElementType LITERAL = new ClojureElementType("literal");
  public static final IElementType VARIABLE = new ClojureElementType("variable");
  public static final IElementType KEY = new ClojureElementType("key definition");

  public static final IElementType BINDINGS = new ClojureElementType("bindings");
  public static final IElementType REST = new ClojureElementType("rest");
  public static final IElementType AS = new ClojureElementType("as");

  public static final IElementType EXPRESSION = new ClojureElementType("expression");
  public static final IElementType QUOTED_FORM = new ClojureElementType("quoted expression");
  public static final IElementType BACKQUOTED_EXPRESSION = new ClojureElementType("backquoted expression");

  public static final IElementType SHARP_EXPRESSION = new ClojureElementType("pound expression");
  public static final IElementType META_FORM = new ClojureElementType("up expression");
  public static final IElementType METADATA = new ClojureElementType("poundup expression");
  public static final IElementType TILDA_EXPRESSION = new ClojureElementType("tilda expression");
  public static final IElementType AT_EXPRESSION = new ClojureElementType("at expression");
  public static final IElementType TILDAAT_EXPRESSION = new ClojureElementType("tildaat expression");
}
