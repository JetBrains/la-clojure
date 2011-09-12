package org.jetbrains.plugins.clojure.psi.util;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

/**
 * @author ilyas
 */
public class ClojurePsiElementFactoryImpl extends ClojurePsiFactory {

  private final Project myProject;

  public ClojurePsiElementFactoryImpl(Project project) {
    myProject = project;
  }

  private static final String DUMMY = "DUMMY.";


  public ASTNode createSymbolNodeFromText(@NotNull String newName) {
    final String text = "(" + newName + ")";
    final ClojureFile dummyFile = createClojureFileFromText(text);
    return dummyFile.getFirstChild().getFirstChild().getNextSibling().getNode();
  }

  @Override
  public boolean hasSyntacticalErrors(@NotNull String text) {
    final ClojureFile clojureFile = (ClojureFile) PsiFileFactory.getInstance(getProject()).createFileFromText(DUMMY + ClojureFileType.CLOJURE_FILE_TYPE.getDefaultExtension(), text);
    return hasErrorElement(clojureFile);
  }

  public String getErrorMessage(@NotNull String text) {
    if (!hasSyntacticalErrors(text)) return null;
    final ClojureFile clojureFile = (ClojureFile) PsiFileFactory.getInstance(getProject()).createFileFromText(DUMMY + ClojureFileType.CLOJURE_FILE_TYPE.getDefaultExtension(), text);
    return getErrorMessageInner(clojureFile);
  }

  private static String getErrorMessageInner(PsiElement element) {
    if (element instanceof PsiErrorElement) {
      return ((PsiErrorElement) element).getErrorDescription();
    }
    for (PsiElement child : element.getChildren()) {
      final String msg = getErrorMessageInner(child);
      if (msg != null) return msg;
    }
    return null;
  }

  private static boolean hasErrorElement(PsiElement element) {
    if (element instanceof PsiErrorElement) return true;
    for (PsiElement child : element.getChildren()) {
      if (hasErrorElement(child)) return true;
    }
    return false;
  }

  @NotNull
  public ClojureFile createClojureFileFromText(@NotNull String text) {
    return (ClojureFile) PsiFileFactory.getInstance(getProject()).createFileFromText(DUMMY + ClojureFileType.CLOJURE_FILE_TYPE.getDefaultExtension(), text);
  }

  @Override
  public ClList createListFromText(@NotNull String text) {
    return (ClList)createSymbolNodeFromText("(" + text + ")");
  }

  @Override
  @Nullable
  public ClList createJavaImportForClass(PsiClass clazz) {
    final String name = clazz.getQualifiedName();
    if (name == null) return null;
    final int lastDot = name.lastIndexOf('.');
    final String text = name.substring(0, lastDot) +
        (lastDot > 0 && lastDot < name.length() ? " " + name.substring(lastDot + 1) : "");
    return createListFromText(text);
  }

  public Project getProject() {
    return myProject;
  }
}