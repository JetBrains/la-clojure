package org.jetbrains.plugins.clojure.debugger.fragments;

import org.jetbrains.plugins.clojure.psi.impl.ClojureFileImpl;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.annotations.NonNls;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.LightVirtualFile;

/**
 * @author ilyas
 */
public class ClojureCodeFragment extends ClojureFileImpl implements JavaCodeFragment{
  private PsiType myThisType;
  private PsiType mySuperType;
  private ExceptionHandler myExceptionHandler;
  private IntentionActionsFilter myFilter;
  private GlobalSearchScope myScope;

  public ClojureCodeFragment(Project project, CharSequence text) {
    super(new SingleRootFileViewProvider(PsiManager.getInstance(project),
        new LightVirtualFile(
            "DUMMY.clj",
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

  public void setIntentionActionsFilter(IntentionActionsFilter filter) {
    myFilter = filter;
  }

  public IntentionActionsFilter getIntentionActionsFilter() {
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

  public String getClassName() {
    return null;
  }

  public PsiElement setClassName(@NonNls String s) {
    return null;
  }
}
