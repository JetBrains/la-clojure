package org.jetbrains.plugins.clojure;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                    new AttributesDescriptor(ClojureSyntaxHighlighter.BAD_CHARACTER_ID, ClojureSyntaxHighlighter.BAD_CHARACTER),
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
                "(defn\n" +
                "#^{:doc \"mymax [xs+] gets the maximum value in xs using > \"\n" +
                "   :test (fn []\n" +
                "             (assert (= 42  (max 2 42 5 4))))\n" +
                "   :user/comment \"this is the best fn ever!\"}\n" +
                "  mymax\n" +
                "  ([x] x)\n" +
                "  ([x y] (if (> x y) x y))\n" +
                "  ([x y & more]\n" +
                "   (reduce mymax (mymax x y) more)))";
    }

    @Nullable
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        Map<String, TextAttributesKey> map = new HashMap<String, TextAttributesKey>();
        return map;
    }
}
