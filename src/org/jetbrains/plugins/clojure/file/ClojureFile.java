package org.jetbrains.plugins.clojure.file;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * User: peter
 * Date: Nov 21, 2008
 * Time: 9:50:00 AM
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
public class ClojureFile extends PsiFileBase {

  @Override
  public String toString() {
    return "ClojureFile";
  }

  public ClojureFile(FileViewProvider viewProvider) {
    super(viewProvider, ClojureFileType.CLOJURE_LANGUAGE);
  }

  @NotNull
  public FileType getFileType() {
    return ClojureFileType.CLOJURE_FILE_TYPE;
  }

  @NotNull
  public String getPackageName() {
    return "";
  }

  public boolean isScript() {
    return true;
  }

  public boolean isJVMDebuggingSupported() {
    return true;
  }
}
