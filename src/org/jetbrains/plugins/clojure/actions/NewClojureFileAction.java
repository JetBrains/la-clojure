package org.jetbrains.plugins.clojure.actions;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;

/**
 * @author ilyas
 */
public class NewClojureFileAction extends NewClojureActionBase {

  private static final String DUMMY = "dummy.";

  public NewClojureFileAction() {
    super(ClojureBundle.message("newfile.menu.action.text"),
        ClojureBundle.message("newfile.menu.action.description"),
        ClojureIcons.CLOJURE_ICON_16x16);
  }

  protected String getActionName(PsiDirectory directory, String newName) {
    return ClojureBundle.message("newfile.menu.action.text");
  }

  protected String getDialogPrompt() {
    return ClojureBundle.message("newfile.dlg.prompt");
  }

  protected String getDialogTitle() {
    return ClojureBundle.message("newfile.dlg.title");
  }

  protected String getCommandName() {
    return ClojureBundle.message("newfile.command.name");
  }

  @NotNull
  protected PsiElement[] doCreate(String newName, PsiDirectory directory) throws Exception {
    PsiFile file = createFileFromTemplate(directory, newName, "ClojureFile.clj");
    PsiElement lastChild = file.getLastChild();
    final Project project = directory.getProject();
    if (lastChild != null && lastChild.getNode() != null && lastChild.getNode().getElementType() != ClojureTokenTypes.WHITESPACE) {
      file.add(createWhiteSpace(project));
    }
    file.add(createWhiteSpace(project));
    PsiElement child = file.getLastChild();
    return child != null ? new PsiElement[]{file, child} : new PsiElement[]{file};
  }

  private static PsiElement createWhiteSpace(Project project) {
    PsiFile dummyFile = PsiFileFactory.getInstance(project).createFileFromText(DUMMY + ClojureFileType.CLOJURE_FILE_TYPE.getDefaultExtension(), "\n");
    return dummyFile.getFirstChild();
  }

}
