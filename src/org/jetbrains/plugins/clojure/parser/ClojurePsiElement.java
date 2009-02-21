package org.jetbrains.plugins.clojure.parser;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Jan 1, 2009
 * Time: 5:44:13 PM
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
public class ClojurePsiElement extends ASTWrapperPsiElement {

  public ClojurePsiElement(@NotNull ASTNode astNode, String name) {
    super(astNode);
  }

  public ClojurePsiElement(@NotNull ASTNode astNode) {
    this(astNode, null);
  }

  protected ClojurePsiElement getDefinition(String symbol) {
    for (PsiElement prev = getPrevSibling(); prev != null; prev = prev.getPrevSibling()) {
      if (prev instanceof ClojurePsiElement) {
        System.out.println(symbol + " " + prev);
        ClojurePsiElement def = ((ClojurePsiElement) prev).getDefinition(symbol);
        if (def != null)
          return def;
      }
    }
    PsiElement parent = getParent();
    if (parent instanceof ClojurePsiElement) {
      return ((ClojurePsiElement) parent).getDefinition(symbol);
    }
    return null;
  }

}
