package org.jetbrains.plugins.clojure;

import com.intellij.openapi.components.ApplicationComponent;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Jan 16, 2009
 * Time: 4:34:18 PM
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
public class ClojureLoader implements ApplicationComponent {

  public ClojureLoader() {
  }

  public void initComponent() {
            /*
    ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerAdapter() {
      public void projectOpened(final Project project) {

        DebuggerManager.getInstance(project).registerPositionManagerFactory(new Function<DebugProcess, PositionManager>() {
          public PositionManager fun(DebugProcess debugProcess) {
            return new ClojurePositionManager(debugProcess);
          }
        });
      }
    });
    */
  }

  public void disposeComponent() {
  }

  @NotNull
  public String getComponentName() {
    return "clojure.support.loader";
  }

}
