package org.jetbrains.plugins.clojure.actions;

import com.intellij.CommonBundle;
import com.intellij.facet.FacetManager;
import com.intellij.ide.IdeView;
import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.utils.ClojureUtils;
import org.jetbrains.plugins.clojure.utils.ClojureNamesUtil;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.config.ClojureFacetType;
import org.jetbrains.plugins.clojure.config.ClojureFacet;

import javax.swing.*;

/**
 * @author ilyas
 */
public abstract class NewClojureActionBase extends CreateElementActionBase {

  @NonNls
  private static final String CLOJURE_EXTENSION = ".clj";

  public NewClojureActionBase(String text, String description, Icon icon) {
    super(text, description, icon);
  }

  @NotNull
  protected final PsiElement[] invokeDialog(final Project project, final PsiDirectory directory) {
    MyInputValidator validator = new MyInputValidator(project, directory);
    Messages.showInputDialog(project, getDialogPrompt(), getDialogTitle(), Messages.getQuestionIcon(), "", validator);

    return validator.getCreatedElements();
  }

  protected abstract String getDialogPrompt();

  protected abstract String getDialogTitle();

  public void update(final AnActionEvent event) {
    super.update(event);
    final Presentation presentation = event.getPresentation();
    final DataContext context = event.getDataContext();
    Module module = (Module) context.getData(DataKeys.MODULE.getName());

    if (module == null) {
      presentation.setEnabled(false);
      presentation.setVisible(false);
      return;
    }

    final FacetManager manager = FacetManager.getInstance(module);
    final ClojureFacet facet = manager.getFacetByType(ClojureFacetType.INSTANCE.getId());

    if (facet == null ||
        !ClojureUtils.isSuitableModule(module) ||
        !presentation.isEnabled() ||
        !isUnderSourceRoots(event)) {
      presentation.setEnabled(false);
      presentation.setVisible(false);
    } else {
      presentation.setEnabled(true);
      presentation.setVisible(true);
    }

  }

  public static boolean isUnderSourceRoots(final AnActionEvent e) {
    final DataContext context = e.getDataContext();
    Module module = (Module) context.getData(DataKeys.MODULE.getName());
    if (!ClojureUtils.isSuitableModule(module)) {
      return false;
    }
    final IdeView view = (IdeView) context.getData(DataKeys.IDE_VIEW.getName());
    final Project project = (Project) context.getData(DataKeys.PROJECT.getName());
    if (view != null && project != null) {
      ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
      PsiDirectory[] dirs = view.getDirectories();
      for (PsiDirectory dir : dirs) {
        PsiPackage aPackage = JavaDirectoryService.getInstance().getPackage(dir);
        if (projectFileIndex.isInSourceContent(dir.getVirtualFile()) && aPackage != null) {
          return true;
        }
      }
    }

    return false;
  }

  @NotNull
  protected PsiElement[] create(String newName, PsiDirectory directory) throws Exception {
    return doCreate(newName, directory);
  }

  @NotNull
  protected abstract PsiElement[] doCreate(String newName, PsiDirectory directory) throws Exception;

  protected static PsiFile createFileFromTemplate(final PsiDirectory directory, String className, @NonNls String templateName,
                                                  @NonNls String... parameters) throws IncorrectOperationException {
    return ClojureTemplatesFactory.createFromTemplate(directory, className, className + CLOJURE_EXTENSION, templateName, parameters);
  }


  protected String getErrorTitle() {
    return CommonBundle.getErrorTitle();
  }

  protected void checkBeforeCreate(String newName, PsiDirectory directory) throws IncorrectOperationException {
    checkCreateFile(directory, newName);
  }

  public static void checkCreateFile(@NotNull PsiDirectory directory, String name) throws IncorrectOperationException {
    if (!ClojureNamesUtil.isIdentifier(name)) {
      throw new IncorrectOperationException(ClojureBundle.message("0.is.not.an.identifier", name));
    }

    String fileName = name + "." + CLOJURE_EXTENSION;
    directory.checkCreateFile(fileName);

    PsiNameHelper helper = JavaPsiFacade.getInstance(directory.getProject()).getNameHelper();
    PsiPackage aPackage = JavaDirectoryService.getInstance().getPackage(directory);
    String qualifiedName = aPackage == null ? null : aPackage.getQualifiedName();
    if (!StringUtil.isEmpty(qualifiedName) && !helper.isQualifiedName(qualifiedName)) {
      throw new IncorrectOperationException("Cannot create class in invalid package: '" + qualifiedName + "'");
    }
  }

}

