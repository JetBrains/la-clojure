package org.jetbrains.plugins.clojure;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileTypeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

/**
 * Created by IntelliJ IDEA.
 * User: merlyn
 * Date: 16-Nov-2008
 * Time: 10:46:56 PM
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
public class ClojureApplication implements ApplicationComponent {
  public void initComponent() {
    ApplicationManager.getApplication().runWriteAction(
        new Runnable() {
          public void run() {
            FileTypeManager.getInstance().registerFileType(new ClojureFileType(), "clj");
          }
        }
    );
  }

  public void disposeComponent() {
    // TODO: insert component disposal logic here
  }

  @NotNull
  public String getComponentName() {
    return "ClojureApplication";
  }
}
