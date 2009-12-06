package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.facet.FacetManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.config.ClojureFacet;
import org.jetbrains.plugins.clojure.config.ClojureFacetType;
import org.jetbrains.plugins.clojure.repl.ReplManager;
import org.jetbrains.plugins.clojure.repl.ReplPanel;

/**
 * @author Kurt Christensen, ilyas
 */
public abstract class ClojureReplAction extends AnAction {

  static Module getModule(AnActionEvent e) {
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


  @Nullable
  protected ReplManager getReplManager(final AnActionEvent e) {
    final Project project = PlatformDataKeys.PROJECT.getData(e.getDataContext());
    return project == null ? null : ReplManager.getInstance(project);
  }

  @Nullable
  protected ReplPanel getCurrentRepl(final AnActionEvent e) {
    final ReplManager replManager = getReplManager(e);
    if (replManager != null) {
      return replManager.getCurrentRepl();
    }

    return null;
  }

  protected @Nullable String getFilePath(@NotNull AnActionEvent e) {
    VirtualFile vfile = e.getData(DataKeys.VIRTUAL_FILE);
    return vfile != null ? vfile.getPath() : null;
  }

}
