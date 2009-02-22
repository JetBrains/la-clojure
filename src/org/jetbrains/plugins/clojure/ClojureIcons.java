package org.jetbrains.plugins.clojure;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;

/**
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
public interface ClojureIcons {
  @NonNls
  final Icon CLOJURE_ICON_16x16 = IconLoader.findIcon("/org/jetbrains/plugins/clojure/icons/clojure_icon_16x16.png");

  final Icon FUNCTION = IconLoader.findIcon("/org/jetbrains/plugins/clojure/icons/def_tmp.png");
  final Icon METHOD = IconLoader.findIcon("/org/jetbrains/plugins/clojure/icons/meth_tmp.png");
}