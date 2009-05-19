package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.ModuleSourceOrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.project.Project;
import com.intellij.util.Icons;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.facet.FacetManager;

import java.util.*;
import java.io.File;

import org.jetbrains.plugins.clojure.config.ClojureFacetType;
import org.jetbrains.plugins.clojure.config.ClojureFacet;

/**
 * @author Kurt Christensen, ilyas
 */
public class AddReplAction extends ClojureAction {
  public AddReplAction() {
    getTemplatePresentation().setIcon(Icons.ADD_ICON);
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
          final Collection<ClojureFacet> clFacet = manager.getFacetsByType(ClojureFacetType.INSTANCE.getId());
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
