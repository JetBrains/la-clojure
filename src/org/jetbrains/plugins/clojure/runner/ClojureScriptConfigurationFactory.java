package org.jetbrains.plugins.clojure.runner;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;

/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Jan 7, 2009
 * Time: 6:02:01 PM
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
public class ClojureScriptConfigurationFactory extends ConfigurationFactory {
  public ClojureScriptConfigurationFactory(ClojureScriptRunConfigurationType scriptRunConfigurationType) {
    super(scriptRunConfigurationType);
  }

  public RunConfiguration createTemplateConfiguration(Project project) {
    return new ClojureScriptRunConfiguration(this, project, "Clojure Script");
  }

}
