package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.ModuleSourceOrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Icons;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.execution.application.ApplicationConfiguration;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.io.File;

/**
 * @author ilyas, Kurt Christensen
 */
public class AddReplAction extends ClojureAction {
  public AddReplAction() {
    getTemplatePresentation().setIcon(Icons.ADD_ICON);
  }

  @Override
  public void update(AnActionEvent e) {
    final Module module = e.getData(DataKeys.MODULE);
    final Presentation pres = e.getPresentation();
    if (module == null) {
      pres.setEnabled(false);
      return;
    }
    pres.setEnabled(true);
    super.update(e);
  }

  public void actionPerformed(AnActionEvent e) {
    final Module module = e.getData(DataKeys.MODULE);
    if (module == null) return;

    ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
    OrderEntry[] entries = moduleRootManager.getOrderEntries();
    Set<VirtualFile> cpVFiles = new HashSet<VirtualFile>();
    for (OrderEntry orderEntry : entries) {
      // Add module sources to classpath
      if (orderEntry instanceof ModuleSourceOrderEntry) {
        cpVFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.SOURCES)));
      } else {
        cpVFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.CLASSES_AND_OUTPUT)));
      }
    }

    final List<String> paths = ContainerUtil.map(cpVFiles, new Function<VirtualFile, String>() {
      public String fun(VirtualFile virtualFile) {
        return virtualFile.getPath();
      }
    });

    getReplToolWindow(e).createRepl(module);
    getReplToolWindow(e).requestFocus();
  }
}
