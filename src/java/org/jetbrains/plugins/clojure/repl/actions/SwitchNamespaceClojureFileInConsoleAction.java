package org.jetbrains.plugins.clojure.repl.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;

/**
 * @author Alefas
 * @since 18.03.14
 */
public class SwitchNamespaceClojureFileInConsoleAction extends ClojureConsoleActionBase {
  @Override
  public void actionPerformed(AnActionEvent event) {
    Editor editor = event.getData(DataKeys.EDITOR);
    if (editor == null) { return; }

    Project project = editor.getProject();
    if (project == null) { return; }

    VirtualFile vfile = FileDocumentManager.getInstance().getFile(editor.getDocument());

    if (vfile == null) return;

    PsiFile psiFile = PsiManager.getInstance(project).findFile(vfile);
    if (psiFile == null || !(psiFile instanceof ClojureFile)) { return; }

    final String namespace = ((ClojureFile) psiFile).getNamespace();
    if (namespace == null) return;
    final String command = "(ns " + namespace + ")";
    executeCommand(project, command);
  }
}
