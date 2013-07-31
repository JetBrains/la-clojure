package org.jetbrains.plugins.clojure.debugger.fragments;

import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.impl.ClojureFileImpl;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.annotations.NonNls;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.LightVirtualFile;

import javax.naming.OperationNotSupportedException;

/**
 * @author ilyas
 */
public class ClojureCodeFragment extends ClojureFileImpl implements JavaCodeFragment{
  private PsiType myThisType;
  private PsiType mySuperType;
  private ExceptionHandler myExceptionHandler;
  private IntentionFilterOwner.IntentionActionsFilter myFilter;
  private GlobalSearchScope myScope;

  public ClojureCodeFragment(Project project, CharSequence text) {
    super(new SingleRootFileViewProvider(PsiManager.getInstance(project),
        new LightVirtualFile(
            "ClojureDebugFile.clj",
            ClojureFileType.CLOJURE_FILE_TYPE,
            text), true));
    ((SingleRootFileViewProvider) getViewProvider()).forceCachedPsi(this);
  }

  public PsiType getThisType() {
    return myThisType;
  }

  public void setThisType(PsiType psiType) {
    myThisType = psiType;
  }

  public PsiType getSuperType() {
    return mySuperType;
  }

  public void setSuperType(PsiType superType) {
    mySuperType = superType;
  }

  public String importsToString() {
    return "";
  }

  public void addImportsFromString(String imports) {
  }

  public void setVisibilityChecker(VisibilityChecker checker) {
  }

  public VisibilityChecker getVisibilityChecker() {
    return VisibilityChecker.EVERYTHING_VISIBLE;
  }

  public void setExceptionHandler(ExceptionHandler checker) {
    myExceptionHandler= checker;
  }

  public ExceptionHandler getExceptionHandler() {
    return myExceptionHandler;
  }

  public boolean importClass(PsiClass aClass) {
    return false;
  }

  public void setIntentionActionsFilter(IntentionFilterOwner.IntentionActionsFilter filter) {
    myFilter = filter;
  }

  public IntentionFilterOwner.IntentionActionsFilter getIntentionActionsFilter() {
    return myFilter;
  }

  public void forceResolveScope(GlobalSearchScope scope) {
    myScope = scope;
  }

  public GlobalSearchScope getForcedResolveScope() {
    return myScope;
  }

  public boolean isClassDefiningFile() {
    return false;
  }

  public String getNamespace() {
    return null; 
  }

  @NotNull
  public ClNs findOrCreateNamespaceElement() throws IncorrectOperationException {
    throw new IncorrectOperationException("creating imports is not supported in this element");
  }

  public String getClassName() {
    return null;
  }

  public PsiElement setClassName(@NonNls String s) {
    return null;
  }

  public void addImportForClass(PsiClass clazz) {
    //todo:
  }
}
