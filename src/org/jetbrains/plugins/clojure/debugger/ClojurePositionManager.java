package org.jetbrains.plugins.clojure.debugger;

import com.intellij.debugger.NoDataException;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

import java.util.ArrayList;
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
    if (!(file instanceof ClojureFile)) throw new NoDataException();

    final ClojureFile clojureFile = (ClojureFile) file;
    PsiElement element = clojureFile.findElementAt(position.getOffset());

    String nsName = getNameSpaceName(element);
    final String nsPrefix = nsName != null ? nsName + "$" : "user$";
    final ClassPrepareRequest prepareRequest = myDebugProcess.getRequestsManager().createClassPrepareRequest(requestor, nsPrefix + "*");

//    prepareRequest.addSourceNameFilter(file.getName());
    return prepareRequest;
  }

  private String getNameSpaceName(PsiElement element) {
    while (!(element.getParent() instanceof ClojureFile)) {
      element = element.getParent();
    }
    final PsiElement parent = element.getParent();
    if (parent instanceof ClojureFile) {
      while (element != null) {
        if (element instanceof ClList) {
          ClList list = (ClList) element;
          final ClSymbol first = list.getFirstSymbol();
          if (first != null && first.getText().equals("ns")) {
            final ClSymbol snd = org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil.findNextSiblingByClass(first, ClSymbol.class);
            if (snd != null) return snd.getText();
          }
        }
        element = element.getPrevSibling();
      }
    }
    return null;
  }

  @NotNull
  public List<ReferenceType> getAllClasses(final SourcePosition position) throws NoDataException {
    PsiFile file = position.getFile();
    if (!(file instanceof ClojureFile)) throw new NoDataException();

/*
    final ClojureFile clojureFile = (ClojureFile) file;
    PsiElement element = clojureFile.findElementAt(position.getOffset());
    String pattern = ".*";
    final ClDef parent = PsiTreeUtil.getParentOfType(element, ClDef.class);
    if (parent != null) {
      final String name = parent.getDefinedName();
      if (name != null) {
        pattern = ".*$" + name + ".*__\\d+";
      }
    }
*/

    final List<ReferenceType> list = myDebugProcess.getVirtualMachineProxy().allClasses();
    final ArrayList<ReferenceType> result = new ArrayList<ReferenceType>();
    final String fileName = position.getFile().getName();
    for (ReferenceType type : list) {
      try {
        final String name = type.sourceName();
        if (fileName.equals(name)) {
          result.add(type);
        }
      } catch (AbsentInformationException e) {
        //do nothing
      }
    }
    return result;
  }


  public SourcePosition getSourcePosition(final Location location) throws NoDataException {
    if (location == null) throw new NoDataException();

    PsiFile psiFile = getPsiFileByLocation(getDebugProcess().getProject(), location);
    if (!(psiFile instanceof ClojureFile)) throw new NoDataException();

    int lineNumber = location.lineNumber();
    if (lineNumber < 1) throw new NoDataException();
    return SourcePosition.createFromLine(psiFile, lineNumber - 1);
  }

  @Nullable
  private PsiFile getPsiFileByLocation(final Project project, final Location location) {
    try {
      final String path = location.sourcePath();
      if (path == null) return null;
      final ProjectRootManager manager = ProjectRootManager.getInstance(project);
      final VirtualFile[] allFiles = manager.getFilesFromAllModules(OrderRootType.SOURCES);
      for (VirtualFile file : allFiles) {
        final String path2 = file.getPath() + "/";
        final String prefix = StringUtil.commonPrefix(path, path2);
        final String relativePath = StringUtil.trimStart(path, prefix);
        final VirtualFile child = file.findFileByRelativePath(relativePath);
        if (child != null) {
          return PsiManager.getInstance(project).findFile(child);
        }
      }
    } catch (AbsentInformationException e) {
      return null;
    }
    return null;
  }
}
