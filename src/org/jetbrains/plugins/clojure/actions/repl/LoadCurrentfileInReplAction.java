package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.ClojureIcons;

/**
 * @author ilyas
 */
public class LoadCurrentfileInReplAction extends ClojureAction {

  public LoadCurrentfileInReplAction(){
    getTemplatePresentation().setIcon(ClojureIcons.REPL_LOAD);
  }

  @Override
  public void update(AnActionEvent e) {
    final Presentation presentation = e.getPresentation();

    final Editor editor = e.getData(DataKeys.EDITOR);

    if (editor == null) {
      presentation.setEnabled(false);
      return;
    }
    final Project project = editor.getProject();
    if (project == null) {
      presentation.setEnabled(false);
      return;
    }

    final Document document = editor.getDocument();
    final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if (psiFile == null || !(psiFile instanceof ClojureFile)) {
      presentation.setEnabled(false);
      return;
    }

    final VirtualFile virtualFile = psiFile.getVirtualFile();
    if (virtualFile == null) {
      presentation.setEnabled(false);
      return;
    }
    final String filePath = virtualFile.getPath();
    if (filePath == null) {
      presentation.setEnabled(false);
      return;
    }

    presentation.setEnabled(true);
    super.update(e);
  }

  public void actionPerformed(final AnActionEvent e) {
    final Editor editor = e.getData(DataKeys.EDITOR);

    if (editor == null) return;
    final Project project = editor.getProject();
    if (project == null) return;

    final Document document = editor.getDocument();
    final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if (psiFile == null || !(psiFile instanceof ClojureFile)) return;

    final VirtualFile virtualFile = psiFile.getVirtualFile();
    if (virtualFile == null) return;
    final String filePath = virtualFile.getPath();
    if (filePath == null) return;

    PsiDocumentManager.getInstance(project).commitAllDocuments();
    getReplToolWindow(e).writeToCurrentRepl("(load-file \"" + filePath + "\")");
  }
}