package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.facet.FacetManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.Icons;
import org.jetbrains.plugins.clojure.config.ClojureFacet;
import org.jetbrains.plugins.clojure.config.ClojureFacetType;
import org.jetbrains.plugins.clojure.ClojureIcons;

/**
 * @author Kurt Christensen, ilyas
 */
public class AddReplAction extends ClojureAction {
  public AddReplAction() {
    getTemplatePresentation().setIcon(ClojureIcons.REPL_ADD);
  }

  @Override
  public void update(AnActionEvent e) {
    final Module m = getModule(e);
    final Presentation presentation = e.getPresentation();
    if (m == null) {
      presentation.setEnabled(false);
      return;
    }
    presentation.setEnabled(true);
    super.update(e);
  }

  public void actionPerformed(AnActionEvent e) {
    Module module = getModule(e);
    if (module != null) {
      getReplToolWindow(e).createRepl(module);
      getReplToolWindow(e).requestFocus();
    }
  }

  private static Module getModule(AnActionEvent e) {
    Module module = e.getData(DataKeys.MODULE);
    if (module == null) {
      final Project project = e.getData(DataKeys.PROJECT);
      if (project == null) return null;
      final Module[] modules = ModuleManager.getInstance(project).getModules();
      if (modules.length == 1) {
        module = modules[0];
      } else {
        for (Module m : modules) {
          final FacetManager manager = FacetManager.getInstance(m);
          final ClojureFacet clFacet = manager.getFacetByType(ClojureFacetType.INSTANCE.getId());
          if (clFacet != null) {
            module = m;
            break;
          }
        }
        if (module == null) {
          module = modules[0];
        }
      }
    }
    return module;
  }
}
