package org.jetbrains.plugins.clojure.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.ClojureIcons;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * User: peter
 * Date: Dec 18, 2008
 * Time: 5:05:07 PM
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
public class ClojureColorsAndFontsPage implements ColorSettingsPage {
  @NotNull
  public String getDisplayName() {
    return "Clojure";
  }

  @Nullable
  public Icon getIcon() {
    return ClojureIcons.CLOJURE_ICON_16x16;
  }

  @NotNull
  public AttributesDescriptor[] getAttributeDescriptors() {
    return ATTRS;
  }

  private static final AttributesDescriptor[] ATTRS =
      new AttributesDescriptor[]{
          new AttributesDescriptor(ClojureSyntaxHighlighter.LINE_COMMENT_ID, ClojureSyntaxHighlighter.LINE_COMMENT),
          new AttributesDescriptor(ClojureSyntaxHighlighter.ATOM_ID, ClojureSyntaxHighlighter.ATOM),
          new AttributesDescriptor(ClojureSyntaxHighlighter.KEY_ID, ClojureSyntaxHighlighter.KEY),
          new AttributesDescriptor(ClojureSyntaxHighlighter.NUMBER_ID, ClojureSyntaxHighlighter.NUMBER),
          new AttributesDescriptor(ClojureSyntaxHighlighter.STRING_ID, ClojureSyntaxHighlighter.STRING),
          new AttributesDescriptor(ClojureSyntaxHighlighter.BRACES_ID, ClojureSyntaxHighlighter.BRACES),
          new AttributesDescriptor(ClojureSyntaxHighlighter.PAREN_ID, ClojureSyntaxHighlighter.PARENTS),
          new AttributesDescriptor(ClojureSyntaxHighlighter.BAD_CHARACTER_ID, ClojureSyntaxHighlighter.BAD_CHARACTER),
          new AttributesDescriptor(ClojureSyntaxHighlighter.CHAR_ID, ClojureSyntaxHighlighter.CHAR),
          new AttributesDescriptor(ClojureSyntaxHighlighter.LITERAL_ID, ClojureSyntaxHighlighter.LITERAL),
          new AttributesDescriptor(ClojureSyntaxHighlighter.CALL_OPERATOR_ID, ClojureSyntaxHighlighter.CALL_OPERATOR),
          new AttributesDescriptor(ClojureSyntaxHighlighter.GLOBAL_VAR_NAME_DECLARATION_ID, ClojureSyntaxHighlighter.GLOBAL_VAR_NAME_DECLARATION),
      };

  @NotNull
  public ColorDescriptor[] getColorDescriptors() {
    return new ColorDescriptor[0];
  }

  @NotNull
  public SyntaxHighlighter getHighlighter() {
    return new ClojureSyntaxHighlighter();
  }

  @NonNls
  @NotNull
  public String getDemoText() {
    return "; Example from Clojure Special Forms http://clojure.org/special_forms\n" +
        "; \n" +
        "\n" +
        "(<callOperator>defn</callOperator>\n" +
        "#^{:doc \"mymax [xs+] gets the maximum value in xs using > \"\n" +
        "   :test (<callOperator>fn</callOperator> []\n" +
        "             (<callOperator>assert</callOperator> (<callOperator>=</callOperator> 42  (<callOperator>max</callOperator> 2 42 5 4))))\n" +
        "   :user/comment \"this is the best fn ever!\"}\n" +
        "  <symbolDeclaration>mymax</symbolDeclaration>\n" +
        "  ([x] x)\n" +
        "  ([x y] (<callOperator>if</callOperator> (<callOperator>></callOperator> x y) x y))\n" +
        "  ([x y & nil]\n" +
        "   (<callOperator>reduce</callOperator> mymax (<callOperator>mymax</callOperator> x y) more {\\tab \"  \"})))\n" +
        "\n" +
        "(<callOperator>intern</callOperator> 'user '<symbolDeclaration>x</symbolDeclaration> \"Foobar\")\n" +
        "\n" +
        "(<callOperator>defprotocol</callOperator> <symbolDeclaration>AProtocol</symbolDeclaration> \n" +
        "  \"A doc string for AProtocol abstraction\"\n" +
        "  (<callOperator>bar</callOperator> [a b] \"bar docs\")\n" +
        "  (<callOperator>baz</callOperator> [a] [a b] [a b c] \"baz docs\"))\n" +
        "\n" +
        "(<callOperator>defrecord</callOperator> <symbolDeclaration>ARecord</symbolDeclaration> [arg1 arg2]\n" +
        "  AProtocol (<callOperator>bar</callOperator> [a b] (<callOperator>+</callOperator> a arg1)))";
  }

  @Nullable
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    Map<String, TextAttributesKey> map = new HashMap<String, TextAttributesKey>();
    map.put("callOperator", ClojureSyntaxHighlighter.CALL_OPERATOR);
    map.put("symbolDeclaration", ClojureSyntaxHighlighter.GLOBAL_VAR_NAME_DECLARATION);
    return map;
  }
}
