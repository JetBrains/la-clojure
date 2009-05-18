package org.jetbrains.plugins.clojure.psi.util;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

/**
 * @author ilyas
 */
public class ClojurePsiElementFactoryImpl extends ClojurePsiElementFactory {

  private final Project myProject;

  public ClojurePsiElementFactoryImpl(Project project) {
    myProject = project;
  }

  private static final String DUMMY = "DUMMY.";


  public ASTNode createSymbolNodeFromText(@NotNull String newName) {
    final String text = "(" + newName + ")";
    final ClojureFile dummyFile = createClojureFileFromText(text);
    final ASTNode newNode = dummyFile.getFirstChild().getFirstChild().getNextSibling().getNode();
    return newNode;
  }

  @Override
  public boolean hasSyntacticalErrors(@NotNull String text) {
    final ClojureFile clojureFile = (ClojureFile) PsiFileFactory.getInstance(getProject()).createFileFromText(DUMMY + ClojureFileType.CLOJURE_FILE_TYPE.getDefaultExtension(), text);
    return hasErrorElement(clojureFile);
  }

  private static boolean hasErrorElement(PsiElement element) {
    if (element instanceof PsiErrorElement) return true;
    for (PsiElement child : element.getChildren()) {
      if (hasErrorElement(child)) return true;
    }
    return false;
  }

  private ClojureFile createClojureFileFromText(String text) {
    return (ClojureFile) PsiFileFactory.getInstance(getProject()).createFileFromText(DUMMY + ClojureFileType.CLOJURE_FILE_TYPE.getDefaultExtension(), text);
  }

  public Project getProject() {
    return myProject;
  }
}
