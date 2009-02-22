package org.jetbrains.plugins.clojure.runner;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.junit.RuntimeConfigurationProducer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;

/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Jan 15, 2009
 * Time: 9:01:26 AM
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
public class ClojureScriptRunConfigurationProducer extends RuntimeConfigurationProducer implements Cloneable {

  private PsiElement mySourceElement;

  public ClojureScriptRunConfigurationProducer() {
    super(ClojureScriptRunConfigurationType.getInstance());
  }

  public PsiElement getSourceElement() {
    return mySourceElement;
  }

  protected RunnerAndConfigurationSettingsImpl createConfigurationByElement(final Location location, final ConfigurationContext context) {
    PsiElement element = location.getPsiElement();
    PsiFile file = element.getContainingFile();
    if (file instanceof ClojureFile) {
      mySourceElement = element;
      return ((RunnerAndConfigurationSettingsImpl) ClojureScriptRunConfigurationType.getInstance().createConfigurationByLocation(location));
    }
    return null;
  }

  public int compareTo(final Object o) {
    return PREFERED;
  }
}
