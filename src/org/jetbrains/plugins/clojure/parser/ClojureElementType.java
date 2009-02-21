package org.jetbrains.plugins.clojure.parser;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

/**
 * User: peter
 * Date: Nov 20, 2008
 * Time: 1:52:13 PM
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
public class ClojureElementType extends IElementType {
  public ClojureElementType(String debugName) {
    super(debugName, ClojureFileType.CLOJURE_LANGUAGE);
  }
}
