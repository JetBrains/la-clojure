package org.jetbrains.plugins.clojure.runner;

import com.intellij.execution.Location;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import static com.intellij.execution.configurations.ConfigurationTypeUtil.findConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Jan 7, 2009
 * Time: 6:00:33 PM
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
public class ClojureScriptRunConfigurationType implements ConfigurationType {


    private ClojureScriptConfigurationFactory myConfigurationFactory;

    public ClojureScriptRunConfigurationType() {
        myConfigurationFactory = new ClojureScriptConfigurationFactory(this);
    }

    // this is the text that appears in the Add New Configurion... list
    public String getDisplayName() {
        return "Clojure Script";
    }

    public String getConfigurationTypeDescription() {
        return "Clojure Script";
    }

    public Icon getIcon() {
        return ClojureIcons.CLOJURE_ICON_16x16;
    }

    @NonNls
    @NotNull
    public String getId() {
        return "ClojureScriptRunConfiguration";
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{myConfigurationFactory};
    }

    public RunnerAndConfigurationSettings createConfigurationByLocation(Location location) {
        return null;
    }

    public boolean isConfigurationByLocation(RunConfiguration configuration, Location location) {
        return true;
    }

    public static ClojureScriptRunConfigurationType getInstance() {
        return findConfigurationType(ClojureScriptRunConfigurationType.class);
    }
}
