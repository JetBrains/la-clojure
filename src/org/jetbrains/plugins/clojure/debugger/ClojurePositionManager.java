package org.jetbrains.plugins.clojure.debugger;

import com.intellij.debugger.NoDataException;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Jan 16, 2009
 * Time: 4:17:46 PM
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
public class ClojurePositionManager implements PositionManager {
  private static final Logger LOG = Logger.getInstance("#com.intellij.debugger.engine.PositionManagerImpl");

  private final DebugProcess myDebugProcess;

  public ClojurePositionManager(DebugProcess debugProcess) {
    myDebugProcess = debugProcess;
  }

  public DebugProcess getDebugProcess() {
    return myDebugProcess;
  }

  @NotNull
  public List<Location> locationsOfLine(ReferenceType type, SourcePosition position) throws NoDataException {
    try {
      int line = position.getLine() + 1;
      List<Location> locations = getDebugProcess().getVirtualMachineProxy().versionHigher("1.4")
          ? type.locationsOfLine(DebugProcessImpl.JAVA_STRATUM, null, line)
          : type.locationsOfLine(line);
      if (locations == null || locations.isEmpty()) throw new NoDataException();
      return locations;
    }
    catch (AbsentInformationException e) {
      throw new NoDataException();
    }
  }

  public ClassPrepareRequest createPrepareRequest(final ClassPrepareRequestor requestor, final SourcePosition position)
      throws NoDataException {
    PsiFile file = position.getFile();
    //if (!(file instanceof ClojureElement.File)) return null;
    PsiElement element = file.findElementAt(position.getOffset());
    return myDebugProcess.getRequestsManager().createClassPrepareRequest(requestor, element.getText());
  }

  public SourcePosition getSourcePosition(final Location location) throws NoDataException {
    if (location == null) throw new NoDataException();

    PsiFile psiFile = getPsiFileByLocation(getDebugProcess().getProject(), location);
    if (psiFile == null) throw new NoDataException();

    int lineNumber = location.lineNumber();
    if (lineNumber < 0) throw new NoDataException();
    return SourcePosition.createFromLine(psiFile, lineNumber);
  }

  @NotNull
  public List<ReferenceType> getAllClasses(final SourcePosition position) throws NoDataException {
    //PsiElement element = position.getElementAt();
    //if( element instanceof PsiClass ) {
    //    return myDebugProcess.getVirtualMachineProxy().classesByName(qName);
    //}
    // ArrayList<ReferenceType> l = new ArrayList<ReferenceType>();
    // l.add( position.getElementAt().getReference().get );
    return Collections.EMPTY_LIST;
  }

  @Nullable
  private PsiFile getPsiFileByLocation(final Project project, final Location location) {
    throw null;
  }
}
