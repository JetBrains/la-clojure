package org.jetbrains.plugins.clojure.debugger;

import com.intellij.debugger.NoDataException;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.CompoundPositionManager;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.impl.DirectoryIndex;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PathUtil;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import com.intellij.util.containers.HashSet;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.ClojureLanguage;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.stubs.ClojureShortNamesCache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    final PsiFile file = position.getFile();
    if (!(file instanceof ClojureFile)) throw new NoDataException();

    final String query = ApplicationManager.getApplication().runReadAction(new Computable<String>() {
      public String compute() {
        final ClojureFile clojureFile = (ClojureFile) file;
        PsiElement element = clojureFile.findElementAt(position.getOffset());

        String nsName = getNameSpaceName(element);
        final String nsPrefix = nsName != null ? nsName + "$" : "user$";

        final ClDef def = PsiTreeUtil.getParentOfType(element, ClDef.class);
        final String name = def == null ? null : def.getName();

        return (nsPrefix + (name != null ? name : "")).replace('-', '_') + "*";
      }
    });

    ClassPrepareRequestor waitRequestor = new MyClassPrepareRequestor(position, requestor);
    final ClassPrepareRequest prepareRequest = myDebugProcess.getRequestsManager().createClassPrepareRequest(waitRequestor, query);
    prepareRequest.addSourceNameFilter(file.getName());
    return prepareRequest;
  }

  private String getNameSpaceName(final PsiElement _element) {
    final Ref<String> stringRef = new Ref<String>(null);
    ApplicationManager.getApplication().runReadAction(new Runnable() {
      public void run() {
        PsiElement element = _element;
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
                final ClSymbol snd = PsiTreeUtil.getNextSiblingOfType(first, ClSymbol.class);
                if (snd != null) {
                  stringRef.set(snd.getText());
                  return;
                }
              }
            }
            element = element.getPrevSibling();
          }
        }
      }
    });
    return stringRef.get();
  }

  @NotNull
  public List<ReferenceType> getAllClasses(final SourcePosition position) throws NoDataException {
    PsiFile file = position.getFile();
    if (!(file instanceof ClojureFile)) throw new NoDataException();
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
    final Ref<PsiFile> result = new Ref<PsiFile>(null);
    ApplicationManager.getApplication().runReadAction(new Runnable() {
      public void run() {
        if (location == null) return;
        final ReferenceType refType = location.declaringType();
        if (refType == null) return;
        final String originalQName = refType.name().replace('/', '.');
        final GlobalSearchScope searchScope = myDebugProcess.getSearchScope();
        int dollar = originalQName.indexOf('$');
        final String qName = dollar >= 0 ? originalQName.substring(0, dollar) : originalQName;
        final ClNs[] nses = ClojureShortNamesCache.getInstance(project).getNsByQualifiedName(qName, searchScope);
        if (nses.length == 1) {
          result.set(nses[0].getContainingFile());
          return;
        }

        String fileName = null;
        try {
          fileName = location.sourceName();
        } catch (AbsentInformationException ignore) {}


        DirectoryIndex directoryIndex = DirectoryIndex.getInstance(project);
        int dotIndex = qName.lastIndexOf(".");
        String packageName = dotIndex >= 0 ? qName.substring(0, dotIndex) : "";
        Query<VirtualFile> query = directoryIndex.getDirectoriesByPackageName(packageName, true);
        if (fileName == null) {
          fileName = dotIndex >= 0 ? qName.substring(dotIndex + 1) : qName;
          fileName += ".clj";
        }


        final String finalFileName = fileName;
        query.forEach(new Processor<VirtualFile>() {
          public boolean process(VirtualFile vDir) {
            VirtualFile vFile = vDir.findChild(finalFileName);
            if (vFile != null) {
              PsiFile psiFile = PsiManager.getInstance(project).findFile(vFile);
              if (psiFile instanceof ClojureFile) {
                result.set(psiFile);
                return false;
              }
            }
            return true;
          }
        });
      }
    });

    return result.get();
  }

  private static class MyClassPrepareRequestor implements ClassPrepareRequestor {
    private final SourcePosition position;
    private final ClassPrepareRequestor requestor;

    public MyClassPrepareRequestor(SourcePosition position, ClassPrepareRequestor requestor) {
      this.position = position;
      this.requestor = requestor;
    }

    public void processClassPrepare(DebugProcess debuggerProcess, ReferenceType referenceType) {
      final CompoundPositionManager positionManager = ((DebugProcessImpl) debuggerProcess).getPositionManager();
      if (positionManager.locationsOfLine(referenceType, position).size() > 0) {
        requestor.processClassPrepare(debuggerProcess, referenceType);
      } else {
        final List<ReferenceType> positionClasses = positionManager.getAllClasses(position);
        if (positionClasses.contains(referenceType)) {
          requestor.processClassPrepare(debuggerProcess, referenceType);
        }
      }
    }
  }

}
