package org.jetbrains.plugins.clojure.editor;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.maddyhome.idea.copyright.CopyrightProfile;
import com.maddyhome.idea.copyright.psi.UpdateCopyright;
import com.maddyhome.idea.copyright.psi.UpdateCopyrightsProvider;
import com.maddyhome.idea.copyright.psi.UpdatePsiFileCopyright;

/**
 * @author peter
 */
public class ClojureCopyrightProvider extends UpdateCopyrightsProvider {
  @Override
  public UpdateCopyright createInstance(Project project, Module module, VirtualFile virtualFile, FileType fileType, CopyrightProfile copyrightProfile) {
    return new UpdatePsiFileCopyright(project, module, virtualFile, copyrightProfile) {

      @Override
      protected void scanFile() {
        PsiElement first = getFile().getFirstChild();
        PsiElement last = first;
        PsiElement next = first;
        while (next != null) {
          if (next instanceof PsiComment || next instanceof PsiWhiteSpace) {
            next = getNextSibling(next);
          }
          else {
            break;
          }
          last = next;
        }

        if (first != null) {
          checkComments(first, last, true);
        }

      }
    };
  }
}
