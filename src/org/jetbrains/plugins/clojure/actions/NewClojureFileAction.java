package org.jetbrains.plugins.clojure.actions;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.ClojureIcons;

/**
 * @author ilyas
 */
public class NewClojureFileAction extends NewClojureActionBase {

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
    return new PsiElement[]{file};
  }
}
