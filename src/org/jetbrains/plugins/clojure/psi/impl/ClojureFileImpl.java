package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.impl.source.PsiFileImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;
import org.jetbrains.plugins.clojure.psi.util.ClojureTextUtil;
import org.jetbrains.plugins.clojure.psi.impl.synthetic.ClSyntheticClassImpl;
import org.jetbrains.plugins.clojure.psi.impl.ns.NamespaceUtil;
import org.jetbrains.plugins.clojure.psi.impl.ns.ClSyntheticNamespace;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.clojure.parser.ClojureParser;

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
public class ClojureFileImpl extends PsiFileBase implements ClojureFile {
  private PsiElement myContext = null;
  private PsiClass myClass;
  private boolean myScriptClassInitialized = false;

  @Override
  public String toString() {
    return "ClojureFile";
  }

  public ClojureFileImpl(FileViewProvider viewProvider) {
    super(viewProvider, ClojureFileType.CLOJURE_LANGUAGE);
  }

  @Override
  public PsiElement getContext() {
    if (myContext != null) {
      return myContext;
    }
    return super.getContext();
  }

  public PsiClass getDefinedClass() {
    if (!myScriptClassInitialized) {
      if (isScript()) {
        myClass = new ClSyntheticClassImpl(this);
      }

      myScriptClassInitialized = true;
    }
    return myClass;
  }


  protected PsiFileImpl clone() {
    final ClojureFileImpl clone = (ClojureFileImpl) super.clone();
    clone.myContext = myContext;
    return clone;
  }

  @NotNull
  public FileType getFileType() {
    return ClojureFileType.CLOJURE_FILE_TYPE;
  }

  @NotNull
  public String getPackageName() {
    String ns = getNamespace();
    if (ns == null) {
      return "";
    } else {
      return ClojureTextUtil.getSymbolPrefix(ns);
    }
  }

  public boolean isScript() {
    return true;
  }

  private boolean isWrongElement(PsiElement element) {
    return element == null ||
            (element instanceof LeafPsiElement || element instanceof PsiWhiteSpace || element instanceof PsiComment);
  }

  public PsiElement getFirstNonLeafElement() {
    PsiElement first = getFirstChild();
    while (first != null && isWrongElement(first)) {
      first = first.getNextSibling();
    }
    return first;
  }

  public PsiElement getLastNonLeafElement() {
    PsiElement lastChild = getLastChild();
    while (lastChild != null && isWrongElement(lastChild)) {
      lastChild = lastChild.getPrevSibling();
    }
    return lastChild;
  }

  public <T> T findFirstChildByClass(Class<T> aClass) {
    PsiElement element = getFirstChild();
    while (element != null && !aClass.isInstance(element)) {
      element = element.getNextSibling();
    }
    return (T) element;
  }

  public PsiElement getSecondNonLeafElement() {
    return null;
  }

  public void setContext(PsiElement context) {
    if (context != null) {
      myContext = context;
    }
  }

  public boolean isClassDefiningFile() {
    final ClList ns = ClojurePsiUtil.findFormByName(this, "ns");
    if (ns == null) return false;
    final ClSymbol first = ns.findFirstChildByClass(ClSymbol.class);
    if (first == null) return false;
    final ClSymbol snd = ClojurePsiUtil.findNextSiblingByClass(first, ClSymbol.class);
    if (snd == null) return false;

    return ClojurePsiUtil.findNamespaceKeyByName(ns, ClojurePsiUtil.GEN_CLASS) != null;
  }

  public String getNamespace() {
    final ClList ns = getNamespaceElement();
    if (ns == null) return null;
    final ClSymbol first = ns.findFirstChildByClass(ClSymbol.class);
    if (first == null) return null;
    final ClSymbol snd = ClojurePsiUtil.findNextSiblingByClass(first, ClSymbol.class);
    if (snd == null) return null;

    return snd.getNameString();
  }

  public ClList getNamespaceElement() {
    return ClojurePsiUtil.findFormByNameSet(this, ClojureParser.NS_TOKENS);
  }

  public String getClassName() {
    String namespace = getNamespace();
    if (namespace == null) return null;
    int i = namespace.lastIndexOf(".");
    return i > 0 && i < namespace.length() - 1 ? namespace.substring(i + 1) : namespace;
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {

    //Process precedent read forms
    ResolveUtil.processChildren(this, processor, state, lastParent, place);

    final JavaPsiFacade facade = JavaPsiFacade.getInstance(getProject());

    // Add all java.lang classes
    final PsiPackage javaLang = facade.findPackage(ClojurePsiUtil.JAVA_LANG);
    if (javaLang != null) {
      for (PsiClass clazz : javaLang.getClasses()) {
        if (!ResolveUtil.processElement(processor, clazz)) {
          return false;
        }
      }
    }

    //Add top-level package names
    final PsiPackage rootPackage = JavaPsiFacade.getInstance(getProject()).findPackage("");
    if (rootPackage != null) {
      rootPackage.processDeclarations(processor, state, null, place);
    }

    // Add all symbols from default namespaces
    for (PsiNamedElement element : NamespaceUtil.getDefaultDefinitions(getProject())) {
      if (PsiTreeUtil.findCommonParent(element, place) != element && !ResolveUtil.processElement(processor, element)) {
        return false;
      }
    }

    //todo Add all namespaces, available in project
    for (ClSyntheticNamespace ns : NamespaceUtil.getTopLevelNamespaces(getProject())) {
      if (!ResolveUtil.processElement(processor, ns)) {
        return false;
      }
    }

    return super.processDeclarations(processor, state, lastParent, place);
  }

  public PsiElement setClassName(@NonNls String s) {
    //todo implement me!
    return null;
  }
}
