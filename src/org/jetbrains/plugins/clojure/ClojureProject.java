package org.jetbrains.plugins.clojure;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: 8-Dec-2008
 * Time: 3:45:16 PM
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
public class ClojureProject implements ProjectComponent {
    private Project project;
    private JComponent replWindow;
    private ToolWindow toolWindow;

    public ClojureProject(Project project) {
        this.project = project;
    }

    public void initComponent() {
        replWindow = new JPanel();

//        // TODO is this runWriteAction necessary?
//        ApplicationManager.getApplication().runWriteAction(
//                new Runnable() {
//                    public void run() {
//                        toolWindow = ToolWindowManager.getInstance(project).registerToolWindow("clojure.repl", replWindow, ToolWindowAnchor.BOTTOM);
//                        toolWindow.setTitle("Repl");
////                        toolWindow.setIcon(icon);
//                    }
//                }
//          );
////
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "ClojureProject";
    }

    public void projectOpened() {
        toolWindow.show(null);
    }

    public void projectClosed() {
        toolWindow.hide(null);
    }
}
