package org.jetbrains.plugins.clojure.parser;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;

/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Jan 1, 2009
 * Time: 5:43:42 PM
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
public class Expression extends ClojurePsiElementImpl {
  public Expression(@NotNull final ASTNode node) {
    super(node);
  }

  protected boolean isEmpty(ASTNode[] children) {
    return children == null || children.length < 1;
  }
}